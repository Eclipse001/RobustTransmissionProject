package blockRecovery;

import java.util.ArrayList;

/**
 * A class which takes in a block of frame data in the type of ArrayList<ArrayList<Double>> and produce the corresponding recovered block as output with the zero insertion method.
 * @author Xuping Fang
 */
public class ZeroInsertionBlockRecovery {
	
	private ArrayList<Double> reference;
	
	private int blockSize;

	/**
	 * Constructor.
	 * @param frameBlock : The fetched frame block that need to be recovered.
	 * @param numOfNodes : Integer value of how many joint data does each frame contains. 
	 */
	public ZeroInsertionBlockRecovery(ArrayList<ArrayList<Double>> frameBlock,int numOfNodes){
		
		this.blockSize=frameBlock.size();
		
		this.reference=this.getLineWithAllZeros(numOfNodes);
		
		
	}
	
	/**
	 * The function which perform the zero insertion recovery method.
	 * @return : Recovered frame block in the ArrayList<ArrayList<Double>> type.
	 */
	public ArrayList<ArrayList<Double>> doBlockRecovery(){
		
		ArrayList<ArrayList<Double>> recoveredBlock=new ArrayList<ArrayList<Double>>();
		
		for(int index=0;index<this.blockSize;index++){
			recoveredBlock.add(reference);
		}
		
		return recoveredBlock;
	}
	
	/**
	 * Private method which makes a ArrayList wfilled with zeros
	 * @param numOfNodes : Integer value of how many joint data does each frame contains. 
	 * @return
	 */
	private ArrayList<Double> getLineWithAllZeros(int numOfNodes){
		
		ArrayList<Double> zeros= new ArrayList<Double>();
		
		for(int index=0;index<numOfNodes;index++){
			zeros.add(0.0);
		}
		
		return zeros;
	}
}
