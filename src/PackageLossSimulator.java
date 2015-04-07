import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;


public class PackageLossSimulator {
	
	private String originFilePath;
	private String badFilePath;
	
	private int packageCapacity;
	
	private double errorRate;
	
	private ArrayList<String> bvhHeader;
	private ArrayList<ArrayList<Double>> frames;
	
	public PackageLossSimulator(String originFilePath,String badFilePath,int packageCapacity,double errorRate){
		this.originFilePath=originFilePath;
		this.badFilePath=badFilePath;
		
		this.packageCapacity=packageCapacity;
		this.errorRate=errorRate;
		
		this.bvhHeader=new ArrayList<String>();
		this.frames=new ArrayList<ArrayList<Double>>();
	}
	
	private ArrayList<Double> makeCopy(ArrayList<Double> src){
		
		ArrayList<Double> cpy=new ArrayList<Double>();
		
		for(Double currentVal : src){
			cpy.add(currentVal);
		}
		
		return cpy;
	}
	
	private Boolean lossFrame(){
		Random random=new Random();
		if(random.nextDouble()<=this.errorRate){
			return true;
		}
		return false;
	}
	
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
		
		// Simulate package loss and get the content of the new file
		try {
			
			String line;
			
			// Read to the frame location
			while ((line=bufferedReader.readLine()) != null){
				
				this.bvhHeader.add(line);
				
				if (line.contains("Frame Time")){
					break;
				}
			}
			
			ArrayList<Double> currentFrame=new ArrayList<Double>();
			
			while ((line=bufferedReader.readLine()) != null){
				
				String[] currentFrameArray = line.split("\\s+");
				
				for(int index=0;index<currentFrameArray.length;index++){
					currentFrame.add(Double.parseDouble(currentFrameArray[index]));
				}
				
				this.frames.add(this.makeCopy(currentFrame));
				
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
	
	public InputData simulatePackageLoss(){
		
		this.readInData();
		
		int numOfFrames=this.frames.size();
		int numOfNodes=this.frames.get(0).size();
		
		int lostFrameCount=0;
		
		boolean lossingFrame=false;
		
		for(int index=0;index<numOfFrames;index++){
			
			if(index%this.packageCapacity==0){
				
				if(this.lossFrame()){
					lossingFrame=true;
				}
				else{
					lossingFrame=false;
				}
			}
			
			if(lossingFrame){
				frames.set(index,null);
				
				lostFrameCount++;
			}
		}
		
		double lostFramePercentage=((double)lostFrameCount)/((double)numOfFrames);
		
		InputData inputData=new InputData(this.frames,this.bvhHeader,numOfFrames,numOfNodes,this.errorRate,this.packageCapacity,lostFramePercentage);
		
		this.writeBadFile(numOfFrames,numOfNodes);
		
		return inputData;
	}
	
	private void writeBadFile(int numOfFrames,int numOfNodes){
		
		PrintWriter printWriter;
		
		try {
			printWriter = new PrintWriter(this.badFilePath,"UTF-8");
			
			for(String line : this.bvhHeader){
				printWriter.println(line);
			}
			
			for(int index=0;index<numOfFrames;index++){
				
				String line="";
				
				ArrayList<Double> currentFrame=this.frames.get(index);
				
				if(currentFrame!=null){
					
					for(int nodeIndex=0;nodeIndex<numOfNodes;nodeIndex++){
						line+=currentFrame.get(nodeIndex);
						
						if(nodeIndex==numOfNodes-1){
							break;
						}
						line+=" ";
					}
				}
				else{
					line="Lost Frame";
				}
				
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
