package singleRecoveryMethods;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import assistClass.InputData;

/***
 * A recovery method class which uses the averaging method to the BVH frame data which experienced frame loss during the transmission simulation.
 * @author Xuping Fang
 */
public class AveragingMethod {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	
	/**
	 * Constructor of this recovery method class.
	 * @param inputData : An InputData object which contains all required information of the BVH frame data which experienced frame loss during the transmission simulation.
	 * @param recoveredFilePath : The location of the recovered BVH file will be created. 
	 */
	public AveragingMethod(InputData inputData,String recoveredFilePath){
		
		this.inputData=inputData;
		this.recoveredFilePath=recoveredFilePath;
	}
	
	/**
	 * The main recovery method, replace each null frame with a frame generated based on averaging recovery method.
	 */
	public void runRecovery(){
		
		ArrayList<ArrayList<Double>> frames=this.inputData.getBadDataCopy();
		
		ArrayList<Double> prevValidFrame=null;						// Previous valid frame.
		
		ArrayList<Integer> badIndexSet=new ArrayList<Integer>();	// The list of indexes which contains the indexes of a block of frames with null values.
		
		ArrayList<Double> currentFrame=null;
		
		for(int index=0;index<this.inputData.getNumOfFrames();index++){
			
			currentFrame=frames.get(index);
			
			if(currentFrame==null){
				
				// Both current frame and previous valid frame is null indicates the first block of frame is null, we use zero insertion here.
				if(prevValidFrame==null){
					frames.set(index,getLineWithAllZeros());
				}
				else{
					// record the current frame index otherwise
					badIndexSet.add(index);
				}
				
			}
			else{
				// The current frame is not null and null frame index set is not zero indicates we shall do recovery at this time.
				if(badIndexSet.size()>0 && prevValidFrame!=null){
					
					// Take the recovered frame set from the single recovery method.
					ArrayList<ArrayList<Double>> recoveredSet=singleRecovery(badIndexSet,prevValidFrame,currentFrame);
					
					// Put the recovered frames in to the correct positions.
					int recoveredIndex=0;
					
					for(int badIndex : badIndexSet){
						
						frames.set(badIndex, recoveredSet.get(recoveredIndex));
						recoveredIndex++;
					}
					
					// Clear the null frame index set.
					badIndexSet.clear();
				}
				
				// Make the current frame as the previous valid frame before loop to the next frame.
				prevValidFrame=this.makeCopy(currentFrame);
			}
		}
		
		// If we found the current frame is null after loop though all frame s this indicates the last block of the frame is null, we use zero insertion here.
		if(currentFrame==null){
			for(int badIndex : badIndexSet){
				
				frames.set(badIndex,this.getLineWithAllZeros());
			}
		}
		
		// Write the recovery file.
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
	 * Core function of this recovery method, perform an averaging method recovery for a block of frames.
	 * @param badIndexSet: The index set of null value frames.
	 * @param prev: The frame data which contains the valid frame before the null frame block.
	 * @param current: The frame data which contains the valid frame after the null frame block.
	 * @return : ArrayList<ArrayList<Double>> type which is the set of the null frame block after recovery.
	 */
	private ArrayList<ArrayList<Double>> singleRecovery(ArrayList<Integer> badIndexSet,ArrayList<Double> prev,ArrayList<Double> current){
		
		ArrayList<ArrayList<Double>> recoveredSet= new ArrayList<ArrayList<Double>>();
		
		// Allocate the recovered frame block with same the number of null frames.
		for(int index=0;index<badIndexSet.size();index++){
			recoveredSet.add(new ArrayList<Double>());
		}
		
		// Do the recovery for each joint of the frame block separately.
		
		for(int index=0;index<this.inputData.getNumOfNodes();index++){
			
			Double upperVal=current.get(index);
			Double lowerVal=prev.get(index);
			
			// Calculate the average difference between each null frame.
			Double singleDiff=(upperVal-lowerVal)/((double)badIndexSet.size()+1);
			Double baseVal=lowerVal;
			
			// Based on the calculated difference, perform the recovery.
			for(int recoverIndex=0;recoverIndex<badIndexSet.size();recoverIndex++){
				
				baseVal+=singleDiff;
				
				ArrayList<Double> recoveredFrame=recoveredSet.get(recoverIndex);
				recoveredFrame.add(baseVal);
				recoveredSet.set(recoverIndex,recoveredFrame);
			}
		}
		
		return recoveredSet;
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
