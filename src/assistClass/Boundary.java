package assistClass;

public class Boundary {
	
	private int upper;
	private int lower;

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
