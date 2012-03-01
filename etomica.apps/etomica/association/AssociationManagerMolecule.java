package etomica.association;
import etomica.api.IBox;
import etomica.api.IMolecule;
import etomica.api.IMoleculeList;
import etomica.api.ISimulation;
import etomica.association.GCPMWater.AssociationDefinitionMolecule;
import etomica.atom.AtomArrayList;
import etomica.atom.MoleculeAgentManager;
import etomica.atom.MoleculeAgentManager.MoleculeAgentSource;
import etomica.atom.MoleculeArrayList;
import etomica.atom.iterator.MoleculeIterator;
import etomica.box.BoxAgentManager;
import etomica.integrator.mcmove.MCMoveEvent;
import etomica.integrator.mcmove.MCMoveMolecular;
import etomica.integrator.mcmove.MCMoveTrialCompletedEvent;
import etomica.nbr.cell.Api1ACell;
import etomica.nbr.cell.NeighborCellManager;
import etomica.nbr.cell.PotentialMasterCell;
import etomica.nbr.cell.molecule.Mpi1ACell;
import etomica.nbr.cell.molecule.NeighborCellManagerMolecular;
import etomica.util.IEvent;
import etomica.util.IListener;

/**
 * Class to define and track molecule associations.  Constructed given an iterator
 * that defines the set of molecules that is managed, and an association definition
 * that is used to determine if molecules are associated.  
 * To incorporate an instance of this class in a simulation, add an instance of 
 * this EnergySum inner class to the simulation:<br>
 *       sim.setEnergySum(associationManager.new EnergySum());
 *
 * If a MC simulation, this should be done before any MCMove classes are instantiated.
 * Also, register this as a listener to IntegratorMC:<br>
 *       integrator.addMCMoveListener(associationManager);
 *       
 *@author Hye Min Kim
 */
public class AssociationManagerMolecule implements MoleculeAgentSource,IListener {
    
    private AssociationDefinitionMolecule associationDefinition;
    private final IBox box;
    private final MoleculeAgentManager agentManager;
    private final Mpi1ACell neighborIterator;
    private final MoleculeArrayList associatedMolecules = new MoleculeArrayList();
    private final IListener mcMoveListener;

    public AssociationManagerMolecule(ISimulation sim,IBox box, BoxAgentManager cellAgentManager, AssociationDefinitionMolecule definition, double range) {
    	this.box = box;
    	agentManager = new MoleculeAgentManager(sim,box,this);
        associationDefinition = definition;
        this.neighborIterator = new Mpi1ACell(3,range,cellAgentManager);
        mcMoveListener = ((NeighborCellManagerMolecular)cellAgentManager.getAgent(box)).makeMCMoveListener(); 
        ((NeighborCellManagerMolecular)cellAgentManager.getAgent(box)).setDoApplyPBC(true);
        }
    
    public AssociationDefinitionMolecule getAssociationDefinition() {
    	return associationDefinition;
    }
    public void initialize() {
        IMoleculeList moleculeList = box.getMoleculeList();//list of all atoms in this box
        for (int i=0; i<moleculeList.getMoleculeCount();i+=1) {
        	IMolecule moleculei = moleculeList.getMolecule(i);
        	((MoleculeArrayList)agentManager.getAgent(moleculei)).clear();
        }
        for (int i=0; i<moleculeList.getMoleculeCount()-1;i+=1) {
        	IMolecule moleculei = moleculeList.getMolecule(i);//definition of atom i
        	for (int j=i+1; j<moleculeList.getMoleculeCount();j+=1) {
            	IMolecule moleculej = moleculeList.getMolecule(j);
            	if(associationDefinition.isAssociated(moleculei,moleculej)) {
                    ((MoleculeArrayList)agentManager.getAgent(moleculei)).add(moleculej);//i and j are associated
                    ((MoleculeArrayList)agentManager.getAgent(moleculej)).add(moleculei);
                }
        	}
         }
        associatedMolecules.clear();
        for (int i=0; i<moleculeList.getMoleculeCount();i+=1) {
        	IMolecule moleculei = moleculeList.getMolecule(i);
        	if(((MoleculeArrayList)agentManager.getAgent(moleculei)).getMoleculeCount() > 0){
        		associatedMolecules.add(moleculei);
        	}
        }
                
    }
    
