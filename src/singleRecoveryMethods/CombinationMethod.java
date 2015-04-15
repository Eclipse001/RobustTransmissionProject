package singleRecoveryMethods;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import blockRecovery.AvgBlockRecovery;
import blockRecovery.BezierCurveBlockRecovery;
import blockRecovery.TempRepBlockRecovery;
import blockRecovery.ZeroInsertionBlockRecovery;
import assistClass.Boundary;
import assistClass.InputData;

/***
 * A recovery method class which uses the combination of different methods to the BVH frame data which experienced frame loss during the transmission simulation.
 * @author Xuping Fang
 */
public class CombinationMethod {
	
	private String recoveredFilePath;
	
	private InputData inputData;
	private ArrayList<ArrayList<Double>> frames;
	private int numOfNodes;
	private int numOfFrames;
	
	private boolean useTempReplacement=true;
	
	private Boundary bezierCurveBoundary;
	private Boundary tempReplacementBoundary;
	
	private final int BEZIER_CURVE = 145;
	private final int AVG_RECOVER = 876;
	private final int TEMP_REP = 1348;
	private final int TEMP_REP_BACK = -1347;

	
	/**
	 * Constructor of this recovery method class.
	 * @param inputData : An InputData object which contains all required information of the BVH frame data which experienced frame loss during the transmission simulation.
	 * @param recoveredFilePath : The location of the recovered BVH file will be created. 
	 * @param useTempReplacement : A boolean value which indicates whether to apply the forward/backward temp replacement method at the last/first frame block if the first/last
	 * frame block is filled with null values. True indicates apply, false indicates to use zero insertion instead.
	 * @param avgBoundary : The boundary value set of the averaging method.
	 * @param bezierCurveBoundary : The boundary value set of bezier curve method.
	 * @param tempReplacementBoundary : The boundary value set of temp replacement method.
	 */
	public CombinationMethod(InputData inputData,String recoveredFilePath,
			boolean useTempReplacement,Boundary avgBoundary,Boundary bezierCurveBoundary,Boundary tempReplacementBoundary){
		
		this.recoveredFilePath=recoveredFilePath;
		
		this.inputData=inputData;
		
		this.frames=inputData.getBadDataCopy();
		
		this.numOfFrames=inputData.getNumOfFrames();
		this.numOfNodes=inputData.getNumOfNodes();
		
		this.useTempReplacement=useTempReplacement;
		
		this.bezierCurveBoundary=bezierCurveBoundary;
		this.tempReplacementBoundary=tempReplacementBoundary;
	}
	
