package etomica.normalmode;

import etomica.api.IBox;
import etomica.api.IRandom;
import etomica.api.IVectorMutable;
import etomica.atom.iterator.AtomIterator;
import etomica.atom.iterator.AtomIteratorLeafAtoms;
import etomica.integrator.mcmove.MCMoveBox;
import etomica.integrator.mcmove.MCMoveTracker;
import etomica.normalmode.CoordinateDefinition.BasisCell;

public class MCMoveHarmonic extends MCMoveBox {

    public MCMoveHarmonic(IRandom random) {
        super(null, new MCMoveTracker());
        this.random = random;
        iterator = new AtomIteratorLeafAtoms();
    }

    public void setRejectable(boolean newIsRejectable) {
        isRejectable = newIsRejectable;
    }
    
    public boolean isRejectable() {
        return isRejectable;
    }
    
    public void setCoordinateDefinition(CoordinateDefinition newCoordinateDefinition) {
        coordinateDefinition = newCoordinateDefinition;
        uOld = null;
    }
    
    public CoordinateDefinition getCoordinateDefinition() {
        return coordinateDefinition;
    }

    public void setOmegaSquared(double[][] omega2, double[] coeff) {
        stdDev = new double[omega2.length][omega2[0].length];
        for (int i=0; i<stdDev.length; i++) {
            for (int j=0; j<stdDev[i].length; j++) {
                stdDev[i][j] = Math.sqrt(1.0/(2.0*omega2[i][j]*coeff[i]));
            }
        }
    }
    
    public void setTemperature(double newTemperature) {
        temperature = newTemperature;
    }
    
    public void setWaveVectors(IVectorMutable[] newWaveVectors) {
        waveVectors = newWaveVectors;
    }
    
    public void setWaveVectorCoefficients(double[] newWaveVectorCoefficients) {
        waveVectorCoefficients = newWaveVectorCoefficients;
    }
    
    public void setEigenVectors(double[][][] newEigenVectors) {
        eigenVectors = newEigenVectors;
    }
    
    public void setBox(IBox newBox) {
        super.setBox(newBox);
        iterator.setBox(newBox);

        int coordinateDim = coordinateDefinition.getCoordinateDim();
        u = new double[coordinateDim];

        rRand = new double[waveVectors.length][coordinateDim];
        iRand = new double[waveVectors.length][coordinateDim];
    }

    public AtomIterator affectedAtoms() {
        return iterator;
    }

    public boolean doTrial() {
        iterator.reset();
        int coordinateDim = coordinateDefinition.getCoordinateDim();
        BasisCell[] cells = coordinateDefinition.getBasisCells();

        lastEnergy = 0;
        double sqrtT = Math.sqrt(temperature);

        for (int iVector=0; iVector<waveVectors.length; iVector++) {
            for (int j=0; j<coordinateDim; j++) {
                if (stdDev[iVector][j] == 0) continue;
                //generate real and imaginary parts of random normal-mode coordinate Q
                double realGauss = random.nextGaussian() * sqrtT;
                double imaginaryGauss = random.nextGaussian() * sqrtT;
                rRand[iVector][j] = realGauss * stdDev[iVector][j];
                iRand[iVector][j] = imaginaryGauss * stdDev[iVector][j];
                //XXX we know that if c(k) = 0.5, one of the gaussians will be ignored, but
                // it's hard to know which.  So long as we don't put an atom at the origin
                // (which is true for 1D if c(k)=0.5), it's the real part that will be ignored.
                if (waveVectorCoefficients[iVector] == 0.5) imaginaryGauss = 0;
                lastEnergy += 0.5 * (realGauss*realGauss + imaginaryGauss*imaginaryGauss);
            }
        }
        
        if (isRejectable) {
            if (uOld == null || uOld.length != cells.length) {
                uOld = new double[cells.length][coordinateDim];
            }
        }
        
        for (int iCell = 0; iCell<cells.length; iCell++) {
            if (isRejectable) {
                double[] uNow = coordinateDefinition.calcU(cells[iCell].molecules);
                System.arraycopy(uNow, 0, uOld[iCell], 0, coordinateDim);
            }
            BasisCell cell = cells[iCell];
            for (int i=0; i<coordinateDim; i++) {
                u[i] = 0;
            }
            //loop over wavevectors and sum contribution of each to the generalized coordinates
            for (int iVector=0; iVector<waveVectors.length; iVector++) {
                double kR = waveVectors[iVector].dot(cell.cellPosition);
                double coskR = Math.cos(kR);
                double sinkR = Math.sin(kR);
                
                for (int i=0; i<coordinateDim; i++) {
                    for (int j=0; j<coordinateDim; j++) {
                        u[j] += waveVectorCoefficients[iVector]*eigenVectors[iVector][i][j]*
                                  2.0*(rRand[iVector][i]*coskR - iRand[iVector][i]*sinkR);
                    }
                }
            }
            double normalization = 1/Math.sqrt(cells.length);
            for (int i=0; i<coordinateDim; i++) {
                u[i] *= normalization;
            }
            coordinateDefinition.setToU(cell.molecules, u);
        }
        return true;
    }
    
    public double getA() {
        // return 1 to guarantee success
        return 1;
    }

    public double getB() {
        // return 0 to guarantee success
        return 0;
    }
    
    /**
     * Returns the harmonic energy of the configuration based on the last
     * harmonic move made by this MC Move.
     */
    public double getLastTotalEnergy() {
        return lastEnergy;
    }
    
    public void acceptNotify() {
    }

    public double energyChange() {
        return 0;
    }

    public void rejectNotify() {
        if (!isRejectable) {
            throw new RuntimeException("I didn't keep track of the old positions.  You have to call setRejectable.");
        }
        BasisCell[] cells = coordinateDefinition.getBasisCells();
        for (int iCell = 0; iCell<cells.length; iCell++) {
            BasisCell cell = cells[iCell];
            coordinateDefinition.setToU(cell.molecules, uOld[iCell]);
        }
    }

    private static final long serialVersionUID = 1L;
    protected CoordinateDefinition coordinateDefinition;
    protected final AtomIteratorLeafAtoms iterator;
    private double[][] stdDev;
    private double[][][] eigenVectors;
    private IVectorMutable[] waveVectors;
    private double[] waveVectorCoefficients;
    protected double[] u;
    protected double[][] rRand;
    protected double[][] iRand;
    protected final IRandom random;
    protected double lastEnergy;
    protected double temperature;
    protected boolean isRejectable;
    protected double[][] uOld;
}
