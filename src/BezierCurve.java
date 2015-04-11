import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;


public class BezierCurve {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	
	private ArrayList<ArrayList<Double>> recoveredNodes;
	private ArrayList<String> recoveredFrames;
	
	public BezierCurve(InputData inputData,String recoveredFilePath){
		
		this.inputData=inputData;
		this.recoveredFilePath=recoveredFilePath;
		
		this.recoveredNodes=new ArrayList<ArrayList<Double>>();
		
	}
	
	public void runRecovery(){
		
		ArrayList<ArrayList<Double>> nodes=this.matrixTransformation(this.inputData.getBadDataCopy());
		
		
		for(ArrayList<Double> currentNode : nodes){
			this.recoveredNodes.add(colRecovery(currentNode));
		}
		
		this.writeRecoveryFile(this.inputData.getBvhHeader());
	}
	
	private ArrayList<ArrayList<Double>> matrixTransformation(ArrayList<ArrayList<Double>> frames){
		
		ArrayList<ArrayList<Double>> nodes=new ArrayList<ArrayList<Double>>();
		
		for(int index=0;index<this.inputData.getNumOfNodes();index++){
			nodes.add(new ArrayList<Double>());
		}
		
		for(int index=0;index<this.inputData.getNumOfFrames();index++){
			
			ArrayList<Double> currentFrame=frames.get(index);
			
			if(currentFrame==null){
				
				for(int nodeIndex=0;nodeIndex<this.inputData.getNumOfNodes();nodeIndex++){
					
					ArrayList<Double> currentNode=nodes.get(nodeIndex);
					currentNode.add(null);
					nodes.set(nodeIndex,currentNode);
				}
			}
			
			else{
				
				for(int nodeIndex=0;nodeIndex<this.inputData.getNumOfNodes();nodeIndex++){
					
					ArrayList<Double> currentNode=nodes.get(nodeIndex);
					currentNode.add(currentFrame.get(nodeIndex));
					nodes.set(nodeIndex,currentNode);
				}
			}
		}
		
		return nodes;
	}
	
