package etomica.nbr.cell.molecule;

import etomica.api.IMolecule;
import etomica.atom.MoleculeArrayList;
import etomica.lattice.AbstractLattice;
import etomica.lattice.RectangularLattice;
import etomica.lattice.SiteFactory;

/**
 * Site used to form array of cells for cell-based neighbor listing.  Each
 * cell is capable of holding lists of molecules that are in them.
 * 
 * 
 * @author Tai Boon Tan
 *
 */
public class CellMolecular implements java.io.Serializable {

    public CellMolecular(int latticeArrayIndex) {
        this.latticeArrayIndex = latticeArrayIndex;
    }
    
    public MoleculeArrayList occupants() {
        return occupants;
    }
    
    public void addMolecule(IMolecule molecule) {
        occupants.add(molecule);
    }
    
    public void removeMolecule(IMolecule molecule) {
        occupants.removeAndReplace(occupants.indexOf(molecule));
    }
    
    public int getLatticeArrayIndex() {
        return latticeArrayIndex;
    }
    
    private final MoleculeArrayList occupants = new MoleculeArrayList(1);
    final int latticeArrayIndex;//identifies site in lattice

    private static final long serialVersionUID = 1L;
    public static final SiteFactory FACTORY = new CellFactory();
    
    public static class CellFactory implements SiteFactory, java.io.Serializable {
        private static final long serialVersionUID = 1L;

        public Object makeSite(AbstractLattice lattice, int[] coord) {
            return new CellMolecular(((RectangularLattice)lattice).arrayIndex(coord));
        }
    };
}
