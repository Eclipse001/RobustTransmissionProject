package singleRecoveryMethods;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import blockRecovery.AvgBlockRecovery;
import blockRecovery.BezierCurveBlockRecovery;
import blockRecovery.TempRepBlockRecovery;
import blockRecovery.ZeroInsertionBlockRecovery;
import assistClass.InputData;

public class CombinationMethod {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	private ArrayList<ArrayList<Double>> frames;
	private int numOfNodes;
	private int numOfFrames;
	
	private boolean useTempReplacement=true;
	private int bound;
	
	private final int BEZIER_CURVE = 145;
	private final int AVG_RECOVER = 876;

	public CombinationMethod(InputData inputData,String recoveredFilePath,boolean useTempReplacement,int bound){
		
		this.recoveredFilePath=recoveredFilePath;
		
		this.inputData=inputData;
		
		this.frames=inputData.getBadDataCopy();
		
		this.numOfFrames=inputData.getNumOfFrames();
		this.numOfNodes=inputData.getNumOfNodes();
		
		this.bound=bound;
		
		this.useTempReplacement=useTempReplacement;
	}
	
	public void doRecovery(){
		
		this.headTailCheck();
		
		//============================================================================================
		
		ArrayList<Double> currentFrame=null;
		
		boolean passingNullBlock=false;
		
		int nullBlockCnt=0;
		
		int beacon=0;
		
		for(int index=0;index<this.numOfFrames;index++){
			
			currentFrame=this.frames.get(index);
			
			if(currentFrame!=null){
				
				if(passingNullBlock){
					
					passingNullBlock=false;
					
					if(nullBlockCnt>this.bound && beacon-1>=0 && index+1<this.numOfFrames && this.frames.get(beacon-1)!=null && this.frames.get(index+1)!=null){
						
						ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
						
						for(int fillIndex=beacon-1;fillIndex<=index+1;fillIndex++){
							block.add(this.frames.get(fillIndex));
						}
						
						this.singleBlockRecovery(beacon-1,index+1,BEZIER_CURVE,block);
					}
					else{
						
						ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
						
						for(int fillIndex=beacon;fillIndex<=index;fillIndex++){
							block.add(this.frames.get(fillIndex));
						}
						
						this.singleBlockRecovery(beacon,index,AVG_RECOVER,block);
					}
					
					nullBlockCnt=0;
				}
			}
			
			else{
				
				if(passingNullBlock==false){
					
					passingNullBlock=true;
					
					beacon=index-1;
				}
				
				nullBlockCnt++;
			}
		}
		
		this.writeFile(frames);
		
	}
	
	private void writeFile(ArrayList<ArrayList<Double>> frames){
		
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
	

	
	private void singleBlockRecovery(int startIndex,int endIndex,int methodCode,ArrayList<ArrayList<Double>> block){
		
		ArrayList<ArrayList<Double>> recoveredBlock=null;
		
		if(methodCode==this.BEZIER_CURVE){
			recoveredBlock=(new BezierCurveBlockRecovery(block).runBlockRecovery());
		}
		
		else if(methodCode==this.AVG_RECOVER){
			recoveredBlock=(new AvgBlockRecovery(block).doBlockRecovery());
		}
		
		int index=0;
		for(int updateIndex=startIndex;updateIndex<=endIndex;updateIndex++){
			
			this.frames.set(updateIndex,recoveredBlock.get(index));
			index++;
		}
	}
	
	
	private void headTailCheck(){
		
		if(frames.get(0)==null){
			headRecovery();
		}
		
		
		if(frames.get(frames.size()-1)==null){
			tailRecovery();
		}
		
	}
	
	private void headRecovery(){
		
		int endIndex=0;
		
		ArrayList<Double> currentFrame=null;
		ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
		
		while(true){
			
			currentFrame=this.frames.get(endIndex);
			block.add(currentFrame);
			
			if(currentFrame!=null){
				break;
			}
			
			endIndex++;
		}
		
		ArrayList<ArrayList<Double>> recoveredBlock=null;
		
		if(this.useTempReplacement){
			
			recoveredBlock=(new TempRepBlockRecovery(block,false).doBlockRecovery());
			
		}
		else{
			endIndex--;
			block.remove(block.size()-1);
			
			recoveredBlock=(new ZeroInsertionBlockRecovery(block,this.numOfNodes).doBlockRecovery());
			
		}
		
		for(int updateIndex=0;updateIndex<=endIndex;updateIndex++){
			
			this.frames.set(updateIndex,recoveredBlock.get(updateIndex));
		}
	}
	
	private void tailRecovery(){
		
		int startIndex=this.frames.size()-1;
		
		ArrayList<Double> currentFrame=null;
		ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
		
		while(true){
			
			currentFrame=this.frames.get(startIndex);
			block.add(0,currentFrame);;
			
			if(currentFrame!=null){
				break;
			}
			
			startIndex--;
		}
		
		ArrayList<ArrayList<Double>> recoveredBlock=null;
		
		if(this.useTempReplacement){
			
			recoveredBlock=(new TempRepBlockRecovery(block,true).doBlockRecovery());
			
		}
		else{
			startIndex++;
			block.remove(0);
			
			recoveredBlock=(new ZeroInsertionBlockRecovery(block,this.numOfNodes).doBlockRecovery());
			
		}
		
		int fetchIndex=0;
		
		for(int updateIndex=startIndex;updateIndex<this.numOfFrames;updateIndex++){
			
			this.frames.set(updateIndex,recoveredBlock.get(fetchIndex));
			
			fetchIndex++;
		}
	}
	
}