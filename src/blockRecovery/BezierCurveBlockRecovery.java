package blockRecovery;
import java.util.ArrayList;

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
		
		// --Should be removed?
		// recoveredFrameBlock.remove(0);
		// recoveredFrameBlock.remove(0);
		// recoveredFrameBlock.remove(recoveredFrameBlock.size()-1);
		// recoveredFrameBlock.remove(recoveredFrameBlock.size()-1);
		
		if(recoveredFrameBlock.size() != this.frameBlock.size()){
			System.out.println("Error");
		}
		if(recoveredFrameBlock.get(0).size() != this.frameBlock.get(0).size()){
			System.out.println("Error");
		}
		
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
	
	private ArrayList<Double> colRecovery(ArrayList<Double> colBlock){
		
		ArrayList<Double> head=new ArrayList<Double>();
		ArrayList<Double> middle;
		ArrayList<Double> tail=new ArrayList<Double>();
		
		for (int index=0; index<2; index++){
			head.add(colBlock.get(index));
		}
		
		for (int index=colBlock.size()-2; index<colBlock.size(); index++){
			tail.add(colBlock.get(index));
		}
		
		colBlock.remove(0);
		colBlock.remove(0);
		colBlock.remove(colBlock.size()-1);
		colBlock.remove(colBlock.size()-1);
		
		
		middle=colBlock;
		
		return this.singleRecoveryWithFourCP(head, middle, tail);
	}
	
	private ArrayList<Double> singleRecoveryWithFourCP(ArrayList<Double> head,ArrayList<Double> middle,ArrayList<Double> tail){
		
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
		
		ArrayList<Double> recoveredBlock = new ArrayList<Double>();
		
		recoveredBlock.addAll(head);
		recoveredBlock.addAll(middle);
		recoveredBlock.addAll(tail);
		
		return recoveredBlock;
	}
}
