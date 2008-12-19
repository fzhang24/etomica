package etomica.normalmode;

import java.io.Serializable;

import etomica.api.IBox;
import etomica.api.ISpecies;
import etomica.api.IVectorMutable;
import etomica.api.IVector;
import etomica.box.Box;
import etomica.lattice.crystal.Primitive;
import etomica.lattice.crystal.PrimitiveOrthorhombicHexagonal;
import etomica.simulation.Simulation;
import etomica.space.ISpace;
import etomica.space.Space;
import etomica.space2d.Space2D;
import etomica.space2d.Vector2D;
import etomica.species.SpeciesSpheresMono;

/**
 * Wave vector factory that returns wave vectors appropriate for a box with 
 * a single-atom basis.  The box shape need not be rectangular so long as it
 * matches the primitive's shape.
 *
 * @author Andrew Schultz
 */
public class WaveVectorFactory2D implements WaveVectorFactory, Serializable {

    public WaveVectorFactory2D(Primitive primitive, ISpace _space) {
        this.primitive = primitive;
        this.space = _space;
    }
    
    public void makeWaveVectors(IBox box) {
        // If we weren't given wave vectors, determine them from the box boundary and primitve
        // assume 1-molecule basis and matchup betwen the box and the primitive
    
        double[] d = primitive.getSize();
        int[] numCells = new int[space.D()];
        IVector[] reciprocals =  primitive.makeReciprocal().vectors();
        IVectorMutable[] waveVectorBasis = new IVectorMutable[reciprocals.length];
        
        for (int i=0; i<space.D(); i++) {
            waveVectorBasis[i] = space.makeVector();
            waveVectorBasis[i].E(reciprocals[i]);
            numCells[i] = (int)Math.round(box.getBoundary().getDimensions().x(i) / (d[i]));
            waveVectorBasis[i].TE(1.0/numCells[i]);
        }
        
        
        int[] kMin = new int[space.D()];
        int[] kMax= new int[space.D()];
        for (int i=0; i<kMax.length; i++) {
            kMin[i] = -(numCells[i]-1)/2;
            kMax[i] = numCells[i]/2;
        }
        int[][] waveVectorIndices = new int[2*kMax[0]+1][2*kMax[1]+1];
        int count = 0;
        int[] idx = new int[2];
        // if we have an odd number of cells, we flip the first non-zero component
        // positive.  If we have an even number of cells, we do so, but ignore any
        // component equal to the max.  And, for even number of cells, if we flip,
        // we re-flip any component that flips to -max. 
        
        boolean [] flip2 = new boolean [2];
        
        for (int i=0; i<2; i++){
        	flip2[i] = numCells[i] %2 == 0;
        }
        
        // this will find N-1 vectors.  Some of them have negatives 
        // within the set others do not.  If its negative is within the set, 
        // exclude the negative, but remember it was there -- they will have 
        // coefficients of '1' while the ones without a negative in the set 
        // will have coefficients of '0.5'.
        int [] k = new int [2];
        
        for ( k[0] = kMin[0]; k[0] < kMax[0]+1; k[0]++) {
            for ( k[1] = kMin[1]; k[1] < kMax[1]+1; k[1]++) {
//                    if (kx == 0 && ky == 0 && kz == 0) continue;
                    for (int i=0; i<2; i++) {
                        idx[i] = kMax[i];
                    }
                    
                    boolean flip = false;
outer:              for (int i=0; i<2; i++){
                    	for (int j=0; j<i; j++){
                    		if (k[j] > 0 && (!flip2[j] || k[j] < kMax[j] )){
                    			break outer;
                    		}
                    	}
                    	if (k[i] < 0){
                    		flip = true;
                    		break;
                    	}
                    }
                    
                    if (flip) {
                        idx[0] -= k[0];
                        idx[1] -= k[1];
                    }
                    else {
                        idx[0] += k[0];
                        idx[1] += k[1];
                    }
                    
                    if (flip) {
                        for (int i=0; i<2; i++) {
                            if (idx[i] == 0 && flip2[i]) {
                                idx[i] = 2*kMax[i];
                            }
                        }
                    }

                    if (waveVectorIndices[idx[0]][idx[1]] == 0) {
                        // this one was unique
                        count++;
                    }
                    waveVectorIndices[idx[0]][idx[1]]++;
                }
            }
        
        waveVectors = new Vector2D[count];
        coefficients = new double[count];
        count = 0;
        for (int kx = -kMax[0]; kx < kMax[0]+1; kx++) {
            for (int ky = -kMax[1]; ky < kMax[1]+1; ky++) {
                    if (waveVectorIndices[kx+kMax[0]][ky+kMax[1]] > 0) {
                        waveVectors[count] = space.makeVector();
                        waveVectors[count].Ea1Tv1(kx, waveVectorBasis[0]);
                        waveVectors[count].PEa1Tv1(ky, waveVectorBasis[1]);
                        coefficients[count] = waveVectorIndices[kx+kMax[0]][ky+kMax[1]]/2.0;
                        count++;
                    }
                }
            }
        }
   
    
    public IVectorMutable[] getWaveVectors() {
        return waveVectors;
    }
    
    public double[] getCoefficients() {
        return coefficients;
    }
    
    public static void main(String[] args) {
        int [] nCells = new int []{2,2};
        Space sp = Space2D.getInstance();
        Simulation sim = new Simulation(sp);
        IBox box = new Box(sp);
        sim.addBox(box);
        Primitive primitive = new PrimitiveOrthorhombicHexagonal(sim.getSpace(), 1);
        box.getBoundary().setDimensions(new Vector2D(primitive.getSize()[0]*nCells[0], primitive.getSize()[1]*nCells[1]));
        ISpecies species = new SpeciesSpheresMono(sim, sp);
        sim.getSpeciesManager().addSpecies(species);
        box.setNMolecules(species, nCells[0]*nCells[1]);
       
        
        WaveVectorFactory2D foo = new WaveVectorFactory2D(primitive, sp);
        foo.makeWaveVectors(box);
        IVectorMutable[] waveVectors = foo.getWaveVectors();
        double[] coefficients = foo.getCoefficients();
        System.out.println("number of wave vectors "+waveVectors.length);
        for (int i=0; i<waveVectors.length; i++) {
            System.out.println(coefficients[i]+" "+waveVectors[i]);
        }
    }

    private static final long serialVersionUID = 1L;
    protected final Primitive primitive;
    protected IVectorMutable[] waveVectors;
    protected double[] coefficients;
    private final ISpace space;
}
