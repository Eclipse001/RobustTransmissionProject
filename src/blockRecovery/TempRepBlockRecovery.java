package blockRecovery;

import java.util.ArrayList;

public class TempRepBlockRecovery {
	
	private ArrayList<Double> reference;
	
	private int blockSize;

	public TempRepBlockRecovery(ArrayList<ArrayList<Double>> frameBlock,boolean isForward){
		
		this.blockSize=frameBlock.size();
		
		if(isForward){
			this.reference=frameBlock.get(0);
		}
		else{
			this.reference=frameBlock.get(this.blockSize-1);
		}
	}
	
	public ArrayList<ArrayList<Double>> doBlockRecovery(){
		
		ArrayList<ArrayList<Double>> recoveredBlock=new ArrayList<ArrayList<Double>>();
		
		for(int index=0;index<this.blockSize;index++){
			recoveredBlock.add(reference);
		}
		
		return recoveredBlock;
	}
}
