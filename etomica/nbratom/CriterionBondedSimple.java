/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.nbratom;

import etomica.AtomPair;
import etomica.nbr.NeighborCriterion;

/**
 * @author andrew
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CriterionBondedSimple extends CriterionAdapter {

    public CriterionBondedSimple(NeighborCriterion criterion) {
        super(criterion);
    }
    
    public void setBonded(boolean b) {
        isBonded = b;
    }
    public boolean isBonded() {
        return isBonded;
    }
    
    // always enforce intramolecularity
    public boolean accept(AtomPair pair) {
        int diff = pair.atom0.node.getOrdinal() - pair.atom1.node.getOrdinal();
        if (isBonded != (diff == 1 || diff == -1) 
                || (!pair.atom0.inSameMolecule(pair.atom1))) {
            return false;
        }
        return subCriterion.accept(pair);
    }
    
    private boolean isBonded;

}
