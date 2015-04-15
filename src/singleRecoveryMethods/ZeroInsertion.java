package singleRecoveryMethods;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import assistClass.InputData;

/***
 * A recovery method class which uses the zero insertion method to the BVH frame data which experienced frame loss during the transmission simulation.
 * @author Xuping Fang
 */
public class ZeroInsertion {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	
	/**
	 * Constructor of this recovery method class.
	 * @param inputData : An InputData object which contains all required information of the BVH frame data which experienced frame loss during the transmission simulation.
	 * @param recoveredFilePath : The location of the recovered BVH file will be created. 
	 */
	public ZeroInsertion(InputData inputData,String recoveredFilePath){
		
		this.inputData=inputData;
		this.recoveredFilePath=recoveredFilePath;
	}
	
	/**
	 * The main recovery method, replace each null frame with a frame just contains zero.
	 */
	public void runRecovery(){
		
		// Fetch the frame data from the InputData object.
		ArrayList<ArrayList<Double>> frames=this.inputData.getBadDataCopy();

		
		ArrayList<Double> currentFrame=null;
		
		for(int index=0;index<this.inputData.getNumOfFrames();index++){
			
			currentFrame=frames.get(index);
			
			// if the current frame is null, replace it with a frame just contains zero.
			if(currentFrame==null){
				
				frames.set(index,getLineWithAllZeros());
				
			}
		}
		
		// Write the recovered file.
		this.writeFile(frames);
	}
	
	/**
	 * A private method which writes the new BVH file after recovery in to the preset location.
	 * @param frames : an ArrayList<ArrayList<Double>> object which contains the frame data after recovery.
	 */
	private void writeFile(ArrayList<ArrayList<Double>> frames){
		
		PrintWriter printWriter;
		
		try {
			printWriter = new PrintWriter(this.recoveredFilePath,"UTF-8");
			
			// Write the header of the file.
			for(String line : this.inputData.getBvhHeader()){
				printWriter.println(line);
			}
			
			// Write each frame of the frame data after recovery.
			for(int index=0;index<this.inputData.getNumOfFrames();index++){
				
				// Construct a string based on the frame data in the ArrayList.
				String line="";
				
				ArrayList<Double> currentFrame=frames.get(index);
				
					
				for(int nodeIndex=0;nodeIndex<this.inputData.getNumOfNodes();nodeIndex++){
						
					line+=currentFrame.get(nodeIndex);
						
					if(nodeIndex==this.inputData.getNumOfNodes()-1){
						break;
					}
					
					line+=" ";
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
	}
	
	/**
	 * A private method which returns an ArrayList filled with zero which represents the frame with just zero based on the number of joint data of each frame fetched from the InputData.
	 * @return : An ArrayList filled with zero.
	 */
	private ArrayList<Double> getLineWithAllZeros(){
		
		ArrayList<Double> zeros= new ArrayList<Double>();
		
		for(int index=0;index<this.inputData.getNumOfNodes();index++){
			zeros.add(0.0);
		}
		
		return zeros;
	}
}
