package etomica.atom;

import etomica.phase.Phase;

public class AtomSourceRandomMoleculeSeq extends AtomSourceRandomMolecule {

    public void setPhase(Phase p) {
        super.setPhase(p);
        speciesMaster = p.getSpeciesMaster();
        agentList = speciesMaster.getAgentList();
        reset();
    }
    
    public IAtom getAtom() {
        int moleculeCount = speciesMaster.moleculeCount();
        // this is probably innapropriate for atom removal
        if (prevIndex == -1 || prevIndex > moleculeCount-1) {
            // no suitable previous atom to step forward from the beginning
            reset();
            // no atoms in the phase
            if (prevIndex == -1) return null;
        }
        int lookAhead = random.nextInt(maxLookAhead+1);
        
        for ( ; agentIndex<agentList.size(); agentIndex++) {
            // advance through the species if needed
            moleculeList = ((IAtomGroup)agentList.get(agentIndex)).getChildList();
            int count = moleculeList.size();
            if (prevIndex+lookAhead < count) {
                prevIndex += lookAhead;
                lookAhead = -1;
            }
            prevIndex -= count;
        }
        if (lookAhead > -1) {
            // we ran out of species, so start over with the first species
            for (agentIndex=0 ; agentIndex<agentList.size(); agentIndex++) {
                moleculeList = ((IAtomGroup)agentList.get(agentIndex)).getChildList();
                int count = moleculeList.size();
                if (prevIndex+lookAhead < count) {
                    prevIndex += lookAhead;
                    lookAhead = -1;
                }
                prevIndex -= count;
            }
        }
        return moleculeList.get(prevIndex);
    }

    /**
     * Reset the atom used to step from to a random molecule
     */
    public void reset() {
        int size = speciesMaster.moleculeCount();
        if (size == 0) {
            prevIndex = -1;
            return;
        }
        prevIndex = random.nextInt(size);
        
        for (agentIndex=0; agentIndex<agentList.size(); agentIndex++) {
            moleculeList = ((IAtomGroup)agentList.get(agentIndex)).getChildList();
            int count = moleculeList.size();
            if (prevIndex < count) {
                break;
            }
            prevIndex -= count;
        }
    }

    /**
     * Returns the maximum number of molecules the source will advance for
     * each call to getAtom
     */
    public int getMaxLookAhead() {
        return maxLookAhead;
    }
    /**
     * Sets the maximum number of molecules the source will advance for
     * each call to getAtom
     */
    public void setMaxLookAhead(int maxLookAhead) {
        this.maxLookAhead = maxLookAhead;
    }
    
    private static final long serialVersionUID = 2L;
    protected int maxLookAhead = 10;
    protected SpeciesMaster speciesMaster;
    protected AtomArrayList moleculeList, agentList;
    protected int prevIndex, agentIndex;
}