package etomica.junit;

import etomica.atom.AtomFactory;
import etomica.atom.AtomFactoryHetero;
import etomica.atom.AtomFactoryMono;
import etomica.atom.AtomSet;
import etomica.atom.AtomTypeGroup;
import etomica.atom.AtomTypeLeaf;
import etomica.atom.AtomTypeSphere;
import etomica.atom.iterator.AtomIteratorTreePhase;
import etomica.phase.Phase;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.space.Space;
import etomica.space3d.Space3D;
import etomica.species.Species;
import etomica.species.SpeciesSpheres;
import etomica.species.SpeciesSpheresMono;
import etomica.species.SpeciesTree;
import etomica.util.Default;

/**
 * Contains some convenience methods and fields useful for implementing unit
 * tests.
 * 
 * @author David Kofke
 *  
 */
public class UnitTestUtil {

    public static boolean VERBOSE = false;

    /**
     * Private to prevent instantiation
     */
    private UnitTestUtil() {
        super();
    }

    public static Simulation makeStandardSpeciesTree() {
        return makeStandardSpeciesTree(new int[] { 5, 7 }, 3, new int[] { 10,
                10 }, new int[] { 3, 3 }, new int[] { 5, 4, 3 });
    }

    /**
     * Makes tree hierarchy of three species and one or more phases. First
     * species has multiatoms molecules, second species has monatomic molecules
     * (with leaf atoms in molecule layer), and third species has an arbitrary
     * tree structure.
     * 
     * @param n0
     *            number of atoms of species0 in each phase
     * @param nA0
     *            number of atoms per molecule of species0
     * @param n1
     *            number of atoms of species1 in each phase
     * @param n2
     *            number of atoms of species2 in each phase
     * @param n2A
     *            tree specification of species 2, e.g., {2,4} indicates that
     *            each molecule has 2 subgroups, each with 4 atoms (such as
     *            CH3-CH3)
     * @return root of species hierarchy
     */

    public static Simulation makeStandardSpeciesTree(int[] n0, int nA0,
            int[] n1, int[] n2, int[] n2Tree) {
        Space space = Space3D.getInstance();
        Simulation sim = new Simulation(space, false, new PotentialMaster(space),
                new int[] { 5, 4, 11, 6, 3, 3 }, new Default());
        Species species0 = null;
        Species species1 = null;
        Species species2 = null;
        int nPhase = 0;
        if (n0 != null) {
            species0 = new SpeciesSpheres(sim, nA0);
            sim.getSpeciesManager().addSpecies(species0);
            nPhase = n0.length;
        }
        if (n1 != null) {
            species1 = new SpeciesSpheresMono(sim);
            sim.getSpeciesManager().addSpecies(species1);
            nPhase = n1.length;
        }
        if (n2 != null) {
            species2 = new SpeciesTree(sim, n2Tree);
            sim.getSpeciesManager().addSpecies(species2);
            nPhase = n2.length;
        }
        for (int i = 0; i < nPhase; i++) {
            Phase phase = new Phase(sim);
            if (species0 != null)
                phase.getAgent(species0).setNMolecules(n0[i]);
            if (species1 != null)
                phase.getAgent(species1).setNMolecules(n1[i]);
            if (species2 != null)
                phase.getAgent(species2).setNMolecules(n2[i]);
        }
        return sim;
    }

    /**
     * Makes tree hierarchy of one or more species in a single phase. Number of
     * species if determined by length of nMolecules array. Each molecule is
     * heterogeneous, formed from atoms of different types. For example:
     * <ul>
     * <li>nMolecules = {5,3} will form two species, with five molecules of
     * speciesA and 3 molecule of SpeciesB.
     * <li>Then with nAtoms = {{2,1,4},{2,3}}, a speciesA molecule will contain
     * 2+1+4 = 7 atoms, with 2 of type-a, 1 of type-b, and 4 of type-c, and a
     * speciesB molecule will contain 2+3 = 5 atoms, with 2 of type-d and 3 of
     * type-e. All types are different instance of AtomTypeSphere.
     * </ul>
     * 
     * @param nMolecules
     *            number of molecules made of each species. Number of species is
     *            determined by length of this array.
     * @param nAtoms
     *            first index corresponds to species, so nAtoms.length should
     *            equal nMolecules.length. Number of types in a species-j
     *            molecule is given by the length of the nAtoms[j] subarray, and
     *            the elements of this array give the number of atoms of each
     *            type used to form a molecule.
     * @return root of the species hierarchy
     */
    public static Simulation makeMultitypeSpeciesTree(int[] nMolecules,
            int[][] nAtoms) {
        Space space = Space3D.getInstance();
        Simulation sim = new Simulation(space, false, new PotentialMaster(space),
                new int[] { 9, 11, 6, 3, 3 }, new Default());
        //        new SpeciesSpheres(sim);
        Phase phase = new Phase(sim);
        for (int i = 0; i < nMolecules.length; i++) {
            AtomFactoryHetero factory = new AtomFactoryHetero(sim);
            AtomFactory[] childFactories = new AtomFactory[nAtoms[i].length];
            for (int j = 0; j < childFactories.length; j++) {
                AtomTypeLeaf atomType = new AtomTypeSphere(sim);
                atomType.setParentType((AtomTypeGroup) factory.getType());
                childFactories[j] = new AtomFactoryMono(space, atomType);
            }
            factory.setChildFactory(childFactories);
            factory.setChildCount(nAtoms[i]);
            Species species = new Species(factory);
            sim.getSpeciesManager().addSpecies(species);
            phase.getAgent(species).setNMolecules(nMolecules[i]);
        }
        return sim;
    }
    
    public static void main(String[] arg) {
        Simulation sim = makeStandardSpeciesTree();
        Phase[] phases = sim.getPhases();
        for (int i=0; i<phases.length; i++) {
            AtomIteratorTreePhase iterator = new AtomIteratorTreePhase();
            iterator.setPhase(phases[i]);
            iterator.setDoAllNodes(true);
            iterator.reset();
            for (AtomSet atom = iterator.next(); atom != null;
                 atom = iterator.next()) {
                System.out.println(atom.toString());
            }
        }
    }

}
