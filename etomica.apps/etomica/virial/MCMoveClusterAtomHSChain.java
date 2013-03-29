package etomica.virial;

import etomica.api.IAtomList;
import etomica.api.IRandom;
import etomica.integrator.mcmove.MCMoveAtom;
import etomica.space.ISpace;
import etomica.space.IVectorRandom;

public class MCMoveClusterAtomHSChain extends MCMoveAtom {

    public MCMoveClusterAtomHSChain(IRandom random, ISpace _space, double sigma) {
        super(random, null, _space);
        this.sigma = sigma;
        dr = (IVectorRandom)space.makeVector();
    }
    
    public boolean doTrial() {
        
        IAtomList leafAtoms = box.getLeafList();
        int n = leafAtoms.getAtomCount();

        for (int i=1; i<n; i++) {
            IVectorRandom pos = (IVectorRandom)leafAtoms.getAtom(i).getPosition();

            pos.setRandomInSphere(random);
            pos.TE(sigma);
            pos.PE(leafAtoms.getAtom(i-1).getPosition());
        }

		((BoxCluster)box).trialNotify();
		return true;
	}
	
    public double getA() {
        return 1;
    }

    public double getB() {
    	return 0.0;
    }
    
    public void rejectNotify() {
        throw new RuntimeException("nope");
    }
    
    public void acceptNotify() {
    	((BoxCluster)box).acceptNotify();
    }

    protected final double sigma;
    protected final IVectorRandom dr;
}
