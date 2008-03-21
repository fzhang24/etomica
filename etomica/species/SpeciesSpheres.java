package etomica.species;
import etomica.api.IAtomLeaf;
import etomica.api.IAtomTypeLeaf;
import etomica.api.IMolecule;
import etomica.api.ISimulation;
import etomica.atom.AtomLeaf;
import etomica.atom.AtomLeafDynamic;
import etomica.atom.AtomPositionGeometricCenter;
import etomica.atom.AtomTypeSphere;
import etomica.atom.Molecule;
import etomica.chem.elements.Element;
import etomica.chem.elements.ElementSimple;
import etomica.config.Conformation;
import etomica.config.ConformationLinear;
import etomica.space.Space;

/**
 * Species in which molecules are made of arbitrary number of spheres,
 * with each sphere having the same mass and size (same type).
 * 
 * @author David Kofke
 */
public class SpeciesSpheres extends Species {

    public SpeciesSpheres(ISimulation sim, Space _space) {
        this(sim, _space, 1);
    }
    public SpeciesSpheres(ISimulation sim, Space _space, int nA) {
        this(sim, _space, nA, new ElementSimple(sim));
    }
    
    public SpeciesSpheres(ISimulation sim, Space _space, int nA, Element leafElement) {
        this(sim, nA, leafElement, new ConformationLinear(_space), _space);
    }
    
    public SpeciesSpheres(ISimulation sim, int nA, Element leafElement,
    		              Conformation conformation, Space _space) {
        this(_space, sim.isDynamic(), nA, new AtomTypeSphere(leafElement), conformation);
    }
    
    public SpeciesSpheres(Space _space, boolean isDynamic, int nA, IAtomTypeLeaf leafAtomType, Conformation conformation) {
        super(new AtomPositionGeometricCenter(_space));
        this.space = _space;
        addChildType(leafAtomType);
        setNumLeafAtoms(nA);
        setConformation(conformation);
        this.leafAtomType = leafAtomType;
        this.isDynamic = isDynamic;
    }
    
    public IAtomTypeLeaf getLeafType() {
        return getChildTypes()[0];
    }
    
    /**
     * Constructs a new group.
     */
     public IMolecule makeMolecule() {
         Molecule group = new Molecule(this);
         for(int i=0; i<atomsPerGroup; i++) {
             group.addChildAtom(makeLeafAtom());
         }
         return group;
     }
     
     protected IAtomLeaf makeLeafAtom() {
         return isDynamic ? new AtomLeafDynamic(space, leafAtomType)
                          : new AtomLeaf(space, leafAtomType);
     }

    /**
     * Specifies the number of child atoms in each atom constructed by this factory.
     * 
     * @param na The new number of atoms per group
     */
    public void setNumLeafAtoms(int na) {
        atomsPerGroup = na;
    }

     public int getNumLeafAtoms() {
         return atomsPerGroup;
     }

     private static final long serialVersionUID = 1L;
     protected final boolean isDynamic;
     protected final Space space;
     protected int atomsPerGroup;
     protected final IAtomTypeLeaf leafAtomType;
}
