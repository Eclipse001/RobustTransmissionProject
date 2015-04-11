package assistClass;
import java.util.ArrayList;


public class InputData {
	
	private ArrayList<ArrayList<Double>> badData;
	private ArrayList<String> bvhHeader;
	
	private int numOfFrames;
	private int numOfNodes;
	
	private double errorRate;
	private int packageCapacity;
	
	private double lostFramePercentage;
	
	public InputData(ArrayList<ArrayList<Double>> badData,ArrayList<String> bvhHeader,int numOfFrames,int numOfNodes,double errorRate,int packageCapacity,double lostFramePercentage){
		
		this.badData=badData;
		this.bvhHeader=bvhHeader;
		
		this.numOfFrames=numOfFrames;
		this.numOfNodes=numOfNodes;
		
		this.errorRate=errorRate;
		this.packageCapacity=packageCapacity;
		
		this.lostFramePercentage=lostFramePercentage;
		
		System.out.println("================ Simulation Finished =================");
		System.out.println("Total number of frames: "+this.numOfFrames);
		System.out.println("Total number of joints: "+this.numOfNodes);
		System.out.println("Pre setted error rate: "+this.errorRate);
		System.out.println("Lost frame percentage: "+this.lostFramePercentage);
		System.out.println("======================================================");
	}

	public ArrayList<ArrayList<Double>> getBadDataCopy() {
		
		ArrayList<ArrayList<Double>> copy=new ArrayList<ArrayList<Double>>();
		
		for(int index=0;index<this.numOfFrames;index++){
			
			ArrayList<Double> currentFrame=this.badData.get(index);
			ArrayList<Double> currentFrameCpy=new ArrayList<Double>();
			
			if(currentFrame==null){
				copy.add(null);
			}
			else{
				for(int nodeIndex=0;nodeIndex<this.numOfNodes;nodeIndex++){
					
					currentFrameCpy.add(currentFrame.get(nodeIndex));
				}
				
				copy.add(currentFrameCpy);
				
			}
		}
		return copy;
	}

	public ArrayList<String> getBvhHeader() {
		return bvhHeader;
	}

	public int getNumOfFrames() {
		return numOfFrames;
	}

	public int getNumOfNodes() {
		return numOfNodes;
	}

	public double getErrorRate() {
		return errorRate;
	}

	public int getPackageCapacity() {
		return packageCapacity;
	}

	public double getLostFramePercentage() {
		return lostFramePercentage;
	}
	
}
