package etomica.threaded.domain;
import etomica.action.activity.ActivityIntegrate;
import etomica.action.activity.Controller;
import etomica.atom.AtomTypeSphere;
import etomica.chem.elements.Copper;
import etomica.chem.elements.Silver;
import etomica.chem.elements.Tin;
import etomica.config.Configuration;
import etomica.config.ConfigurationLattice;
import etomica.data.AccumulatorAverage;
import etomica.data.AccumulatorHistory;
import etomica.data.DataPump;
import etomica.data.IDataInfo;
import etomica.data.AccumulatorAverage.StatType;
import etomica.data.meter.MeterEnergy;
import etomica.data.meter.MeterKineticEnergy;
import etomica.data.meter.MeterPotentialEnergy;
import etomica.graphics.ColorSchemeByType;
import etomica.graphics.DisplayBox;
import etomica.graphics.DisplayPhase;
import etomica.graphics.DisplayPlot;
import etomica.graphics.SimulationGraphic;
import etomica.integrator.IntegratorVelocityVerlet;
import etomica.integrator.IntervalActionAdapter;
import etomica.lattice.BravaisLatticeCrystal;
import etomica.lattice.crystal.BasisCubicFcc;
import etomica.lattice.crystal.PrimitiveCubic;
import etomica.meam.DataProcessorCvMD;
import etomica.meam.ParameterSetMEAM;
import etomica.meam.PotentialMEAM;
import etomica.nbr.CriterionSimple;
import etomica.nbr.list.PotentialMasterList;
import etomica.phase.Phase;
import etomica.simulation.Simulation;
import etomica.space3d.Space3D;
import etomica.space3d.Vector3D;
import etomica.species.Species;
import etomica.species.SpeciesSpheresMono;
import etomica.threaded.IntegratorVelocityVerletThreaded;
import etomica.threaded.PotentialThreaded;
import etomica.units.CompoundUnit;
import etomica.units.Joule;
import etomica.units.Kelvin;
import etomica.units.Mole;
import etomica.units.Unit;
import etomica.util.HistoryCollapsingAverage;

/**
 * Molecular-Dynamics Simulation Using the Modified Embedded-Atom Method 
 * (MEAM) Potential.  
 * 
 * The MEAM potential is intended for use with metallic and covalently-bonded
 * solid systems.
 * 
 * The MEAM potential for an atom is built using terms describing parts of the
 * relationships between the atom and each of its neighbors, the number of which 
 * is determined by a cutoff and/or screening function.  Each type of pair-
 * wise term is summed over all the neighbors, and then used in expressions 
 * describing the embedding energy and the repulsive energy of the atom.   
 * Effectively, the MEAM potential is a many-body potential.  
 * 
 * This class was adapted from LjMd3D.java by K.R. Schadel and A. Schultz in July 
 * 2005.  Intitially, it employed a version of the embedded-atom method potential, 
 * and was later adapted in February 2006 to use the modified embedded-atom method
 * potential.
 */
 
public class MEAMMd3DThreaded extends Simulation {
    
    private static final long serialVersionUID = 1L;
    private static final String APP_NAME = "MEAM Md3D Threaded";
    public PotentialMasterListThreaded potentialMaster;
    public IntegratorVelocityVerlet integrator;
    public SpeciesSpheresMono sn;
    public SpeciesSpheresMono ag;
    public SpeciesSpheresMono cu;
    public Phase phase;
    public PotentialMEAM[] potentialN;
    public PotentialThreaded potentialThreaded;
    public Controller controller;
    public DisplayPhase display;
    public DisplayPlot plot;
    public MeterEnergy energy;
    public ActivityIntegrate activityIntegrate;
    public IDataInfo info2;

