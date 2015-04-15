package blockRecovery;

import java.util.ArrayList;

/**
 * A class which takes in a block of frame data in the type of ArrayList<ArrayList<Double>> and produce the corresponding recovered block as output with the averaging recovery method.
 * @author Xuping Fang
 */
public class AvgBlockRecovery {
	
	private ArrayList<ArrayList<Double>> frameBlock;

	/**
	 * Constructor.
	 * @param frameBlock : The fetched frame block that need to be recovered.
	 */
	public AvgBlockRecovery(ArrayList<ArrayList<Double>> frameBlock){
		
		this.frameBlock=frameBlock;
	}
	
	/**
	 * The function which perform the averaging recovery method.
	 * @return : Recovered frame block in the ArrayList<ArrayList<Double>> type.
	 */
	public ArrayList<ArrayList<Double>> doBlockRecovery(){
		
		int numOfNodes=this.frameBlock.get(0).size();
		
		// Get the divisor of the total difference between lower and upper bound.
		double divisor=(double)(this.frameBlock.size()-2+1);
		
		// Perform the recovery for each single joint data separately.
		for(int nodeIndex=0;nodeIndex<numOfNodes;nodeIndex++){
			
			Double upperBound=this.frameBlock.get(this.frameBlock.size()-1).get(nodeIndex);
			Double lowerBound=this.frameBlock.get(0).get(nodeIndex);
			
			// Calculate the single difference between each frame for this joint.
			double singleDiff=(upperBound-lowerBound)/divisor;
			
			double baseVal=lowerBound;
			
			// Put the recovered data in to the correct positions.
			for(int recoverIndex=1;recoverIndex<this.frameBlock.size()-1;recoverIndex++){
				
				baseVal+=singleDiff;
				
				ArrayList<Double> currentFrame=this.frameBlock.get(recoverIndex);
				
				// Construct a new ArrayList and fill in the null values temporary if the current value of this frame is null.
				if(currentFrame==null){
					
					currentFrame=new ArrayList<Double>();
					
					for(int fillIndex=0;fillIndex<numOfNodes;fillIndex++){
						currentFrame.add(null);
					}
				}
				
				currentFrame.set(nodeIndex,baseVal);
				this.frameBlock.set(recoverIndex,currentFrame);
			}
		}
		
		// Return the recovered block.
		return this.frameBlock;
		
	}
}
