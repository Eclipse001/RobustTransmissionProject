package singleRecoveryMethods;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import assistClass.InputData;
import beizerCurveCore.Casteljau;

/***
 * A recovery method class which uses the bezier curve method to the BVH frame data which experienced frame loss during the transmission simulation.
 * @author Xuping Fang
 */
public class BezierCurve {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	
	private ArrayList<ArrayList<Double>> recoveredNodes;
	private ArrayList<String> recoveredFrames;
	
	/**
	 * Constructor of this recovery method class.
	 * @param inputData : An InputData object which contains all required information of the BVH frame data which experienced frame loss during the transmission simulation.
	 * @param recoveredFilePath : The location of the recovered BVH file will be created. 
	 */
	public BezierCurve(InputData inputData,String recoveredFilePath){
		
		this.inputData=inputData;
		this.recoveredFilePath=recoveredFilePath;
		
		this.recoveredNodes=new ArrayList<ArrayList<Double>>();
		
	}
	
	/**
	 * The main recovery method, transform the frame data (an ArrayList of ArrayList) form the each frame per inner list to each joint per inner list,
	 * then do recovery for each joint, after that transform the recovered data back to the frame per inner list then write the recovered file.
	 */
	public void runRecovery(){
		
		ArrayList<ArrayList<Double>> nodes=this.matrixTransformation(this.inputData.getBadDataCopy());
		
		
		for(ArrayList<Double> currentNode : nodes){
			this.recoveredNodes.add(colRecovery(currentNode));
		}
		
		this.writeRecoveryFile(this.inputData.getBvhHeader());
	}
	