    public static void main(String[] args) {
        int numAtoms = 500;
        int numThreads = 1;
    	MEAMMd3DThreaded sim = new MEAMMd3DThreaded(numAtoms, numThreads);
    	
    	MeterPotentialEnergy energyMeter = new MeterPotentialEnergy(sim.potentialMaster);
    	MeterKineticEnergy kineticMeter = new MeterKineticEnergy();
    	
    	energyMeter.setPhase(sim.phase);
    	kineticMeter.setPhase(sim.phase);
        
        AccumulatorHistory energyAccumulator = new AccumulatorHistory(new HistoryCollapsingAverage());
        AccumulatorHistory kineticAccumulator = new AccumulatorHistory(new HistoryCollapsingAverage());
        
        AccumulatorAverage accumulatorAveragePE = new AccumulatorAverage(50);
    	AccumulatorAverage accumulatorAverageKE = new AccumulatorAverage(50);
    	
    	DataPump energyManager = new DataPump(energyMeter,accumulatorAveragePE);   	
    	DataPump kineticManager = new DataPump(kineticMeter, accumulatorAverageKE);
    	
    	accumulatorAveragePE.addDataSink(energyAccumulator, new StatType[]{StatType.MOST_RECENT});
    	accumulatorAverageKE.addDataSink(kineticAccumulator, new StatType[]{StatType.MOST_RECENT});
    	
    	DisplayPlot plotPE = new DisplayPlot();
    	plotPE.setLabel("PE Plot");
        DisplayPlot plotKE = new DisplayPlot();
        plotKE.setLabel("KE Plot");

        energyAccumulator.setDataSink(plotPE.getDataSet().makeDataSink());
        kineticAccumulator.setDataSink(plotKE.getDataSet().makeDataSink());
        //energyAccumulator.setBlockSize(50);
        
    	accumulatorAveragePE.setPushInterval(1);
    	accumulatorAverageKE.setPushInterval(1);
    	
    	sim.register(energyMeter, energyManager);
    	sim.register(kineticMeter, kineticManager);
    	
    	//Heat Capacity (PE)
    	DataProcessorCvMD dataProcessorPE = new DataProcessorCvMD();
    	dataProcessorPE.setIntegrator(sim.integrator);
    	
    	//Heat Capacity (KE)
    	DataProcessorCvMD dataProcessorKE = new DataProcessorCvMD();
    	dataProcessorKE.setIntegrator(sim.integrator);
    	
    	accumulatorAveragePE.addDataSink(dataProcessorPE, new StatType[]{StatType.STANDARD_DEVIATION});
    	accumulatorAverageKE.addDataSink(dataProcessorKE, new StatType[]{StatType.STANDARD_DEVIATION});
    	  	
        IntervalActionAdapter adapter = new IntervalActionAdapter(energyManager, sim.integrator);
        adapter.setActionInterval(1);
        IntervalActionAdapter kineticAdapter = new IntervalActionAdapter(kineticManager, sim.integrator);
        kineticAdapter.setActionInterval(1);    

        SimulationGraphic simGraphic = new SimulationGraphic(sim, SimulationGraphic.TABBED_PANE, APP_NAME);

    	DisplayBox cvBoxPE = new DisplayBox();
    	dataProcessorPE.setDataSink(cvBoxPE);
    	cvBoxPE.setUnit(new CompoundUnit(new Unit[]{Joule.UNIT, Kelvin.UNIT, Mole.UNIT}, new double []{1,-1,-1}));
    	cvBoxPE.setLabel("PE Cv contrib.");
    	DisplayBox cvBoxKE = new DisplayBox();
    	dataProcessorKE.setDataSink(cvBoxKE);
    	cvBoxKE.setUnit(new CompoundUnit(new Unit[]{Joule.UNIT, Kelvin.UNIT, Mole.UNIT}, new double []{1,-1,-1}));
    	cvBoxKE.setLabel("KE Cv contrib.");

    	simGraphic.add(plotPE);
    	simGraphic.add(plotKE);

    	simGraphic.add(cvBoxKE);
    	simGraphic.add(cvBoxPE);

    	ColorSchemeByType colorScheme = ((ColorSchemeByType)((DisplayPhase)simGraphic.displayList().getFirst()).getColorScheme());
    	colorScheme.setColor(sim.sn.getMoleculeType(),java.awt.Color.blue);
    	colorScheme.setColor(sim.ag.getMoleculeType(),java.awt.Color.gray);
    	colorScheme.setColor(sim.cu.getMoleculeType(),java.awt.Color.orange);

    	simGraphic.makeAndDisplayFrame(APP_NAME);

    	//sim.activityIntegrate.setMaxSteps(1000);
    	//sim.getController().run();
    	//DataGroup data = (DataGroup)energyAccumulator.getData(); // kmb change type to Data instead of double[]
        //double PE = ((DataDouble)data.getData(AccumulatorAverage.StatType.AVERAGE.index)).x
                    // /sim.species.getAgent(sim.phase).getNMolecules();  // kmb changed 8/3/05
        //double PE = data[AccumulatorAverage.AVERAGE.index]/sim.species.getAgent(sim.phase).getNMolecules();  // orig line
        //System.out.println("PE(eV)="+ElectronVolt.UNIT.fromSim(PE));
    }
    
