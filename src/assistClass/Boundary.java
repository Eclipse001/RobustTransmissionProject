package assistClass;

/**
 * A helper class provides 2 boundary for a specific recovery method in order to let the combination method to choose which method to use.
 * @author Xuping Fang
 */
public class Boundary {
	
	private int upper;
	private int lower;

	/**
	 * Constructor.
	 * @param lower : Upper boundary
	 * @param upper : Lower boundary
	 */
	public Boundary(int lower,int upper){
		
		this.upper=upper;
		this.lower=lower;
	}
	
	public int getUpperBound(){
		return this.upper;
	}
	
	public int getLowerBound(){
		return this.lower;
	}
}
