package etomica.junit;

import junit.framework.TestCase;
import etomica.Atom;
import etomica.Phase;
import etomica.Simulation;
import etomica.Species;
import etomica.SpeciesAgent;
import etomica.SpeciesMaster;
import etomica.SpeciesSpheres;
import etomica.SpeciesSpheresMono;
import etomica.atom.AtomSequencerFactory;
import etomica.atom.AtomTreeNodeGroup;


/**
 * Performs tests of AtomIndexManager, checking that methods to determine
 * if two atoms are in the same phase, species, molecule, etc., function 
 * correctly.
 */

/*
 * History
 * Created on Mar 9, 2005 by kofke
 */
public class AtomIndexManagerTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Simulation sim = new Simulation();
        SpeciesSpheresMono species0 = new SpeciesSpheresMono(sim);
        species0.setNMolecules(20);
        SpeciesSpheres species1 = new SpeciesSpheres(sim, AtomSequencerFactory.SIMPLE,5);
        species1.setNMolecules(10);
        Phase phase0 = new Phase(sim);
        Phase phase1 = new Phase(sim);
        atoms = new Atom[27];
        int i = 0;
        atoms[i++] = sim.speciesRoot;//0
        atoms[i++] = master0 = phase0.speciesMaster;//1
        atoms[i++] = master1 = phase1.speciesMaster;//2
        atoms[i++] = agent00 = phase0.getAgent(species0);//3
        atoms[i++] = agent01 = phase0.getAgent(species1);//4
        atoms[i++] = agent10 = phase1.getAgent(species0);//5
        atoms[i++] = agent11 = phase1.getAgent(species1);//6
        AtomTreeNodeGroup[][] node = new AtomTreeNodeGroup[2][2];
        node[0][0] = (AtomTreeNodeGroup)agent00.node;
        node[0][1] = (AtomTreeNodeGroup)agent01.node;
        node[1][0] = (AtomTreeNodeGroup)agent10.node;
        node[1][1] = (AtomTreeNodeGroup)agent11.node;
        phaseIndex =    new int[] {0,0,0,0,1,1,1,1, 0,0,0,0,0,0,1,1,1,1,1,1};
        speciesIndex =  new int[] {0,0,1,1,0,0,1,1, 1,1,1,1,1,1,1,1,1,1,1,1};
        moleculeIndex = new int[] {0,5,0,3,0,5,0,4, 0,0,3,3,4,4,0,0,3,3,4,4};
        atomIndex =     new int[] {0,5,0,3,0,5,0,4, 1,4,0,4,0,1,1,4,0,4,0,1};
        i0 = i;
        int del = 8;
        for(int j=0; j<del; j++) {
            atoms[i++] = node[phaseIndex[j]][speciesIndex[j]].childList.get(moleculeIndex[j]);//7
        }
        for(int j=del; j<phaseIndex.length; j++) {
            atoms[i++] = node[phaseIndex[j]][speciesIndex[j]].getDescendant(new int[] {moleculeIndex[j], atomIndex[j]});
        }
        atom = new Atom(sim.space);
    }

    public void testGetOrdinal() {
        for(int i=0; i<atomIndex.length; i++) {
//          System.out.println("AtomIndex: "+atoms[i+i0].node.getOrdinal()+" "+(1+atomIndex[i]));
//          System.out.println("Bits: "+Integer.toBinaryString(atoms[i+i0].node.index()));
//          System.out.println(atoms[i+i0].toString());
          assertEquals(atoms[i+i0].node.getOrdinal(), 1+atomIndex[i]);
      }
    }

    public void testGetPhaseIndex() {
        for(int i=0; i<phaseIndex.length; i++) {
//          System.out.println("PhaseIndex: "+atoms[i+i0].node.getPhaseIndex()+" "+(1+phaseIndex[i]));
          assertEquals(atoms[i+i0].node.getPhaseIndex(), 1+phaseIndex[i]);
      }
    }

    public void testGetSpeciesIndex() {
        for(int i=0; i<speciesIndex.length; i++) {
//            System.out.println("SpeciesIndex: "+atoms[i+i0].node.getSpeciesIndex()+" "+(1+speciesIndex[i]));
            assertEquals(atoms[i+i0].node.getSpeciesIndex(), 1+speciesIndex[i]);
        }
    }

    public void testGetMoleculeIndex() {
        for(int i=0; i<moleculeIndex.length; i++) {
//            System.out.println("MoleculeIndex: "+atoms[i+i0].node.getMoleculeIndex()+" "+(1+moleculeIndex[i]));
            assertEquals(atoms[i+i0].node.getMoleculeIndex(), 1+moleculeIndex[i]);
        }
    }

    public void testSamePhase() {
        int trueCount = 0;
        int falseCount = 0;
        int undefinedCount = 0;
        for(int i=1; i<atoms.length; i++) {//skip speciesRoot
            Phase phaseA = atoms[i].node.parentPhase();
            assertFalse(atoms[i].inSamePhase(atom));
            assertFalse(atom.inSamePhase(atoms[i]));
            for(int j=1; j<atoms.length; j++) {
                Phase phaseB = atoms[j].node.parentPhase();
                boolean inSamePhase = atoms[i].inSamePhase(atoms[j]);
                if(phaseA == null || phaseB == null) {
                    if(inSamePhase) System.out.println(inSamePhase+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertFalse(inSamePhase);
                    undefinedCount++;
                } else if(phaseA == phaseB) {
                    if(!inSamePhase) System.out.println(inSamePhase+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertTrue(inSamePhase);
                    trueCount++;
                } else {
                    if(inSamePhase) System.out.println(inSamePhase+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertFalse(inSamePhase);
                    falseCount++;
                }
            }
        }
        System.out.println("AtomIndexManagerTest(Phase): trueCount = "+trueCount+"; falseCount = "+falseCount+"; undefinedCount = "+undefinedCount);
    }

    public void testSameSpecies() {
        int trueCount = 0;
        int falseCount = 0;
        int undefinedCount = 0;
        for(int i=0; i<atoms.length; i++) {
            Species speciesA = atoms[i].type.getSpecies();
            if(atoms[i].inSameSpecies(atom)) System.out.println(i+" "+atoms[i]+" "+atom);
            assertFalse(atoms[i].inSameSpecies(atom));
            assertFalse(atom.inSameSpecies(atoms[i]));
            for(int j=0; j<atoms.length; j++) {
                if(i < 3 && j < 3) continue;
                Species speciesB = atoms[j].type.getSpecies();
                boolean inSameSpecies = atoms[i].inSameSpecies(atoms[j]);
                if(speciesA == null || speciesB == null) {
                    if(inSameSpecies) System.out.println(inSameSpecies+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertFalse(inSameSpecies);
                    undefinedCount++;
                } else if(speciesA == speciesB) {
                    if(!inSameSpecies) System.out.println(inSameSpecies+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertTrue(inSameSpecies);
                    trueCount++;
                } else {
                    if(inSameSpecies) System.out.println(inSameSpecies+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertFalse(inSameSpecies);
                    falseCount++;
                }
            }
        }
        System.out.println("AtomIndexManagerTest(Species): trueCount = "+trueCount+"; falseCount = "+falseCount+"; undefinedCount = "+undefinedCount);
    }

    public void testSameMolecule() {
        int trueCount = 0;
        int falseCount = 0;
        int undefinedCount = 0;
        for(int i=i0; i<atoms.length; i++) {
            if(atoms[i].inSameMolecule(atom)) System.out.println(i+" "+atoms[i]+" "+atom);
            assertFalse(atoms[i].inSameMolecule(atom));
            assertFalse(atom.inSameMolecule(atoms[i]));
            Atom moleculeA = atoms[i].node.parentMolecule();
            for(int j=i0; j<atoms.length; j++) {
                Atom moleculeB = atoms[j].node.parentMolecule();
                boolean inSameMolecule = atoms[i].inSameMolecule(atoms[j]);
                if(moleculeA == null || moleculeB == null) {
                    if(inSameMolecule) System.out.println(inSameMolecule+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertFalse(inSameMolecule);
                    undefinedCount++;
                } else if(moleculeA == moleculeB) {
                    if(!inSameMolecule) System.out.println(inSameMolecule+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertTrue(inSameMolecule);
                    trueCount++;
                } else {
                    if(inSameMolecule) System.out.println(inSameMolecule+" "+i+" "+j+" "+atoms[i]+" "+atoms[j]);
                    assertFalse(inSameMolecule);
                    falseCount++;
                }
            }
        }
        System.out.println("AtomIndexManagerTest(Molecule): trueCount = "+trueCount+"; falseCount = "+falseCount+"; undefinedCount = "+undefinedCount);
    }

    Atom atom;
    SpeciesAgent agent00, agent01, agent10, agent11;
    SpeciesMaster master0, master1;
    Atom[] atoms;
    int[] moleculeIndex, phaseIndex, speciesIndex, atomIndex;
    int i0;
    

}