    public MEAMMd3DThreaded(int numAtoms, int numThreads) {
        super(Space3D.getInstance(), true); //INSTANCE); kmb change 8/3/05
        potentialMaster = new PotentialMasterListThreaded(this);
        integrator = new IntegratorVelocityVerletThreaded(this, potentialMaster, numThreads);
        integrator.setTimeStep(0.001);
        integrator.setTemperature(Kelvin.UNIT.toSim(295));
        integrator.setThermostatInterval(100);
        integrator.setIsothermal(true);
        activityIntegrate = new ActivityIntegrate(this,integrator);
        activityIntegrate.setSleepPeriod(2);
        getController().addAction(activityIntegrate);
        sn = new SpeciesSpheresMono(this, Tin.INSTANCE);
        ag = new SpeciesSpheresMono(this, Silver.INSTANCE);
        cu = new SpeciesSpheresMono(this, Copper.INSTANCE);

        getSpeciesManager().addSpecies(sn);
        getSpeciesManager().addSpecies(ag);
        getSpeciesManager().addSpecies(cu);

        /** The following values come from either the ASM Handbook or Cullity & Stock's 
         * "Elements of X-Ray Diffraction" (2001)
         */
        ((AtomTypeSphere)sn.getFactory().getType()).setDiameter(3.022); 
        
        ((AtomTypeSphere)ag.getFactory().getType()).setDiameter(2.8895); 
        
        ((AtomTypeSphere)cu.getFactory().getType()).setDiameter(2.5561); 
        
        
        phase = new Phase(this);
        addPhase(phase);
        phase.getAgent(sn).setNMolecules(0);
        phase.getAgent(ag).setNMolecules(numAtoms);
        phase.getAgent(cu).setNMolecules(0);
        
        // beta-Sn phase
        /**
        //The dimensions of the simulation box must be proportional to those of
        //the unit cell to prevent distortion of the lattice.  The values for the 
        //lattice parameters for tin's beta phase (a = 5.8314 angstroms, c = 3.1815 
        //angstroms) are taken from the ASM Handbook. 
        phase.setDimensions(new Vector3D(5.8314*3, 5.8314*3, 3.1815*6));
        PrimitiveTetragonal primitive = new PrimitiveTetragonal(space, 5.8318, 3.1819);
        //Alternatively, using the parameters calculated in Ravelo & Baskes (1997)
        //phase.setDimensions(new Vector3D(5.92*3, 5.92*3, 3.23*6));
        //PrimitiveTetragonal primitive = new PrimitiveTetragonal(space, 5.92, 3.23);
        LatticeCrystal crystal = new LatticeCrystal(new Crystal(
        		primitive, new BasisBetaSnA5(primitive)));
		*/
		
        //FCC Cu
		/**
	    phase.setDimensions(new Vector3D(3.6148*4, 3.6148*4, 3.6148*4));
	    PrimitiveCubic primitive = new PrimitiveCubic(space, 3.6148);
	    LatticeCrystal crystal = new LatticeCrystal(new Crystal(
		        primitive, new BasisCubicFcc(primitive)));
	    */
        
        //FCC Ag
        
	    phase.setDimensions(new Vector3D(4.0863*4, 4.0863*4, 4.0863*4));
	    PrimitiveCubic primitive = new PrimitiveCubic(space, 4.0863);
	    BravaisLatticeCrystal crystal = new BravaisLatticeCrystal(primitive, new BasisCubicFcc());
	    
           
		Configuration config = new ConfigurationLattice(crystal);
		config.initializeCoordinates(phase);
        
        
        
        
        potentialN = new PotentialMEAM[numThreads];
        
        for(int i=0; i<numThreads; i++){
            potentialN[i] = new PotentialMEAM(space);
            potentialN[i].setParameters(sn, ParameterSetMEAM.Sn);
            potentialN[i].setParameters(ag, ParameterSetMEAM.Ag);
            potentialN[i].setParameters(cu, ParameterSetMEAM.Cu);
            potentialN[i].setParametersIMC(cu, ParameterSetMEAM.Cu3Sn);
            potentialN[i].setParametersIMC(ag, ParameterSetMEAM.Ag3Sn); 
        }
        
		potentialThreaded = new PotentialThreaded(space, potentialN);
       
        potentialMaster.addPotential(potentialThreaded, new Species[]{sn, ag, cu});  
        
        ((PotentialMasterListThreaded)potentialMaster).setNumThreads(numThreads, phase);
        
        ((PotentialMasterListThreaded)potentialMaster).setRange(potentialThreaded.getRange()*1.1);
        ((PotentialMasterListThreaded)potentialMaster).setCriterion(potentialThreaded, new CriterionSimple(this, potentialThreaded.getRange(), potentialThreaded.getRange()*1.1));   
        integrator.addListener(((PotentialMasterList)potentialMaster).getNeighborManager(phase));
        
        
        integrator.setPhase(phase);
        
        
        // IntegratorCoordConfigWriter - Displacement output (3/1/06 - MS)
        //IntegratorCoordConfigWriter coordWriter = new IntegratorCoordConfigWriter(space, "MEAMoutput");
        //coordWriter.setPhase(phase);
        //coordWriter.setIntegrator(integrator);
        //coordWriter.setWriteInterval(100);
        
        // Control simulation lengths
        //activityIntegrate.setMaxSteps(500);

		energy = new MeterEnergy(potentialMaster);
    }
    
}