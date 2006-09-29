package etomica.models.water;

import etomica.atom.Atom;
import etomica.atom.AtomFactory;
import etomica.atom.AtomFactoryMono;
import etomica.atom.AtomLeaf;
import etomica.atom.AtomPositionGeometricCenter;
import etomica.atom.AtomTypeGroup;
import etomica.atom.AtomTypeSphere;
import etomica.simulation.Simulation;
import etomica.space.CoordinateFactory;
import etomica.space.CoordinateFactorySphere;
import etomica.species.Species;

/**
 * Factory that constructs a 3-point water molecule, with three child atoms of 
 * two Hydrogen and one Oxygen.
 * @author kofke
 *
 */
public class AtomFactoryWater3P extends AtomFactory {

	/**
	 * Constructor for AtomFactoryWater.
	 * @param sim
	 * @param sequencerFactory
	 */
    public AtomFactoryWater3P(Simulation sim, AtomTypeGroup agentType) {
		super(new AtomTypeGroup(new AtomPositionGeometricCenter(sim.space)), AtomTreeNodeWater3P.FACTORY);
        // atomType is the AtomTypeGroup we just made and passed to the super constructor
        atomType.setParentType(agentType);

        AtomTypeSphere hType = new AtomTypeSphere(1.0, 2.0);
        AtomTypeSphere oType = new AtomTypeSphere(16.0, 3.167);
        hType.setParentType((AtomTypeGroup)atomType);
        oType.setParentType((AtomTypeGroup)atomType);
        CoordinateFactory leafCoordFactory = new CoordinateFactorySphere(sim);
        hFactory = new AtomFactoryMono(leafCoordFactory, hType);
		oFactory = new AtomFactoryMono(leafCoordFactory, oType);

		conformation = new ConformationWater3P(sim.space); 
	}

	/**
	 * @see etomica.atom.AtomFactory#build(etomica.Atom)
	 */
	public Atom makeAtom() {
        Atom group = newParentAtom();
		AtomTreeNodeWater3P waterNode = (AtomTreeNodeWater3P)group.node;
		waterNode.O = (AtomLeaf)oFactory.makeAtom();
        waterNode.H1 = (AtomLeaf)hFactory.makeAtom();
        waterNode.H2 = (AtomLeaf)hFactory.makeAtom();
        waterNode.O.node.setParent(waterNode);
        waterNode.H1.node.setParent(waterNode);
        waterNode.H2.node.setParent(waterNode);
		conformation.initializePositions(waterNode.childList);
		return group;
	}
    
    /**
     * Returns 4, equal to 1 parent molecule + 3 atoms child atoms in the molecule.
     */
    public int getNumTreeAtoms() {
        return 4;
    }
    
    /**
     * Returns 3.
     */
    public int getNumChildAtoms() {
        return 3;
    }
    
    /**
     * Returns 3.
     */
    public int getNumLeafAtoms() {
        return 3;
    }
    
	public final AtomFactoryMono hFactory, oFactory;
}
