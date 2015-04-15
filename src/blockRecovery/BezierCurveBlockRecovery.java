package blockRecovery;
import java.util.ArrayList;

import beizerCurveCore.Casteljau;

/**
 * A class which takes in a block of frame data in the type of ArrayList<ArrayList<Double>> and produce the corresponding recovered block as output with the bezier curve recovery method.
 * @author Xuping Fang
 */
public class BezierCurveBlockRecovery {
	
	private ArrayList<ArrayList<Double>> frameBlock;
	private ArrayList<ArrayList<Double>> nodeBlock;
	
	private ArrayList<ArrayList<Double>> recoveredNodes;
	
	private int numOfFrames;
	private int numOfNodes;
	
	/**
	 * Constructor.
	 * @param badBlockWithFourCP : The fetched frame block that need to be recovered.
	 */
	public BezierCurveBlockRecovery(ArrayList<ArrayList<Double>> badBlockWithFourCP){
		
		this.frameBlock=badBlockWithFourCP;
		this.nodeBlock=new ArrayList<ArrayList<Double>>();
		
		this.numOfFrames=this.frameBlock.size();
		this.numOfNodes=this.frameBlock.get(0).size();
	}
	
	/**
	 * The function which perform the bezier curve recovery method.
	 * @return : Recovered frame block in the ArrayList<ArrayList<Double>> type.
	 */
	public ArrayList<ArrayList<Double>> runBlockRecovery(){
		
		// Transform the frame data (an ArrayList of ArrayList) form the each frame per inner list to each joint per inner list
		this.blockMatrixTransformation();
		
		this.recoveredNodes=new ArrayList<ArrayList<Double>>();
		
		// Do recovery for all frames of a single joint separately.
		for(ArrayList<Double> currentNode : this.nodeBlock){
			this.recoveredNodes.add(colRecovery(currentNode));
		}
		
		// Transform the frame data (an ArrayList of ArrayList) form the each joint per inner list back to each frame per inner list and return.
		return this.reoveredNodesMatrixInverseTransformation();
		
	}
	
	/**
	 * Function which transform the frame data (an ArrayList of ArrayList) form the each joint per inner list back to each frame per inner list
	 * @return : An ArrayList<ArrayList<Double>> type object, frame data after transform.(Each frame per inner list)
	 */
	private ArrayList<ArrayList<Double>> reoveredNodesMatrixInverseTransformation(){
		
		ArrayList<ArrayList<Double>> recoveredFrameBlock=new ArrayList<ArrayList<Double>>();
		
		int frameNum=recoveredNodes.get(0).size();
		int nodeNum=recoveredNodes.size();
		
		// Transform the recovered data back to the frame per inner list.
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
	
	/**
	 * Function which transform the frame data (an ArrayList of ArrayList) form the each frame per inner list to each joint per inner list
	 * @return : An ArrayList<ArrayList<Double>> type object, frame data after transform.(Each joint per inner list)
	 */
	private void blockMatrixTransformation(){
		
		// Allocate the new outer list with the number of the inner list equals to the number of joints of each frame.
		for(int index=0;index<this.numOfNodes;index++){
			this.nodeBlock.add(new ArrayList<Double>());
		}
		
		// Data transformation: append each separate frame data to the new allocated list in the correct order of joints.
		for(int index=0;index<this.numOfFrames;index++){
			
			ArrayList<Double> currentFrame=this.frameBlock.get(index);
			
			// If the current frame is a null value, then append each corresponding list a null value.
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
	
	/**
	 * A private method which will perform the recovery operation of a block of frames of a single joint.
	 * @param colBlock : An ArrayList<Double> object contains the frame data of a single joint.
	 * @return : An ArrayList<Double> object contains all the RECOVERED frame data of a single joint.
	 */
	private ArrayList<Double> colRecovery(ArrayList<Double> colBlock){
		
		ArrayList<Double> head=new ArrayList<Double>();
		ArrayList<Double> middle;
		ArrayList<Double> tail=new ArrayList<Double>();
		
		// Put first 2 control points in to a list.
		for (int index=0; index<2; index++){
			head.add(colBlock.get(index));
		}
		
		// Put last 2 control points in to a list.
		for (int index=colBlock.size()-2; index<colBlock.size(); index++){
			tail.add(colBlock.get(index));
		}
		
		// Remove the control points.
		colBlock.remove(0);
		colBlock.remove(0);
		colBlock.remove(colBlock.size()-1);
		colBlock.remove(colBlock.size()-1);
		
		
		middle=colBlock;
		
		return this.singleRecoveryWithFourCP(head, middle, tail);
	}
	
	/**
	 * The bezier curve BLOCK recovery method by 4 control points.
	 * @param head : First 2 control points stores in a list, ArrayList<Double> type.
	 * @param middle : Current null frame block, ArrayList<Double> type.
	 * @param tail : Last 2 control points stores in a list, ArrayList<Double> type.
	 * @return : An ArrayList<Double> type which contains the recovered frames.
	 */
	private ArrayList<Double> singleRecoveryWithFourCP(ArrayList<Double> head,ArrayList<Double> middle,ArrayList<Double> tail){
		
		ArrayList<Double> inputValueSet=new ArrayList<Double>();
		inputValueSet.add(head.get(head.size()-2));
		inputValueSet.add(head.get(head.size()-1));
		inputValueSet.add(tail.get(0));
		inputValueSet.add(tail.get(1));
		
		// Get the number of points along the curve.
		int n=middle.size()+3;
		
		// Calculate the t value difference for each point among the curve.
		Double t_diff=1.0/n;
		
		// Calculate the t value of the first point which its value will be reset among the curve.
		Double t=1.0/n;
		
		// Make a core algorithm class and set the number of control points with their values.
		Casteljau casteljau=new Casteljau((Double[])inputValueSet.toArray(new Double[inputValueSet.size()]),4);
		
		// NOTE: the last data value of the previous valid block and the first value of the next valid block WILL BE CHANGED if we use the recovery method of 4 control points
		// as we need to create a smooth curve. 
		head.set(head.size()-1,casteljau.getValue(t));
		t+=t_diff;
		
		// Recovery each null block values corresponding the each t value.
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