    public IMoleculeList getAssociatedMolecules() {return associatedMolecules;}
    
    //need also to handle associatedAtoms list
    public void actionPerformed(IEvent evt) {
    	mcMoveListener.actionPerformed(evt);
    	MCMoveEvent mcEvent = (MCMoveEvent)evt;
        if(mcEvent instanceof MCMoveTrialCompletedEvent && ((MCMoveTrialCompletedEvent) mcEvent).isAccepted()) {
        	return;
            }
        
        MoleculeIterator iterator = ((MCMoveMolecular)mcEvent.getMCMove()).affectedMolecules(box);
        iterator.reset();
        neighborIterator.setBox(box);
        for (IMolecule moleculei = iterator.nextMolecule();moleculei != null; moleculei =iterator.nextMolecule()){
        	MoleculeArrayList listi = (MoleculeArrayList)agentManager.getAgent(moleculei);//list of the old bonds
        	if (listi.getMoleculeCount() > 0){
        		for (int i = 0; i<listi.getMoleculeCount();i++){
            		IMolecule moleculej = listi.getMolecule(i);
            		MoleculeArrayList listj = (MoleculeArrayList)agentManager.getAgent(moleculej);
        			listj.remove(listj.indexOf(moleculei));//remove atom i from the listj
        			if ( listj.getMoleculeCount() == 0) {
        				associatedMolecules.remove(associatedMolecules.indexOf(moleculej));
        			}
            	}
            	listi.clear();//remove all the elements from listi
            	associatedMolecules.remove(associatedMolecules.indexOf(moleculei));
        	}
        	
        	neighborIterator.setTarget(moleculei);
        	neighborIterator.reset();
        	for (IMoleculeList moleculeij = neighborIterator.next();moleculeij != null; moleculeij =neighborIterator.next()){
            	IMolecule moleculej = moleculeij.getMolecule(0);
            	if (moleculej == moleculei){
            		moleculej = moleculeij.getMolecule(1);
            	}
            	if (moleculeij == null) throw new RuntimeException();
            	if (associationDefinition.isAssociated(moleculei, moleculej)){ //they are associated
        			if (listi.getMoleculeCount() == 0) {
        				associatedMolecules.add(moleculei);
        			}
        			listi.add(moleculej); //make atom i and atom j to be associated
        			MoleculeArrayList listj = (MoleculeArrayList)agentManager.getAgent(moleculej);
        			if (listj.getMoleculeCount() == 0) {
        				associatedMolecules.add(moleculej);
        			}
        			listj.add(moleculei);//make atom i and atom j to be associated

            	}
        	}
        }
        for (IMolecule moleculei = iterator.nextMolecule();moleculei != null; moleculei =iterator.nextMolecule()){
        if (getAssociatedMolecules(moleculei).getMoleculeCount() > 1) {
        	//System.out.println("moleculei" +moleculei);
        } else if (getAssociatedMolecules(moleculei).getMoleculeCount() == 1){
        	IMolecule moleculej = getAssociatedMolecules(moleculei).getMolecule(0);
        	if (getAssociatedMolecules(moleculej).getMoleculeCount() == 0){
        		System.out.println("Wrong");
        	} else if(getAssociatedMolecules(moleculej).getMoleculeCount() > 1){
        		AtomArrayList listj = (AtomArrayList)agentManager.getAgent(moleculej);
        		System.out.println("Wrong:smer");
        		System.out.println("listj = "+listj+" moleculei= " +moleculei+" moleculej= "+moleculej);
        	} 
        }
        }
    }
    
        
    /**
     * Returns the number of atoms on the list of associations of the given atom.
     */
    public IMoleculeList getAssociatedMolecules(IMolecule molecule) {
       return (MoleculeArrayList)agentManager.getAgent(molecule);
    }

	public Class getMoleculeAgentClass() {
		return MoleculeArrayList.class;
	}

	public Object makeAgent(IMolecule a) {
		return new MoleculeArrayList();
	}

	public void releaseAgent(Object agent, IMolecule atom) {
		
	}
}
    