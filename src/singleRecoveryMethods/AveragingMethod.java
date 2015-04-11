package singleRecoveryMethods;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import assistClass.InputData;


public class AveragingMethod {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	
	public AveragingMethod(InputData inputData,String recoveredFilePath){
		
		this.inputData=inputData;
		this.recoveredFilePath=recoveredFilePath;
	}
	
	public void runRecovery(){
		
		ArrayList<ArrayList<Double>> frames=this.inputData.getBadDataCopy();
		
		ArrayList<Double> prevValidFrame=null;
		
		ArrayList<Integer> badIndexSet=new ArrayList<Integer>();
		
		ArrayList<Double> currentFrame=null;
		
		for(int index=0;index<this.inputData.getNumOfFrames();index++){
			
			currentFrame=frames.get(index);
			
			if(currentFrame==null){
				
				if(prevValidFrame==null){
					frames.set(index,getLineWithAllZeros());
				}
				else{
					badIndexSet.add(index);
				}
				
			}
			else{
				
				if(badIndexSet.size()>0 && prevValidFrame!=null){
					
					ArrayList<ArrayList<Double>> recoveredSet=singleRecovery(badIndexSet,prevValidFrame,currentFrame);
					
					int recoveredIndex=0;
					
					for(int badIndex : badIndexSet){
						
						frames.set(badIndex, recoveredSet.get(recoveredIndex));
						recoveredIndex++;
					}
					
					badIndexSet.clear();
				}
				
				prevValidFrame=this.makeCopy(currentFrame);
			}
		}
		
		if(currentFrame==null){
			for(int badIndex : badIndexSet){
				
				frames.set(badIndex,this.getLineWithAllZeros());
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
	
	private ArrayList<ArrayList<Double>> singleRecovery(ArrayList<Integer> badIndexSet,ArrayList<Double> prev,ArrayList<Double> current){
		
		ArrayList<ArrayList<Double>> recoveredSet= new ArrayList<ArrayList<Double>>();
		
		for(int index=0;index<badIndexSet.size();index++){
			recoveredSet.add(new ArrayList<Double>());
		}
		
			
		for(int index=0;index<this.inputData.getNumOfNodes();index++){
			
			Double upperVal=current.get(index);
			Double lowerVal=prev.get(index);
			
			Double singleDiff=(upperVal-lowerVal)/((double)badIndexSet.size()+1);
			Double baseVal=lowerVal;
			
			for(int recoverIndex=0;recoverIndex<badIndexSet.size();recoverIndex++){
				
				baseVal+=singleDiff;
				
				ArrayList<Double> recoveredFrame=recoveredSet.get(recoverIndex);
				recoveredFrame.add(baseVal);
				recoveredSet.set(recoverIndex,recoveredFrame);
			}
		}
		
		return recoveredSet;
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
