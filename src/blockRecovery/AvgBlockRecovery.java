package blockRecovery;

import java.util.ArrayList;

public class AvgBlockRecovery {
	
	private ArrayList<ArrayList<Double>> frameBlock;

	public AvgBlockRecovery(ArrayList<ArrayList<Double>> frameBlock){
		
		this.frameBlock=frameBlock;
	}
	
	public ArrayList<ArrayList<Double>> doBlockRecovery(){
		
		int numOfNodes=this.frameBlock.get(0).size();
		
		double divisor=(double)(this.frameBlock.size()-2+1);
		
		for(int nodeIndex=0;nodeIndex<numOfNodes;nodeIndex++){
			
			Double upperBound=this.frameBlock.get(this.frameBlock.size()-1).get(nodeIndex);
			Double lowerBound=this.frameBlock.get(0).get(nodeIndex);
			double singleDiff=(upperBound-lowerBound)/divisor;
			
			double baseVal=lowerBound;
			
			for(int recoverIndex=1;recoverIndex<this.frameBlock.size()-1;recoverIndex++){
				
				baseVal+=singleDiff;
				
				ArrayList<Double> currentFrame=this.frameBlock.get(recoverIndex);
				
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
		
		return this.frameBlock;
		
	}
}
