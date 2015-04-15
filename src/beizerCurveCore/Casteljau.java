package beizerCurveCore;

/**
 * Bezier curve core algorithm class
 * Taken from http://math.stackexchange.com/questions/43947/casteljaus-algorithm-practical-example
 */
public class Casteljau {

    private Double[] initialValues;
    
    private int n;

    private double[][] b;

    /**
     * Constructor.
     * @param initialValues : Array of control point values.
     * @param n : Number of control points.
     */
    public Casteljau(Double[] initialValues, int n) {
    	
        this.initialValues = initialValues;
        this.b = new double[n][n];
        this.n = n;
    }

    /**
     * Initialization method
     * @param initialValues :  Array of control point values.
     */
    private void init(Double[] initialValues) {
    	
        for (int i = 0; i < n; i++) {
            b[0][i] = initialValues[i];
        }
    }

    /**
     * Core algorithm of Casteljau
     * @param t	: ratio
     * @param initialValues : control points
     * @return Return the value on the curve
     */
    private double evaluate(Double t, Double[] initialValues) {
        init(initialValues);
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                b[j][i] = b[j-1][i] * (1-t) + b[j-1][i+1] * t;
            }
        }
        return(b[n-1][0]);
    }

    /**
     * Return the value on the bezier corresponding to the ratio t.
     * @param t
     * @return the value on the bezier corresponding to the ratio t
     */
    public double getValue(Double t) {
        double val = evaluate(t,initialValues);
        return val;
    }
}

