package etomica.space;

import etomica.api.IVectorMutable;
import etomica.api.IVector;
import etomica.lattice.IndexIteratorRectangular;
import etomica.lattice.IndexIteratorSizable;

/**
 * Class for implementing pore periodic boundary conditions, in which
 * one dimension is periodic.  Selection of periodic dimension may be changed
 * after construction.
 */
public class BoundaryRectangularPore extends BoundaryRectangular {
    
    /**
     * Makes cubic volume with the x-dimension (index 0)
     * not periodic.  Length of each box edge is given by default boxSize in
     * given Simulation.
     */
    public BoundaryRectangularPore(Space space) {
        //consumer can set appropriate slit dim later
        this(space,0);
    }
    
    /**
     * Makes cubic volume with the indicated dimension not periodic.
     * Length of each box edge is given by the default boxSize in the
     * given Simulation.
     * 
     * @param slitDim index indicating dimension that is not periodic (0 for x-dimension,
     * 1 for y-dimension, etc.).
     * @throws IllegalArgumentException if not (0 <= slitDim < space.D).
     */
    public BoundaryRectangularPore(Space space, int poreDim) {
        this(space, poreDim, 10.0);
    }
    
    /**
     * Constructor for periodic boundary conditions with a slit 
     * in the given dimension.
     * @param space
     * @param slitDim slit dimension (in which PBC is not imposed).
     */
    public BoundaryRectangularPore(Space space, int slitDim, double boxSize) {
        super(space,boxSize);
        pDim = slitDim;
        dimensionsHalf = space.makeVector();
        tempImage = space.makeVector();
        // call updateDimensions again so dimensionsHalf is updated
        updateDimensions();
    }

    /**
     * Sets the periodic dimension to that indicated by the given value 
     * (0 is x-dimension, 1 is y-dimension, etc.).
     * 
     * @throws IllegalArgumentException if not (0 <= slitDim < space.D).
     */
    public void setPoreDim(int poreDim) {
        pDim = poreDim;
    }
    
    /**
     * Returns index of periodic dimension.
     */
    public int getPoreDim() {
        return pDim;
    }
    
    protected void updateDimensions() {
        super.updateDimensions();
        // superclass constructor calls this before dimensionsHalf has been instantiated
        if (dimensionsHalf != null) {
            dimensionsHalf.Ea1Tv1(0.5,dimensions);
        }
    }

    public void nearestImage(IVectorMutable dr) {
        double x = dr.x(pDim);
        if (x < -dimensionsHalf.x(pDim)) {
            x += dimensions.x(pDim);
            dr.setX(pDim, x);
        }
        else if (x > dimensionsHalf.x(pDim)) {
            x -= dimensions.x(pDim);
            dr.setX(pDim, x);
        }
    }
    
    public IVector centralImage(IVector r) {
        tempImage.E(r);
        nearestImage(tempImage);
        tempImage.ME(r);
        return tempImage;
    }


    public IndexIteratorSizable getIndexIterator() {
        return new IndexIteratorRectangular(1);
    }

    public boolean getPeriodicity(int d) {
        return d == pDim;
    }

    private int pDim;
    private static final long serialVersionUID = 1L;
    protected final IVectorMutable dimensionsHalf;
    protected final IVectorMutable tempImage;
}