	/**
	 * The main recovery method.
	 * First perform the first/last frame block's recovery if applicable.
	 * Then for the other parts of the frames, pass each null frame block to a recovery method which the number of null frames in that block is in 
	 * the range of the corresponding recovery method's boundary.
	 * Finally write the recovery file.
	 */
	public void doRecovery(){
		
		// Perform the first/last frame block's recovery if applicable.
		this.headTailCheck();
		
		//============================================================================================
		
		ArrayList<Double> currentFrame=null;
		
		boolean passingNullBlock=false;					// A boolean indicates whether the program is looping through null values.
		
		int nullBlockCnt=0;								// Counter of number of null frames in the block.
		
		int beacon=0;									// The index value of the previous valid frame right before a null frame block.
		
		for(int index=0;index<this.numOfFrames;index++){
			
			currentFrame=this.frames.get(index);
			
			if(currentFrame!=null){
				
				// If the program just passed a null frame block, then it is the time to perform recovery for that block.
				if(passingNullBlock){
					
					passingNullBlock=false;
					
					// For the bezier curve recovery method, not only we need to check the number of null frames is the range of the bezier curve's method boundary, we also need to check
					// if there's 4 control points at the beginning and the end of the block, otherwise we cannot use this method.
					if(nullBlockCnt<=this.bezierCurveBoundary.getUpperBound() && nullBlockCnt>=this.bezierCurveBoundary.getLowerBound()
							/*SSA*/ && beacon-1>=0 && index+1<this.numOfFrames && this.frames.get(beacon-1)!=null && this.frames.get(index+1)!=null){
						
						// Create a list of lists fill it with frames that need to be recovered and the reference frame which will be used during bezier curve recovery.
						ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
						
						for(int fillIndex=beacon-1;fillIndex<=index+1;fillIndex++){
							block.add(this.frames.get(fillIndex));
						}
						
						// Call the recovery method.
						this.singleBlockRecovery(beacon-1,index+1,BEZIER_CURVE,block);
					}
					
					// Check the number of null frames is the range of the temp replacement's method boundary.
					else if(nullBlockCnt<=this.tempReplacementBoundary.getUpperBound() && nullBlockCnt>=this.tempReplacementBoundary.getLowerBound()){
						
						// Create a list of lists fill it with frames that need to be recovered and the reference frame which will be used during temp replacement recovery.
						ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
						
						for(int fillIndex=beacon;fillIndex<=index;fillIndex++){
							block.add(this.frames.get(fillIndex));
						}
						// Call the recovery method randomly due to fairness, there's 50 50 percent chance to call the forward/backward temp replacement method.
						if(this.useForward()){
							block.remove(block.size()-1);
							this.singleBlockRecovery(beacon, index-1, TEMP_REP, block);
						}
						else{
							block.remove(0);
							this.singleBlockRecovery(beacon+1, index, TEMP_REP_BACK, block);
						}
					}
					
					// For averaging method, we do not check boundary as this is the only one method left.
					// If the number of null frames in the block is in the range of the bezier curve method but there's not enough control points, then use averaging method instead.
					else{
						
						// Create a list of lists fill it with frames that need to be recovered and the reference frame which will be used during averaging recovery.
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
		
		// Write the recovered file.
		this.writeFile(frames);
		
	}
	
	/**
	 * A function helps program to decide whether to use the forward temp replacement or backward if the temp replacement mthod is chosed.
	 * @return A random boolean value.
	 */
	private Boolean useForward(){
		Random random=new Random();
		if(random.nextDouble()<=0.5){
			return true;
		}
		return false;
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
	 * A private method which performs block recovery based on the chosen recovery method.
	 * @param startIndex : The index of first frame of the frame block that required to pass in to the chosen method.
	 * @param endIndex : The index of last frame of the frame block that required to pass in to the chosen method.
	 * @param methodCode : A integer which indicates which recovery method will be used.
	 * @param block : The fetched frame block that required to pass in to the chosen method.
	 */
	private void singleBlockRecovery(int startIndex,int endIndex,int methodCode,ArrayList<ArrayList<Double>> block){
		
		ArrayList<ArrayList<Double>> recoveredBlock=null;
		
		// Get the recovered block from the chosen recovery method.
		if(methodCode==this.BEZIER_CURVE){
			recoveredBlock=(new BezierCurveBlockRecovery(block).runBlockRecovery());
		}
		
		else if(methodCode==this.AVG_RECOVER){
			recoveredBlock=(new AvgBlockRecovery(block).doBlockRecovery());
		}
		
		else if(methodCode==this.TEMP_REP){
			recoveredBlock=(new TempRepBlockRecovery(block,true).doBlockRecovery());
		}
		
		else if(methodCode==this.TEMP_REP_BACK){
			recoveredBlock=(new TempRepBlockRecovery(block,false).doBlockRecovery());
		}
		
		// Set the recovered frames in to the right position.
		int index=0;
		for(int updateIndex=startIndex;updateIndex<=endIndex;updateIndex++){
			
			this.frames.set(updateIndex,recoveredBlock.get(index));
			index++;
		}
	}
	
	/**
	 * A private method which perform the first/last frame block's recovery if applicable.
	 */
	private void headTailCheck(){
		
		if(frames.get(0)==null){
			headRecovery();
		}
		
		
		if(frames.get(frames.size()-1)==null){
			tailRecovery();
		}
		
	}
	
	/**
	 * This method performs the recovery of the first frame block.
	 */
	private void headRecovery(){
		
		int endIndex=0;
		
		ArrayList<Double> currentFrame=null;
		ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
		
		// Fetch the first frame block from the frame data.
		while(true){
			
			currentFrame=this.frames.get(endIndex);
			block.add(currentFrame);
			
			if(currentFrame!=null){
				break;
			}
			
			endIndex++;
		}
		
		ArrayList<ArrayList<Double>> recoveredBlock=null;
		
		// If temp replacement is chosen, do the recovery.
		if(this.useTempReplacement){
			
			recoveredBlock=(new TempRepBlockRecovery(block,false).doBlockRecovery());
			
		}
		// If temp replacement is not chosen, remove the reference frame do the recovery with zero insertion.
		else{
			endIndex--;
			block.remove(block.size()-1);
			
			recoveredBlock=(new ZeroInsertionBlockRecovery(block,this.numOfNodes).doBlockRecovery());
			
		}
		
		// Reset the recovered frames.
		for(int updateIndex=0;updateIndex<=endIndex;updateIndex++){
			
			this.frames.set(updateIndex,recoveredBlock.get(updateIndex));
		}
	}
	
	/**
	 * This method performs the recovery of the last frame block.
	 */
	private void tailRecovery(){
		
		int startIndex=this.frames.size()-1;
		
		ArrayList<Double> currentFrame=null;
		ArrayList<ArrayList<Double>> block=new ArrayList<ArrayList<Double>>();
		
		// Fetch the first frame block from the frame data.
		while(true){
			
			currentFrame=this.frames.get(startIndex);
			block.add(0,currentFrame);;
			
			if(currentFrame!=null){
				break;
			}
			
			startIndex--;
		}
		
		ArrayList<ArrayList<Double>> recoveredBlock=null;
		
		// If temp replacement is chosen, do the recovery.
		if(this.useTempReplacement){
			
			recoveredBlock=(new TempRepBlockRecovery(block,true).doBlockRecovery());
			
		}
		// If temp replacement is not chosen, remove the reference frame do the recovery with zero insertion.
		else{
			startIndex++;
			block.remove(0);
			
			recoveredBlock=(new ZeroInsertionBlockRecovery(block,this.numOfNodes).doBlockRecovery());
			
		}
		
		int fetchIndex=0;
		
		// Reset the recovered frames.
		for(int updateIndex=startIndex;updateIndex<this.numOfFrames;updateIndex++){
			
			this.frames.set(updateIndex,recoveredBlock.get(fetchIndex));
			
			fetchIndex++;
		}
	}
	
}