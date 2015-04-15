package mainPackage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import assistClass.InputData;

/***
 * The simulator class which simulates the package loss during the transmission of the BVH file's frame data.
 * It reads an BVH file, and produce an InputData object as the input for the recovery method class.
 * It also writes a new BVH file with some of the frame data removed(represents the frame data which experienced package loss during transmission).
 * @author Xuping Fang
 */

public class PackageLossSimulator {
	
	private String originFilePath;
	private String badFilePath;
	
	private int packageCapacity;
	
	private double errorRate;
	
	private ArrayList<String> bvhHeader;
	private ArrayList<ArrayList<Double>> frames;
	
	/**
	 * Constructs a PackageLossSimulator object with the following given parameters set as attributes.
	 * @param originFilePath : The path of the original BVH file.
	 * @param badFilePath : The path of the modified output BVH file with some of the frame data removed after simulation.
	 * @param packageCapacity : An integer value which indicates the capacity of each single package during the transmission simulation. 
	   (i.e: the minimum number of continuous frames which could possibly lost during the transmission.)
	 * @param errorRate : A double value between 0 and 1 which indicates the possibility of the package loss during the transmission simulation.
	 */
	public PackageLossSimulator(String originFilePath,String badFilePath,int packageCapacity,double errorRate){
		this.originFilePath=originFilePath;
		this.badFilePath=badFilePath;
		
		this.packageCapacity=packageCapacity;
		this.errorRate=errorRate;
		
		this.bvhHeader=new ArrayList<String>();
		this.frames=new ArrayList<ArrayList<Double>>();
	}
	
	/**
	 * A private method which takes an ArrayList<Double> type as the input parameter, and returns an independent copy of it.
	 * @param src : An ArrayList<Double> as the list which will be copied.
	 * @return : An independent copy of the input parameter.
	 */
	private ArrayList<Double> makeCopy(ArrayList<Double> src){
		
		ArrayList<Double> cpy=new ArrayList<Double>();
		
		for(Double currentVal : src){
			cpy.add(currentVal);
		}
		
		return cpy;
	}
	
	/**
	 * A private method which return a boolean value which will be used to decide if the simulator will drop the current frame data or not
	 based on the preset error rate.
	 * @return : True if the random double generated in this function is smaller or equal to the error rate, false otherwise.
	 */
	private Boolean lossFrame(){
		Random random=new Random();
		if(random.nextDouble()<=this.errorRate){
			return true;
		}
		return false;
	}
	
	/**
	 * A private method which reads the BVH file line by line, stores the header and the frame data of the file in to the attribute of this object.
	 */
	private void readInData(){
		
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		// Open the BVH file
		try{
			fileReader=new FileReader(new File(this.originFilePath));
			bufferedReader=new BufferedReader(fileReader);
		} 
		catch (FileNotFoundException e){
			e.printStackTrace();
			System.out.println("BVH file not found, exit with error.");
			return;
		}
		
		// Read the file line by line
		try {
			
			String line;
			
			// Read to the frame location while store the file header.
			while ((line=bufferedReader.readLine()) != null){
				
				this.bvhHeader.add(line);
				
				if (line.contains("Frame Time")){
					break;
				}
			}
			
			ArrayList<Double> currentFrame=new ArrayList<Double>();
			
			while ((line=bufferedReader.readLine()) != null){
				
				// Split the current frame data by white spaces and put them in to an array.
				String[] currentFrameArray = line.split("\\s+");
				
				// Transform each single data of the current frame from string to double and put them in an ArrayList in the correct order.
				for(int index=0;index<currentFrameArray.length;index++){
					currentFrame.add(Double.parseDouble(currentFrameArray[index]));
				}
				
				// Make a copy and append the ArrayList which contains the current frame data in to the ArrayList of ArrayList which will be contain data of all frames in the BVH file.
				this.frames.add(this.makeCopy(currentFrame));
				
				// Clear the buffer list
				currentFrame.clear();
			}
			
			// Close file
			fileReader.close();
			bufferedReader.close();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This function will read-in the BVH file, then simulate the package loss by the preset error rate and preset package capacity.
	 * Then write a new BVH file at the specific location which contains the frame data after the package loss simulation.
	 * @return : An InputData object which contains all of the required data which will be passed to the recovery method class.
	 */
	public InputData simulatePackageLoss(){
		
		this.readInData();
		
		int numOfFrames=this.frames.size();
		int numOfNodes=this.frames.get(0).size();
		
		int lostFrameCount=0;
		
		boolean lossingFrame=false;
		
		// Start simulation.
		for(int index=0;index<numOfFrames;index++){
			
			// If a package has been sent, then reset the boolean before send next package.
			if(index%this.packageCapacity==0){
				
				if(this.lossFrame()){
					lossingFrame=true;
				}
				else{
					lossingFrame=false;
				}
			}
			
			// If the boolean indicates the package is lost, then set the current frame data to null.
			if(lossingFrame){
				frames.set(index,null);
				
				lostFrameCount++;
			}
		}
		
		double lostFramePercentage=((double)lostFrameCount)/((double)numOfFrames);
		
		// Create the InputData object which will be passed to the recovery method class.
		InputData inputData=new InputData(this.frames,this.bvhHeader,numOfFrames,numOfNodes,this.errorRate,this.packageCapacity,lostFramePercentage);
		
		// Write the new BVH file.
		this.writeBadFile(numOfFrames,numOfNodes);
		
		return inputData;
	}
	
	/**
	 * A private method which writes the new BVH file which contains the frame data after package loss in to the preset location.
	 * @param numOfFrames : The number of frames in the original BVH file.
	 * @param numOfNodes : The number of joint data of each frame in the original BVH file.
	 */
	private void writeBadFile(int numOfFrames,int numOfNodes){
		
		PrintWriter printWriter;
		
		try {
			printWriter = new PrintWriter(this.badFilePath,"UTF-8");
			
			// Write the header of the file.
			for(String line : this.bvhHeader){
				printWriter.println(line);
			}
			
			// Write each frame of the frame data after package loss.
			for(int index=0;index<numOfFrames;index++){
				
				String line="";
				
				ArrayList<Double> currentFrame=this.frames.get(index);
				
				if(currentFrame!=null){
					
					// Construct a string based on the frame data in the ArrayList.
					for(int nodeIndex=0;nodeIndex<numOfNodes;nodeIndex++){
						line+=currentFrame.get(nodeIndex);
						
						if(nodeIndex==numOfNodes-1){
							break;
						}
						line+=" ";
					}
				}
				else{
					// If the String is null then just write "Lost Frame" instead.
					line="Lost Frame";
				}
				
				// Write the frame to the file
				printWriter.println(line);
				
			}
			
			printWriter.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Bad File written, path: "+this.badFilePath);
	}
}
