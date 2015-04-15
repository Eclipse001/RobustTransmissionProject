package assistClass;
import java.util.ArrayList;

/***
 * A class which used to pack all of the required data which the recovery method will use together as an object.
 * @author Xuping Fang
 */
public class InputData {
	
	private ArrayList<ArrayList<Double>> badData;
	private ArrayList<String> bvhHeader;
	
	private int numOfFrames;
	private int numOfNodes;
	
	private double errorRate;
	private int packageCapacity;
	
	private double lostFramePercentage;
	
	/**
	 * Constructor, take all of the parameters passed in and set them as attributes.
	 * @param badData : ArrayList<ArrayList<Double>> object which contains all of the frame data after frame loss simulation.
	 * @param bvhHeader : ArrayList<String> object which contains all of the lines of the BVH header in the list.
	 * @param numOfFrames : Total number of frames in the original BVH file.
	 * @param numOfNodes : Total number of joints data of each frame in the original BVH file.
	 * @param errorRate : The preset error rate before the simulation.
	 * @param packageCapacity : The preset package capacity before the simulation.
	 * @param lostFramePercentage : The real percentage of the lost frame during the simulation.
	 */
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

	/**
	 * Give an independent copy of all of the frame data after frame loss simulation.
	 * @return ArrayList<ArrayList<Double>> object which contains all of the frame data after frame loss simulation.
	 */
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
