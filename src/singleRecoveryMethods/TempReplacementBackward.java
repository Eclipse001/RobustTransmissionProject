package singleRecoveryMethods;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import assistClass.InputData;


public class TempReplacementBackward {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	
	public TempReplacementBackward(InputData inputData,String recoveredFilePath){
		
		this.inputData=inputData;
		this.recoveredFilePath=recoveredFilePath;
	}
	
	public void runRecovery(){
		
		ArrayList<ArrayList<Double>> frames=this.inputData.getBadDataCopy();
		
		ArrayList<Double> prevValidFrame=null;
		
		ArrayList<Double> currentFrame=null;
		
		for(int index=this.inputData.getNumOfFrames()-1;index>=0;index--){
			
			currentFrame=frames.get(index);
			
			if(currentFrame==null){
				
				if(prevValidFrame==null){
					frames.set(index,getLineWithAllZeros());
				}
				else{
					frames.set(index,prevValidFrame);
				}
				
			}
			else{
				
				prevValidFrame=this.makeCopy(currentFrame);
			}
		}
		
		this.writeBadFile(frames);
	}
	
	private void writeBadFile(ArrayList<ArrayList<Double>> frames){
		
		PrintWriter printWriter;
		
		try {
			printWriter = new PrintWriter(this.recoveredFilePath,"UTF-8");
			
			for(String line : this.inputData.getBvhHeader()){
				printWriter.println(line);
			}
			
			for(int index=0;index<this.inputData.getNumOfFrames();index++){
				
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
	
	private ArrayList<Double> makeCopy(ArrayList<Double> src){
		
		ArrayList<Double> cpy=new ArrayList<Double>();
		
		for(Double currentVal : src){
			cpy.add(currentVal);
		}
		
		return cpy;
	}
	
	private ArrayList<Double> getLineWithAllZeros(){
		
		ArrayList<Double> zeros= new ArrayList<Double>();
		
		for(int index=0;index<this.inputData.getNumOfNodes();index++){
			zeros.add(0.0);
		}
		
		return zeros;
	}
}