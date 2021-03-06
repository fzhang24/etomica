/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.data.histogram;



/* History
 * 09/08/02 (DAK) added set/get methods for xMin, xMax, nBins
 * 08/04/04 (DAK,AJS,NRC) deleted DataSource.X methods; de-implemented DataSource.X.  Dimension-related material removed
 */

import etomica.math.DoubleRange;

/**
 * Histogram implementation with a static x range and number of bins for reweighted data.
 * If an x value is given that falls outside the histogram's x range, the 
 * value is dropped.  The x range and number of bins can be changed explicitly,
 * but doing so will reset the histogram (losing all previously collected data).
 * 
 * e.g. sum [x * w]/ sum [ w ]
 * 		x : measurements
 * 		w : weight
 */
public class HistogramReweightedData implements Histogram, java.io.Serializable {
	protected double deltaX;
	private long sum;
	protected double weightSum;
	protected double[] sums;
	protected double[] histogram;
    protected double xValues[];
    protected double xMin;
    protected double xMax;
    protected int nBins;

    /**
     * Makes a new histogram instance, with the range of x values given by
     * xRange.
     */
    public HistogramReweightedData(DoubleRange xRange) {
        this(100, xRange);
	}
	
    /**
     * Makes a new histogram instance, with the range of x values given by
     * xRange and n bins.
     */
    public HistogramReweightedData(int n, DoubleRange xRange) {
        nBins = n;
        sums = new double[n];
        histogram = new double[n];
        xValues = new double[n];
        setXRange(xRange);
    }
	
	public void reset() {
        //resets all histogram values and counts to zero
	    sum = 0;
	    for(int i=0; i<nBins; i++) {
	        sums[i] = 0;
            xValues[i] = xMin + deltaX * (i+0.5);
	    }
	}
	
    public void addValue(double x, double y) {
        //takes new value and updates histogram
        if(x >= xMin && x <= xMax) {
	        int i = (int)Math.floor(((x-xMin)/deltaX));
	        if(i == nBins){i--;}
            sums[i] += y;
	    }
        sum++;
	    weightSum+=y;
    }
    
    public void addValue(double x) {
    	// This is just to trick Histogram!!! 
    	// Using this method will not cause class to behave like HistogramSimple!
    	addValue(x,1);
    }
    
    public void setXRange(DoubleRange xRange) {
        xMin = xRange.minimum();
        xMax = xRange.maximum();
        deltaX = (xMax-xMin)/nBins;
        reset();
    }
    
    public DoubleRange getXRange() {
        return new DoubleRange(xMin, xMax);
    }
      
    public int getNBins() {
        return nBins;
    }
    
    public void setNBins(int n) {
        this.nBins = n;
        histogram = new double[n];
        xValues = new double[n];
        deltaX = (xMax-xMin)/nBins;
        reset();
    }
    
    public double[] getHistogram() {
        //returns an array representing the present histogram
        if (sum != 0) {
		    for(int i=0; i<nBins; i++) {
		        histogram[i] = sums[i] /(weightSum*deltaX);
		    }
        }
	    return histogram;
    }
    
    public long getCount() {
        return sum;
    }
 
    public double[] xValues() {
        return xValues;
    }

}
