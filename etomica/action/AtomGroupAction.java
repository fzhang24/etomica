package etomica.action;

import etomica.atom.Atom;
import etomica.atom.AtomArrayList;
import etomica.atom.AtomLinker;
import etomica.atom.AtomTreeNodeGroup;

/**
 * Wraps an AtomAction, and performs the wrapped action on the atom
 * only if it is a leaf atom; if given an atom group (as indicated
 * by the atom's node), performs action instead on all the atom's
 * child atoms.  This process continues recursively until the leaf atoms are
 * encountered.
 *
 * @author David Kofke
 */

/*
 * History
 * Created on Jan 26, 2005 by kofke
 */
public class AtomGroupAction extends AtomActionAdapter {

    /**
     * Constructor takes wrapped action, which is final.
     */
    public AtomGroupAction(AtomAction action) {
        this.action = action;
    }
    /* (non-Javadoc)
     * @see etomica.action.AtomAction#actionPerformed(etomica.Atom)
     */
    public void actionPerformed(Atom atom) {
        if(atom.node.isLeaf()) {
            action.actionPerformed(atom);
        } else {
            AtomArrayList atomList = ((AtomTreeNodeGroup)atom.node).childList;
            int size = atomList.size();
            for(int i=0; i<size; i++) {
                this.actionPerformed(atomList.get(i));
            }
        }
    }

    /* (non-Javadoc)
     * @see etomica.Action#getLabel()
     */
    public String getLabel() {
        return action.getLabel();
    }
    
    /**
     * @return Returns the wrapped action.
     */
    public AtomAction getAction() {
        return action;
    }
    
    private final AtomAction action;
}
