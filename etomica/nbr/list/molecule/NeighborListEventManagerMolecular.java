package etomica.nbr.list.molecule;

import java.util.LinkedList;


/**
 * @author Tai Boon Tan
 *
 */
public class NeighborListEventManagerMolecular {

    private final LinkedList<INeighborListListenerMolecular> intervalListeners = new LinkedList<INeighborListListenerMolecular>();
    private static final long serialVersionUID = 1L;

    
    public synchronized void addListener(INeighborListListenerMolecular newListener) {
        if(newListener == null) throw new NullPointerException("Cannot add null as a listener to Neighbor List");
        if (intervalListeners.contains(newListener)) {
            throw new RuntimeException(newListener+" is already an interval action");
        }
        intervalListeners.add(newListener);
    }

    public synchronized void removeListener(INeighborListListenerMolecular listener) {
        intervalListeners.remove(listener);
    }

    public void neighborsUpdated() {
        for(int i = 0; i < intervalListeners.size(); i++) {
            intervalListeners.get(i).neighborListNeighborsUpdated();
        }
    }
}
