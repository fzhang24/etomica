package etomica.integrator;

import etomica.EtomicaElement;
import etomica.EtomicaInfo;
import etomica.atom.Atom;
import etomica.atom.AtomAgentManager;
import etomica.atom.AtomLeaf;
import etomica.atom.AtomTypeLeaf;
import etomica.atom.AtomAgentManager.AgentSource;
import etomica.atom.iterator.IteratorDirective;
import etomica.exception.ConfigurationOverlapException;
import etomica.phase.Phase;
import etomica.potential.PotentialCalculationForceSum;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.space.ICoordinateKinetic;
import etomica.space.IVector;
import etomica.space.Space;
import etomica.util.IRandom;

public final class IntegratorVerlet extends IntegratorMD implements EtomicaElement, AgentSource {

    private static final long serialVersionUID = 1L;
    public final PotentialCalculationForceSum forceSum;
    private final IteratorDirective allAtoms;
    private double t2;

    IVector work;

    protected AtomAgentManager agentManager;

    public IntegratorVerlet(Simulation sim) {
        this(sim.getPotentialMaster(),sim.getRandom(),sim.getDefaults().timeStep,
                sim.getDefaults().temperature);
    }
    
    public IntegratorVerlet(PotentialMaster potentialMaster, IRandom random, 
            double timeStep, double temperature) {
        super(potentialMaster,random,timeStep,temperature);
        forceSum = new PotentialCalculationForceSum();
        allAtoms = new IteratorDirective();
        // allAtoms is used only for the force calculation, which has no LRC
        allAtoms.setIncludeLrc(false);
        work = potentialMaster.getSpace().makeVector();
    }

    public static EtomicaInfo getEtomicaInfo() {
        EtomicaInfo info = new EtomicaInfo("Molecular dynamics using basic Verlet algorithm");
        return info;
    }
        
    public final void setTimeStep(double t) {
        super.setTimeStep(t);
        t2 = timeStep * timeStep;
    }
          
    public void setPhase(Phase p) {
        if (phase != null) {
            // allow agentManager to de-register itself as a PhaseListener
            agentManager.dispose();
        }
        super.setPhase(p);
        agentManager = new AtomAgentManager(this,p);
    }
    
//--------------------------------------------------------------
// steps all particles across time interval tStep

    public void doStep() {

        //Compute forces on each atom
        atomIterator.reset();
        while(atomIterator.hasNext()) {   //zero forces on all atoms
            ((Agent)agentManager.getAgent(atomIterator.nextAtom())).force.E(0.0);
        }
        potential.calculate(phase, allAtoms, forceSum);

        //take step
        atomIterator.reset();
        while(atomIterator.hasNext()) {
            AtomLeaf a = (AtomLeaf)atomIterator.nextAtom();
            Agent agent = (Agent)agentManager.getAgent(a);
            IVector r = a.getCoord().getPosition();
            work.E(r);
            r.PE(agent.rMrLast);
            agent.force.TE(((AtomTypeLeaf)a.getType()).rm()*t2);
            r.PE(agent.force);
            agent.rMrLast.E(r);
            agent.rMrLast.ME(work);
        }
    }//end of doStep
    

    public void reset() throws ConfigurationOverlapException {
        // reset might be called because atoms were added or removed
        // calling getAgents ensures we have an up-to-date array.
        forceSum.setAgentManager(agentManager);

        atomIterator.reset();
        while(atomIterator.hasNext()) {
            AtomLeaf a = (AtomLeaf)atomIterator.nextAtom();
            Agent agent = (Agent)agentManager.getAgent(a);
            agent.rMrLast.Ea1Tv1(timeStep,((ICoordinateKinetic)a.getCoord()).getVelocity());//06/13/03 removed minus sign before timeStep
        }
        super.reset();
    }
              
//--------------------------------------------------------------
    
    public Class getAgentClass() {
        return Agent.class;
    }

    public final Object makeAgent(Atom a) {
        return new Agent(potential.getSpace());
    }
    
    public void releaseAgent(Object agent, Atom atom) {}
            
	public final static class Agent implements IntegratorPhase.Forcible {  //need public so to use with instanceof
        public IVector force;
        public IVector rMrLast;  //r - rLast

        public Agent(Space space) {
            force = space.makeVector();
            rMrLast = space.makeVector();
        }
        
        public IVector force() {return force;}
    }//end of Agent
    
}//end of IntegratorVerlet

