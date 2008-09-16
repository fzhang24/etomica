package etomica.lattice;
import etomica.lattice.crystal.BasisOrthorhombicHexagonal;
import etomica.lattice.crystal.PrimitiveHexagonal2D;
import etomica.lattice.crystal.PrimitiveOrthorhombicHexagonal;
import etomica.space.ISpace;

/**
 * Lattice class for a hexagonal lattice composed of rectangular primitives 
 * with a 2-atom basis.
 * 
 * @author Andrew Schultz
 */
public class LatticeOrthorhombicHexagonal extends BravaisLatticeCrystal implements SpaceLattice {

    public LatticeOrthorhombicHexagonal(ISpace space) {
        this(space, 1);
    }
    
    public LatticeOrthorhombicHexagonal(ISpace space, double latticeConstant) {
        this(new PrimitiveOrthorhombicHexagonal(space, latticeConstant));
        if(space.D() != 2) {
            throw new IllegalArgumentException("LatticeOrthorhombicHexagonal requires a 2-D space");
        }
    }

    /**
     * Auxiliary constructor needed to be able to pass new PrimitiveCubic and
     * new BasisCubicFcc (which needs the new primitive) to super.
     */ 
    private LatticeOrthorhombicHexagonal(PrimitiveOrthorhombicHexagonal primitive) {
        super(primitive, new BasisOrthorhombicHexagonal());
    }
    
    /**
     * Returns a new PrimitiveFcc instance corresponding to the fcc lattice. 
     * Changes to the returned instance have no effect on the lattice.
     */
    public PrimitiveHexagonal2D getPrimitiveHexagonal() {
        PrimitiveHexagonal2D p = new PrimitiveHexagonal2D(getSpace());
        p.setSizeAB(((PrimitiveOrthorhombicHexagonal)primitive).getSizeA());
        return p;
    }
    

    /**
     * Rescales the lattice by the given factor. Multiplies the lattice constant
     * by the given value.
     */
    public void scaleBy(double scaleFactor) {
        primitive.scaleSize(scaleFactor);
    }

    /**
     * Returns "OrthorhombicHexagonal".
     */
    public String toString() {return "OrthorhombicHexagonal";}
    
    private static final long serialVersionUID = 1L;
}
