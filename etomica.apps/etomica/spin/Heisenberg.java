package etomica.spin;

import etomica.action.activity.ActivityIntegrate;
import etomica.atom.AtomType;
import etomica.data.AccumulatorAverage;
import etomica.data.DataPump;
import etomica.graphics.DeviceSlider;
import etomica.graphics.DisplayBox;
import etomica.graphics.DisplayBoxesCAE;
import etomica.graphics.DisplayPhase;
import etomica.graphics.DisplayPhaseSpin2D;
import etomica.graphics.SimulationGraphic;
import etomica.integrator.IntegratorMC;
import etomica.integrator.IntervalActionAdapter;
import etomica.nbr.site.NeighborSiteManager;
import etomica.nbr.site.PotentialMasterSite;
import etomica.phase.Phase;
import etomica.phase.PhaseAgentManager;
import etomica.simulation.Simulation;
import etomica.space.Space;
import etomica.space2d.Space2D;
import etomica.species.Species;
import etomica.species.SpeciesSpheresMono;
import etomica.units.systems.LJ;


/**
 * Simulation of a simple 2D Ising model.  Prototype
 * for simulation of a more general magentic system.
 *
 * @author David Kofke
 *
 */
public class Heisenberg extends Simulation {

	private static final String APP_NAME = "Heisenberg";

    public Heisenberg() {
        this(Space2D.getInstance(),60);
    }
    
    /**
     * 
     */
    public Heisenberg(Space space, int nCells) {
        super(space, false);
        potentialMaster = new PotentialMasterSite(this, nCells);
        defaults.makeLJDefaults();
        phase = new Phase(this);
        addPhase(phase);
        int numAtoms = space.powerD(nCells);
        spins = new SpeciesSpheresMono(this);
        getSpeciesManager().addSpecies(spins);
        phase.getAgent(spins).setNMolecules(numAtoms);
        new ConfigurationAligned().initializeCoordinates(phase);
        
        potential = new P2Spin(space);
        field = new P1MagneticField(space);
        integrator = new IntegratorMC(this, potentialMaster);
        mcmove = new MCMoveSpinFlip(potentialMaster, getRandom());
        integrator.getMoveManager().addMCMove(mcmove);
        
        ActivityIntegrate activityIntegrate = new ActivityIntegrate(this,integrator);
        activityIntegrate.setDoSleep(false);
        activityIntegrate.setSleepPeriod(1);
        getController().addAction(activityIntegrate);

        AtomType type = spins.getFactory().getType();
        potentialMaster.addPotential(field, new AtomType[] {type});
        potentialMaster.addPotential(potential, new AtomType[] {type, type});
        
        integrator.setPhase(phase);
        
        meter = new MeterSpin(space);
        meter.setPhase(phase);
        dAcc = new AccumulatorAverage(this);
        pump = new DataPump(meter, dAcc);
        adapter = new IntervalActionAdapter(pump,integrator);
        adapter.setActionInterval(10);

    }

    private static final long serialVersionUID = 2L;
    public PotentialMasterSite potentialMaster;
    public Phase phase;
    public Species spins;
    public P2Spin potential;
    public P1MagneticField field;
    private IntegratorMC integrator;
    public MCMoveSpinFlip mcmove;
    public MeterSpin meter;
    public DataPump pump;
    public IntervalActionAdapter adapter;
    public AccumulatorAverage dAcc;
    
    public static void main(String[] args) {
        Heisenberg sim = new Heisenberg(Space2D.getInstance(), 60);
        sim.register(sim.integrator);
        SimulationGraphic simGraphic = new SimulationGraphic(sim, APP_NAME);
        DisplayPhase displayPhase = simGraphic.getDisplayPhase(sim.phase);
        simGraphic.remove(displayPhase);
        PhaseAgentManager phaseAgentManager = sim.potentialMaster.getCellAgentManager();
        NeighborSiteManager neighborSiteManager = (NeighborSiteManager)phaseAgentManager.getAgent(sim.phase);
        displayPhase.setPhaseCanvas(new DisplayPhaseSpin2D(displayPhase,neighborSiteManager));
        simGraphic.add(displayPhase);
        DeviceSlider temperatureSlider = new DeviceSlider(sim.getController(), sim.integrator,"temperature");
        temperatureSlider.setMinimum(0.5);
        temperatureSlider.setMaximum(10.0);
        temperatureSlider.setShowBorder(true);
        LJ lj = new LJ();
        temperatureSlider.setUnit(lj.temperature());
        simGraphic.add(temperatureSlider);
        temperatureSlider.setValue(sim.integrator.getTemperature());
        DeviceSlider fieldSlider = new DeviceSlider(sim.getController(), sim.field, "h");
        fieldSlider.setMinimum(-5.);
        fieldSlider.setMaximum(+5.);
        fieldSlider.setNMajor(5);
        fieldSlider.setValue(0.0);
        fieldSlider.setShowBorder(true);
        fieldSlider.setLabel("Magnetic field");
        simGraphic.add(fieldSlider);
        
        DisplayBoxesCAE boxes = new DisplayBoxesCAE();
        boxes.setAccumulator(sim.dAcc);
        boxes.setLabel("Magnetization");
        boxes.setLabelType(DisplayBox.LabelType.BORDER);
        simGraphic.add(boxes);

        simGraphic.makeAndDisplayFrame(APP_NAME);
    }
}
