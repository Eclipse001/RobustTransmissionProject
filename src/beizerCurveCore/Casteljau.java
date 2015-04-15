package beizerCurveCore;

/** Taken from http://math.stackexchange.com/questions/43947/casteljaus-algorithm-practical-example**/

public class Casteljau {

    private Double[] initialValues;
    
    private int n;

    private double[][] b;

    public Casteljau(Double[] initialValues, int n) {
    	
        this.initialValues = initialValues;
        this.b = new double[n][n];
        this.n = n;
    }

    private void init(Double[] initialValues) {
    	
        for (int i = 0; i < n; i++) {
            b[0][i] = initialValues[i];
        }
    }

    private double evaluate(Double t, Double[] initialValues) {
        init(initialValues);
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                b[j][i] = b[j-1][i] * (1-t) + b[j-1][i+1] * t;
            }
        }
        return(b[n-1][0]);
    }

    public double getValue(Double t) {
        double val = evaluate(t,initialValues);
        return val;
    }
}

