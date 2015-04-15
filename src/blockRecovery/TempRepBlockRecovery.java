package blockRecovery;

import java.util.ArrayList;

/**
 * A class which takes in a block of frame data in the type of ArrayList<ArrayList<Double>> and produce the corresponding recovered block as output with the temp replacement method.
 * @author Xuping Fang
 */
public class TempRepBlockRecovery {
	
	private ArrayList<Double> reference;
	
	private int blockSize;

	/**
	 * Constructor.
	 * @param frameBlock : The fetched frame block that need to be recovered.
	 * @param isForward : If this value is true: use the forward temp replacement method, otherwise backward. 
	 */
	public TempRepBlockRecovery(ArrayList<ArrayList<Double>> frameBlock,boolean isForward){
		
		this.blockSize=frameBlock.size();
		
		// Chose reference frame based on the forward or backward method.
		if(isForward){
			this.reference=frameBlock.get(0);
		}
		else{
			this.reference=frameBlock.get(this.blockSize-1);
		}
	}
	
	/**
	 * The function which perform the temp replacement recovery method.
	 * @return : Recovered frame block in the ArrayList<ArrayList<Double>> type.
	 */
	public ArrayList<ArrayList<Double>> doBlockRecovery(){
		
		ArrayList<ArrayList<Double>> recoveredBlock=new ArrayList<ArrayList<Double>>();
		
		// Fill the whole block with the reference frame.
		for(int index=0;index<this.blockSize;index++){
			recoveredBlock.add(reference);
		}
		
		return recoveredBlock;
	}
}
