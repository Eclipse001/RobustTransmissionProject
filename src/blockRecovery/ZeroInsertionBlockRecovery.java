package blockRecovery;

import java.util.ArrayList;

public class ZeroInsertionBlockRecovery {
	
	private ArrayList<Double> reference;
	
	private int blockSize;

	public ZeroInsertionBlockRecovery(ArrayList<ArrayList<Double>> frameBlock,int numOfNodes){
		
		this.blockSize=frameBlock.size();
		
		this.reference=this.getLineWithAllZeros(numOfNodes);
		
		
	}
	
	public ArrayList<ArrayList<Double>> doBlockRecovery(){
		
		ArrayList<ArrayList<Double>> recoveredBlock=new ArrayList<ArrayList<Double>>();
		
		for(int index=0;index<this.blockSize;index++){
			recoveredBlock.add(reference);
		}
		
		return recoveredBlock;
	}
	
	private ArrayList<Double> getLineWithAllZeros(int numOfNodes){
		
		ArrayList<Double> zeros= new ArrayList<Double>();
		
		for(int index=0;index<numOfNodes;index++){
			zeros.add(0.0);
		}
		
		return zeros;
	}
}
