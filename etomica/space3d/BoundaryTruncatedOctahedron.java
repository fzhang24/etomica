package etomica.space3d;

import etomica.api.IVector;
import etomica.exception.MethodNotImplementedException;
import etomica.lattice.IndexIteratorRectangular;
import etomica.lattice.IndexIteratorSizable;
import etomica.math.SpecialFunctions;
import etomica.math.geometry.Plane;
import etomica.math.geometry.Polygon;
import etomica.math.geometry.Polyhedron;
import etomica.math.geometry.TruncatedOctahedron;
import etomica.space.Boundary;
import etomica.space.BoundaryPeriodic;
import etomica.space.ISpace;

/**
 * This class enables creation of a periodic truncated-octahedron boundary.
 * Truncated octahedrons are spacefillers; unlike spheres, about which voids
 * form when placed in an array, truncated octahedrons can be flush with other
 * truncated octahedrons at every face when grouped in an array. Moreover,
 * truncated octahedrons are considerably more sphere-like than cubes, and using
 * a truncated octahedron as a boundary greatly increases the isotropy of the
 * system.
 * <p>
 * There are no subclasses for periodic and nonperiodic boundary conditions for
 * this class because nonperiodic boundary conditions are not applicable for
 * truncated octahedrons.
 */
public class BoundaryTruncatedOctahedron extends Boundary implements
        BoundaryPeriodic {

    public BoundaryTruncatedOctahedron(ISpace _space) {
        this(_space, 30.0);
    }
    public BoundaryTruncatedOctahedron(ISpace _space, double boxSize) {
        super(_space, new TruncatedOctahedron(_space));
        plane = new Plane(space);
        isPeriodic = new boolean[space.D()];
        for (int i = 0; i < space.D(); i++)
            isPeriodic[i] = true;
        dimensions = space.makeVector();
        dimensions.E(boxSize);
        rrounded = space.makeVector();
        intoTruncatedOctahedron = space.makeVector();
        dimensionsCopy = space.makeVector();
        dimensionsHalf = space.makeVector();
        dimensionsHalfCopy = space.makeVector();
        indexIterator = new IndexIteratorRectangular(space.D());
        updateDimensions();
    }

    public boolean[] getPeriodicity() {
        return isPeriodic;
    }

    public final IVector getDimensions() {
        return dimensionsCopy;
    }

    public IVector randomPosition() {
        throw new MethodNotImplementedException();
        //temp.setRandom(dimensions);
        //return temp;
    }

    public IndexIteratorSizable getIndexIterator(){
      return new IndexIteratorRectangularFiltered(vecs.length,vecs);
    }
    
    private static class IndexIteratorRectangularFiltered
      implements IndexIteratorSizable {
      
      private IndexIteratorRectangular iis;
      private boolean hasnext;
      private int[] vals;
      private int[] retvals;
      private IVector[] vecs;
      int numLayers;
      
      public IndexIteratorRectangularFiltered(int D, IVector[] v) {
        iis = new IndexIteratorRectangular(D);
        vecs = v;
        hasnext = false;
        vals = new int[D];
        retvals = new int[D];
      }

      public int getD() { return iis.getD(); }
      public boolean hasNext() { return hasnext; }
      
      public int[] next() {
        for(int i=0; i<retvals.length; i++)
          retvals[i] = vals[i];
        hasnext = false;
        findNext();
        return retvals;
      }

      public void reset() {
        hasnext = false;
        iis.reset();
        findNext();
      }

      /**
       * Find next iterate, if any. Stores it locally and updates
       * hasnext as needed.
       */
      private void findNext() {
        double radius = Math.abs(vecs[0].x(0)*2.0001)*numLayers;
        if(!iis.hasNext()) {
          hasnext = false;
          vals = null;
          return;
        }
        
        while(iis.hasNext()) {
          float dx = 0, dy = 0, dz = 0;
          int[] lvals = iis.next();
          for(int i=0; i<getD(); i++) {
            dx += (float)( (lvals[i]-(numLayers*2)) * vecs[i].x(0));
            dy += (float)( (lvals[i]-(numLayers*2)) * vecs[i].x(1));
            dz += (float)( (lvals[i]-(numLayers*2)) * vecs[i].x(2));
          }
          if(Math.sqrt(dx*dx + dy*dy + dz*dz) <= radius) {
            //include
            hasnext = true;
            /*
             * The vectors generated based on the iterator alone would be
             * biased toward the positive. Subtract numLayers to add extra
             * bias to the negative. This is dependent on subtracting
             * numlayers*2 above as well as the fudge factor of 4.
             */
            for(int i=0; i<vals.length; i++)
              vals[i] = lvals[i] - numLayers;
            break;
          }
        }
      }
      
      //for generating more shells than we need for filtering
      private int fudgeFactor = 4;

      public void setSize(int[] size) {
        this.numLayers = (size[0]-1)/2; // assume each dimension is same size
        int[] newsize = new int[size.length];
        for(int i=0; i<size.length; i++) { newsize[i] = (size[i]*fudgeFactor)+1; }
        iis.setSize(newsize);
      }
      
    }
    
    private final void updateDimensions() {
      /*
       * update field for these vectors (vecs); index iterator
       * references them and automatically calculates a new
       * radius based on that
       * 
       * vec update should be automatic now
       */
        dimensionsHalf.Ea1Tv1(0.5, dimensions);
        dimensionsCopy.E(dimensions);
        ((TruncatedOctahedron) shape).setContainingCubeEdgeLength(dimensions
                .x(0));
    }

    public void setDimensions(IVector v) {
        dimensions.E(v);
        updateDimensions();
    }
    
    private IVector[] vecs; 
    public IVector[] getPeriodicVectors() {
      //throw new RuntimeException("Not yet.  Gimme a break!");
      double x = dimensions.x(0)*.5;
      if(vecs == null || vecs[0].x(0) == 0) {
        vecs = new IVector[] {
            space.makeVector(new double[]{-x,x,x}),
            space.makeVector(new double[]{x,-x,x}),
            space.makeVector(new double[]{x,x,-x}) };
      }
      else if(vecs[1].x(0) != x) {
        double ratio = x/vecs[1].x(0);
        vecs[0].TE(ratio);
        vecs[1].TE(ratio);
        vecs[2].TE(ratio);
      }
      return vecs;
    }

    public double[][] imageOrigins(int nShells) {
        if(nShells == 0) {
            return new double[0][];
        } else if(nShells == 1) {
            Polygon[] faces = ((Polyhedron)shape).getFaces();
            double[][] origins = new double[faces.length][space.D()];
            double multiplier = ((TruncatedOctahedron)shape).getContainingCubeEdgeLength();
            for(int i=0; i<faces.length; i++) {
                IVector[] vertices = faces[i].getVertices();
                plane.setThreePoints(vertices[0], vertices[1], vertices[2]);
                plane.getNormalVector(normal);
                normal.TE(multiplier);
                normal.assignTo(origins[i]);
            }
            return origins;
        }
        //algorithm for nShells > 1 misses many of the images (those through the hexagon faces)
        IVector workVector = space.makeVector();
        int shellFormula = (2 * nShells) + 1;
        int nImages = space.powerD(shellFormula) - 1;
        double[][] origins = new double[nImages][space.D()];
        indexIterator.setSize(shellFormula);
        indexIterator.reset();
        int k = 0;
        while (indexIterator.hasNext()) {
            int[] index = indexIterator.next();
            for (int i = 0; i<workVector.getD(); i++) {
                workVector.setX(i,index[i]);
            }
            workVector.PE(-(double) nShells);
            if (workVector.isZero())
                continue;
            workVector.TE(dimensions);
            workVector.assignTo(origins[k++]);
        }
        return origins;
    }

    public void nearestImage(IVector dr) {
        dr.PEa1Tv1(0.5, dimensions);
        dr.PE(centralImage(dr));
        dr.PEa1Tv1(-0.5, dimensions);

        //        double n =
        // ((TruncatedOctahedron)shape).getContainingCubeEdgeLength();
        //        intoContainingCube.EModShift(dr, dimensions);
        //        rrounded.Ev1Pv2(dr,intoContainingCube);
        //        rrounded.TE(1./n);
        //        int aint =
        // (int)(4.0/3.0*(Math.abs(rrounded.x(0))+Math.abs(rrounded.x(1))+Math.abs(rrounded.x(2))));
        //        double corr = 0.5 * n * aint;
        //        if(corr != 0.0) {
        //            if(rrounded.x(0) > 0) intoTruncatedOctahedron.setX(0,
        // intoContainingCube.x(0) - corr);
        //                else intoTruncatedOctahedron.setX(0, intoContainingCube.x(0) + corr);
        //            if(rrounded.x(1) > 0) intoTruncatedOctahedron.setX(1,
        // intoContainingCube.x(1) - corr);
        //                else intoTruncatedOctahedron.setX(1, intoContainingCube.x(1) + corr);
        //            if(rrounded.x(2) > 0) intoTruncatedOctahedron.setX(2,
        // intoContainingCube.x(2) - corr);
        //                else intoTruncatedOctahedron.setX(2, intoContainingCube.x(2) + corr);
        //        } else {
        //            intoTruncatedOctahedron.E(intoContainingCube);
        //        }
        //        dr.PE(intoTruncatedOctahedron);
    }

    public IVector centralImage(IVector r) {
        double n = ((TruncatedOctahedron) shape).getContainingCubeEdgeLength();
        intoTruncatedOctahedron.Ev1Pv2(r, dimensionsHalf);
        intoTruncatedOctahedron.mod(dimensions);
        intoTruncatedOctahedron.ME(dimensionsHalf);
        rrounded.E(intoTruncatedOctahedron);
        intoTruncatedOctahedron.ME(r);
        rrounded.TE(1. / n);

        int aint = (int) (4.0 / 3.0 * (Math.abs(rrounded.x(0))
                + Math.abs(rrounded.x(1)) + Math.abs(rrounded.x(2))));

        if (aint != 0) {
            double corr = 0.5 * n * aint;

            for (int i=0; i<3; i++) {
                rrounded.setX(i,SpecialFunctions.sgn(rrounded.x(i)));
            }
            
            intoTruncatedOctahedron.PEa1Tv1(-corr, rrounded);
        }
        return intoTruncatedOctahedron;
    }
    
    public IVector getBoundingBox() {
        return dimensionsCopy;
    }
    
    public IVector getCenter() {
        return dimensionsHalfCopy;
    }

    private static final long serialVersionUID = 1L;
    protected final IVector intoTruncatedOctahedron;
    protected final IVector rrounded;
    protected final IVector dimensions;
    protected final IVector dimensionsCopy;
    protected final IVector dimensionsHalf;
    protected final IVector dimensionsHalfCopy;
    private final IndexIteratorRectangular indexIterator;
    protected final boolean[] isPeriodic;
    protected final float[][] shift0 = new float[0][0];
    protected float[][] shift;
    private final Plane plane;
    private final Vector3D normal = new Vector3D();
}