	private void writeRecoveryFile(ArrayList<String> header){
		
		recoveredFrames=new ArrayList<String>();
		
		int frameNum=recoveredNodes.get(0).size();
		int colNum=recoveredNodes.size();
		
		for(int frameIndex=0;frameIndex<frameNum;frameIndex++){
			
			String line="";
			
			for(int colIndex=0;colIndex<colNum;colIndex++){
				
				line+=String.valueOf(recoveredNodes.get(colIndex).get(frameIndex));
				
				if(colIndex+1==colNum){
					break;
				}
				
				line+=" ";
			}
			
			recoveredFrames.add(line);
		}
		
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter(this.recoveredFilePath,"UTF-8");
			
			int lineIndex=0;
			for(lineIndex=0;lineIndex<header.size();lineIndex++){
				printWriter.println(header.get(lineIndex));
			}
			
			for(lineIndex=0;lineIndex<recoveredFrames.size();lineIndex++){
				printWriter.println(recoveredFrames.get(lineIndex));
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
	//---
	
	private ArrayList<Double> colRecovery(ArrayList<Double> col){
		
		ArrayList<ArrayList<Double>> dataFragments = new ArrayList<ArrayList<Double>>();
		
		int index=0;
		
		boolean firstFragNull=false;
		boolean currentNullFlag=false;
		
		ArrayList<Double> currentFrag=new ArrayList<Double>();
		
		for(index=0;index<col.size();index++){
			
			Double currentVal=col.get(index);
			
			if(currentVal==null && index==0){
				firstFragNull=true;
				currentNullFlag=true;
			}
			
			// Fragmentation operation:
			
			if(currentVal!=null && currentNullFlag){
				currentNullFlag=false;
				dataFragments.add(currentFrag);
				currentFrag=new ArrayList<Double>();
			}
			else if(currentVal==null && currentNullFlag==false){
				currentNullFlag=true;
				dataFragments.add(currentFrag);
				currentFrag=new ArrayList<Double>();
			}
			
			currentFrag.add(currentVal);
		}
		dataFragments.add(currentFrag);
		
		//=====================================================
		
		int recoverIndex=0;
		
		if(firstFragNull){
			ArrayList<Double> current=dataFragments.get(0);
			Collections.fill(current,0.0);
			dataFragments.set(0,current);
			recoverIndex=1;
		}
		
		while(recoverIndex+2<dataFragments.size()){
			ArrayList<Double> head=dataFragments.get(recoverIndex);
			ArrayList<Double> middle=dataFragments.get(recoverIndex+1);
			ArrayList<Double> tail=dataFragments.get(recoverIndex+2);
			
			ArrayList<ArrayList<Double>> recoveredSet=null;
			
			if(head.size()>=2 && tail.size()>=2){
				// 4 control points recovery
				recoveredSet=singleRecoveryWithFourCP(head,middle,tail);
			}
			
			else{
				// 2 control points recovery
				recoveredSet=singleRecoveryWithTwoCP(head,middle,tail);
			}
			
			dataFragments.set(recoverIndex,recoveredSet.get(0));
			dataFragments.set(recoverIndex+1,recoveredSet.get(1));
			dataFragments.set(recoverIndex+2,recoveredSet.get(2));
			
			recoverIndex+=2;
		}
		
		
		ArrayList<Double> recoveredCol=new ArrayList<Double>();
		
		for(ArrayList<Double> recoveredFrag : dataFragments){
			if(recoveredFrag.get(0)==null){
				Collections.fill(recoveredFrag,0.0);
			}
			recoveredCol.addAll(recoveredFrag);
		}
		
		return recoveredCol;
	}
	
	private ArrayList<ArrayList<Double>> singleRecoveryWithFourCP(ArrayList<Double> head,ArrayList<Double> middle,ArrayList<Double> tail){
		ArrayList<Double> inputValueSet=new ArrayList<Double>();
		inputValueSet.add(head.get(head.size()-2));
		inputValueSet.add(head.get(head.size()-1));
		inputValueSet.add(tail.get(0));
		inputValueSet.add(tail.get(1));
		
		int n=middle.size()+3;
		
		Double t_diff=1.0/n;
		Double t=1.0/n;
		
		Casteljau casteljau=new Casteljau((Double[])inputValueSet.toArray(new Double[inputValueSet.size()]),4);
		
		head.set(head.size()-1,casteljau.getValue(t));
		t+=t_diff;
		
		int middleIndex;
		for(middleIndex=0;middleIndex<middle.size();middleIndex++){
			middle.set(middleIndex,casteljau.getValue(t));
			t+=t_diff;
		}
		
		tail.set(0,casteljau.getValue(t));
		
		ArrayList<ArrayList<Double>> recoveredSet = new ArrayList<ArrayList<Double>>();
		
		recoveredSet.add(head);
		recoveredSet.add(middle);
		recoveredSet.add(tail);
		
		return recoveredSet;
	}
	
	
	private ArrayList<ArrayList<Double>> singleRecoveryWithTwoCP(ArrayList<Double> head,ArrayList<Double> middle,ArrayList<Double> tail){
		ArrayList<Double> inputValueSet=new ArrayList<Double>();
		inputValueSet.add(head.get(head.size()-1));
		inputValueSet.add(tail.get(0));
		
		int n=middle.size()+1;
		
		Double t_diff=1.0/n;
		Double t=1.0/n;
		
		Casteljau casteljau=new Casteljau(inputValueSet.toArray(new Double[inputValueSet.size()]),2);
		
		int middleIndex;
		for(middleIndex=0;middleIndex<middle.size();middleIndex++){
			middle.set(middleIndex,casteljau.getValue(t));
			t+=t_diff;
		}
		
		ArrayList<ArrayList<Double>> recoveredSet = new ArrayList<ArrayList<Double>>();
		
		recoveredSet.add(head);
		recoveredSet.add(middle);
		recoveredSet.add(tail);
		
		return recoveredSet;
	}
	
}
