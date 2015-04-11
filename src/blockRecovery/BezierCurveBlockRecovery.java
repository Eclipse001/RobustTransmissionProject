package blockRecovery;
import java.util.ArrayList;
import java.util.Collections;

import beizerCurveCore.Casteljau;


public class BezierCurveBlockRecovery {
	
	private ArrayList<ArrayList<Double>> frameBlock;
	private ArrayList<ArrayList<Double>> nodeBlock;
	
	private ArrayList<ArrayList<Double>> recoveredNodes;
	
	private int numOfFrames;
	private int numOfNodes;
	
	public BezierCurveBlockRecovery(ArrayList<ArrayList<Double>> badBlockWithFourCP){
		
		this.frameBlock=badBlockWithFourCP;
		this.nodeBlock=new ArrayList<ArrayList<Double>>();
		
		this.numOfFrames=this.frameBlock.size();
		this.numOfNodes=this.frameBlock.get(0).size();
	}
	
	public ArrayList<ArrayList<Double>> runBlockRecovery(){
		
		this.blockMatrixTransformation();
		
		this.recoveredNodes=new ArrayList<ArrayList<Double>>();
		
		
		for(ArrayList<Double> currentNode : this.nodeBlock){
			this.recoveredNodes.add(colRecovery(currentNode));
		}
		
		return this.reoveredNodesMatrixInverseTransformation();
		
	}
	
	private ArrayList<ArrayList<Double>> reoveredNodesMatrixInverseTransformation(){
		
		ArrayList<ArrayList<Double>> recoveredFrameBlock=new ArrayList<ArrayList<Double>>();
		
		int frameNum=recoveredNodes.get(0).size();
		int nodeNum=recoveredNodes.size();
		
		for(int frameIndex=0;frameIndex<frameNum;frameIndex++){
			
			ArrayList<Double> currentFrame=new ArrayList<Double>();
			
			for(int nodeIndex=0;nodeIndex<nodeNum;nodeIndex++){
				
				currentFrame.add(recoveredNodes.get(nodeIndex).get(frameIndex));
				
			}
			
			recoveredFrameBlock.add(currentFrame);
		}
		
		recoveredFrameBlock.remove(0);
		recoveredFrameBlock.remove(0);
		recoveredFrameBlock.remove(recoveredFrameBlock.size()-1);
		recoveredFrameBlock.remove(recoveredFrameBlock.size()-1);
		
		return recoveredFrameBlock;
	}
	
	private void blockMatrixTransformation(){
		
		for(int index=0;index<this.numOfNodes;index++){
			this.nodeBlock.add(new ArrayList<Double>());
		}
		
		for(int index=0;index<this.numOfFrames;index++){
			
			ArrayList<Double> currentFrame=this.frameBlock.get(index);
			
			if(currentFrame==null){
				
				for(int nodeIndex=0;nodeIndex<this.numOfNodes;nodeIndex++){
					
					ArrayList<Double> currentNode=nodeBlock.get(nodeIndex);
					currentNode.add(null);
					nodeBlock.set(nodeIndex,currentNode);
				}
			}
			
			else{
				
				for(int nodeIndex=0;nodeIndex<this.numOfNodes;nodeIndex++){
					
					ArrayList<Double> currentNode=nodeBlock.get(nodeIndex);
					currentNode.add(currentFrame.get(nodeIndex));
					nodeBlock.set(nodeIndex,currentNode);
				}
			}
		}
	}
	
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
				System.out.println("Error! No 2Cp!");
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
}