	/**
	 * The function which transform the frame data (an ArrayList of ArrayList) form the each frame per inner list to each joint per list
	 * @param frames : An ArrayList<ArrayList<Double>> type object, which is the original frame data after frame loss.(Each frame per inner list)
	 * @return : An ArrayList<ArrayList<Double>> type object, frame data after transform.(Each joint per inner list)
	 */
	private ArrayList<ArrayList<Double>> matrixTransformation(ArrayList<ArrayList<Double>> frames){
		
		ArrayList<ArrayList<Double>> nodes=new ArrayList<ArrayList<Double>>();
		
		// Allocate the new outer list with the number of the inner list equals to the number of joints of each frame.
		for(int index=0;index<this.inputData.getNumOfNodes();index++){
			nodes.add(new ArrayList<Double>());
		}
		
		// Data transformation: append each separate frame data to the new allocated list in the correct order of joints.
		for(int index=0;index<this.inputData.getNumOfFrames();index++){
			
			ArrayList<Double> currentFrame=frames.get(index);
			
			// If the current frame is a null value, then append each corresponding list a null value.
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
	
	/**
	 * A private method which writes the new BVH file after recovery in to the preset location.
	 * @param header : an ArrayList<String> object which contains all the lines of the BVH header.
	 */
	private void writeRecoveryFile(ArrayList<String> header){
		
		recoveredFrames=new ArrayList<String>();
		
		int frameNum=recoveredNodes.get(0).size();
		int colNum=recoveredNodes.size();
		
		// Transform the recovered data back to the frame per inner list.
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
		
		// Write the recovered file.
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
	/**
	 * A private method which will perform the recovery operation of a single joint.
	 * @param col : An ArrayList<Double> object contains all the frame data of a single joint.
	 * @return : An ArrayList<Double> object contains all the RECOVERED frame data of a single joint.
	 */
	private ArrayList<Double> colRecovery(ArrayList<Double> col){
		
		ArrayList<ArrayList<Double>> dataFragments = new ArrayList<ArrayList<Double>>(); // A list of lists which will contain all of the separate frame blocks of this joint.
		
		int index=0;
		
		boolean firstFragNull=false;	// Becomes true if the first frame block of this joint is null. 
		boolean currentNullFlag=false;	// Becomes true if currently iterate through null values.
		
		ArrayList<Double> currentFrag=new ArrayList<Double>();
		
		// Separate all the frames of this joint in to small blocks(based on the edge of null values and valid values) and append them to the outer list.
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
		
		// If the first frame block of this joint is zero, then use the zero insertion method for the first block.
		if(firstFragNull){
			ArrayList<Double> current=dataFragments.get(0);
			Collections.fill(current,0.0);
			dataFragments.set(0,current);
			recoverIndex=1;
		}
		
		// Do the recovery for all null frame blocks with the previous and next valid block as data reference.
		while(recoverIndex+2<dataFragments.size()){
			ArrayList<Double> head=dataFragments.get(recoverIndex);		// Previous valid block
			ArrayList<Double> middle=dataFragments.get(recoverIndex+1); // Current null block
			ArrayList<Double> tail=dataFragments.get(recoverIndex+2);	// Next valid block
			
			ArrayList<ArrayList<Double>> recoveredSet=null;				// A list of lists used to pack the recovered blocks.
			
			// If we have 2 valid data in the previous valid block and 2 valid data in the next valid block, we will use the recovery method of 4 control points.
			if(head.size()>=2 && tail.size()>=2){
				// 4 control points recovery method
				recoveredSet=singleRecoveryWithFourCP(head,middle,tail);
			}
			
			// If we do not have 2 valid data in the previous valid block and 2 valid data in the next valid block, we will use the recovery method of 2 control points.
			else{
				// 2 control points recovery method
				recoveredSet=singleRecoveryWithTwoCP(head,middle,tail);
			}
			
			// Set the new value of the reference and the recovered blocks.
			dataFragments.set(recoverIndex,recoveredSet.get(0));
			dataFragments.set(recoverIndex+1,recoveredSet.get(1));
			dataFragments.set(recoverIndex+2,recoveredSet.get(2));
			
			recoverIndex+=2;
		}
		
		
		ArrayList<Double> recoveredCol=new ArrayList<Double>();
		
		// Attach all of the blocks to form a recovered frame set of this joint.
		for(ArrayList<Double> recoveredFrag : dataFragments){
			
			// If there's still exists null value block, then it must be the last block, use the zero insertion method for that block.
			if(recoveredFrag.get(0)==null){
				Collections.fill(recoveredFrag,0.0);
			}
			recoveredCol.addAll(recoveredFrag);
		}
		
		return recoveredCol;
	}
	
	/**
	 * The bezier curve recovery method by 4 control points.
	 * @param head : Previous valid frame block, ArrayList<Double> type.
	 * @param middle : Current null frame block, ArrayList<Double> type.
	 * @param tail : Next valid frame block, ArrayList<Double> type.
	 * @return : An ArrayList<ArrayList<Double>> type which contains the previous valid block,current null block,next valid frame block after recover in order.
	 */
	private ArrayList<ArrayList<Double>> singleRecoveryWithFourCP(ArrayList<Double> head,ArrayList<Double> middle,ArrayList<Double> tail){
		
		// Put the last two value of previous valid block and first two values of next valid block in the another list.
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
		
		ArrayList<ArrayList<Double>> recoveredSet = new ArrayList<ArrayList<Double>>();
		
		// pack the three recovered blocks.
		recoveredSet.add(head);
		recoveredSet.add(middle);
		recoveredSet.add(tail);
		
		return recoveredSet;
	}
	
	/**
	 * The bezier curve recovery method by 2 control points.
	 * @param head : Previous valid frame block, ArrayList<Double> type.
	 * @param middle : Current null frame block, ArrayList<Double> type.
	 * @param tail : Next valid frame block, ArrayList<Double> type.
	 * @return : An ArrayList<ArrayList<Double>> type which contains the previous valid block,current null block,next valid frame block after recover in order.
	 */
	private ArrayList<ArrayList<Double>> singleRecoveryWithTwoCP(ArrayList<Double> head,ArrayList<Double> middle,ArrayList<Double> tail){
		
		// Put the last two value of previous valid block and first two values of next valid block in the another list.
		ArrayList<Double> inputValueSet=new ArrayList<Double>();
		inputValueSet.add(head.get(head.size()-1));
		inputValueSet.add(tail.get(0));
		
		// Get the difference of each point along the curve.
		int n=middle.size()+1;
		
		// Calculate the t value difference for each point among the curve.
		Double t_diff=1.0/n;
		
		// Calculate the t value of the first point which its value will be reset among the curve.
		Double t=1.0/n;
		
		// Make a core algorithm class and set the number of control points with their values.
		Casteljau casteljau=new Casteljau(inputValueSet.toArray(new Double[inputValueSet.size()]),2);
		
		// Recovery each null block values corresponding the each t value.
		int middleIndex;
		for(middleIndex=0;middleIndex<middle.size();middleIndex++){
			middle.set(middleIndex,casteljau.getValue(t));
			t+=t_diff;
		}
		
		
		ArrayList<ArrayList<Double>> recoveredSet = new ArrayList<ArrayList<Double>>();
		
		// pack the three recovered blocks.
		recoveredSet.add(head);
		recoveredSet.add(middle);
		recoveredSet.add(tail);
		
		return recoveredSet;
	}
	
}
