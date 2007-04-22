package etomica.normalmode;

import java.io.FileWriter;
import java.io.IOException;

import etomica.action.activity.ActivityIntegrate;
import etomica.atom.AtomFactoryMono;
import etomica.atom.AtomType;
import etomica.atom.AtomTypeSphere;
import etomica.config.ConfigurationLattice;
import etomica.data.AccumulatorAverage;
import etomica.data.DataFork;
import etomica.data.DataPump;
import etomica.data.AccumulatorAverage.StatType;
import etomica.data.types.DataDouble;
import etomica.data.types.DataDoubleArray;
import etomica.data.types.DataGroup;
import etomica.integrator.IntegratorHard;
import etomica.integrator.IntegratorMD;
import etomica.integrator.IntervalActionAdapter;
import etomica.lattice.BravaisLattice;
import etomica.lattice.LatticeCubicFcc;
import etomica.lattice.LatticeCubicSimple;
import etomica.math.SpecialFunctions;
import etomica.nbr.list.PotentialMasterList;
import etomica.phase.Phase;
import etomica.potential.P1HardPeriodic;
import etomica.potential.P2HardSphere;
import etomica.potential.Potential;
import etomica.simulation.Simulation;
import etomica.space.BoundaryRectangularPeriodic;
import etomica.space.Space;
import etomica.species.SpeciesSpheresMono;

/**
 * Simulation to run sampling with the hard sphere potential, but measuring
 * the harmonic potential based on normal mode data from a previous simulation.
 * 
 * @author Andrew Schultz
 */
public class SimTarget extends Simulation {

    public SimTarget(Space space, int numAtoms, double density) {
        super(space, true, new PotentialMasterList(space));

        
        defaults.makeLJDefaults();
        defaults.atomSize = 1.0;

        SpeciesSpheresMono species = new SpeciesSpheresMono(this);
        getSpeciesManager().addSpecies(species);

        phase = new Phase(this);
        phase.getAgent(species).setNMolecules(numAtoms);

        integrator = new IntegratorHard(this);

        integrator.setIsothermal(false);
        activityIntegrate = new ActivityIntegrate(this,
                integrator);
        double timeStep = 0.4;
        integrator.setTimeStep(timeStep);
        getController().addAction(activityIntegrate);

        Potential potential = new P2HardSphere(space, defaults.atomSize, false);
        AtomTypeSphere sphereType = (AtomTypeSphere) ((AtomFactoryMono) species
                .moleculeFactory()).getType();
        potentialMaster.addPotential(potential, new AtomType[] { sphereType,
                sphereType });

        bdry = new BoundaryRectangularPeriodic(this);
        phase.setBoundary(bdry);
        phase.setDensity(density);

        if (space.D() == 1) {
            lattice = new LatticeCubicSimple(1,phase.getBoundary().getDimensions().x(0)/numAtoms);
            if (numAtoms < 4) {
                ((IntegratorHard)integrator).setNullPotential(new P1HardPeriodic(space));
            }
        }
        else {
            lattice = new LatticeCubicFcc();
        }
        ConfigurationLattice config = new ConfigurationLattice(lattice);

        config.initializeCoordinates(phase);

        if (potentialMaster instanceof PotentialMasterList) {
            double neighborRange;
            if (space.D() == 1) {
                neighborRange = 1.01 / density;
            }
            else {
                //FCC
                double L = Math.pow(4.01/density, 1.0/3.0);
                neighborRange = L / Math.sqrt(2.0);
            }
            ((PotentialMasterList)potentialMaster).setRange(neighborRange);
            // find neighbors now.  Don't hook up NeighborListManager (neighbors won't change)
            ((PotentialMasterList)potentialMaster).getNeighborManager(phase).reset();
        }

        integrator.setPhase(phase);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        //set up simulation parameters
        int D = 3;
        int nA = 108;
        double density = 1.04;
        double harmonicFudge = 1;
        double simTime = 1000;
        if (D == 1) {
            nA = 10;
            density = 0.5;
            simTime = 400000;
        }
        String filename = "normal_modes"+D+"D";
        if (args.length > 0) {
            filename = args[0];
        }
        if (args.length > 1) {
            density = Double.parseDouble(args[1]);
        }
        if (args.length > 2) {
            simTime = Double.parseDouble(args[2]);
        }
        if (args.length > 3) {
            nA = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            harmonicFudge = Double.parseDouble(args[4]);
        }
        

        System.out.println("Running "+(D==1 ? "1D" : (D==3 ? "FCC" : "2D hexagonal")) +" hard sphere simulation, measuring harmonic energy");
        System.out.println(nA+" atoms at density "+density);
        System.out.println("harmonic fudge: "+harmonicFudge);
        System.out.println(simTime+" time units");
        System.out.println("output data to "+filename);

        //instantiate simulation
        SimTarget sim = new SimTarget(Space.getInstance(D), nA, density);
        
        NormalModes normalModes = null;
        if(D == 1) {
            normalModes = new NormalModes1DHR();
        } else {
            normalModes = new NormalModesFromFile(filename, D);
        }
        normalModes.setHarmonicFudge(harmonicFudge);

        //add meters to for FEP averages
        //this one does averaging of total energy and its Boltzmann factor
        //so long as we're using a MeterHarmonicSingleEnergy, we'll use that instead to get the sum
//        MeterHarmonicEnergy harmonicEnergy = new MeterHarmonicEnergy(new CoordinateDefinitionLeaf(sim.getSpace()), normalModes);
//        harmonicEnergy.setPhase(sim.phase);
//        DataPump foo = new DataPump(harmonicEnergy, null);
//        IntervalActionAdapter bar = new IntervalActionAdapter(foo, sim.integrator);
//        bar.setActionInterval(50);
        
        //this one does averaging of Boltzmann factors of each mode
        MeterHarmonicSingleEnergy harmonicSingleEnergy = new MeterHarmonicSingleEnergy(new CoordinateDefinitionLeaf(sim.getSpace()), normalModes);
        harmonicSingleEnergy.setPhase(sim.phase);
        DataPump pump = new DataPump(harmonicSingleEnergy, null);
        IntervalActionAdapter adapter = new IntervalActionAdapter(pump);
        adapter.setActionInterval(50);
        sim.integrator.addListener(adapter);

        DataFork harmonicSingleFork = new DataFork();
        pump.setDataSink(harmonicSingleFork);
        BoltzmannProcessor boltz = new BoltzmannProcessor();
        boltz.setTemperature(1.0);
        harmonicSingleFork.addDataSink(boltz);
        AccumulatorAverage harmonicSingleAvg = new AccumulatorAverage(10);
        boltz.setDataSink(harmonicSingleAvg);
        
        DataProcessorSum summer = new DataProcessorSum();
        harmonicSingleFork.addDataSink(summer);
        DataFork harmonicFork = new DataFork();
        summer.setDataSink(harmonicFork);
        AccumulatorAverage harmonicAvg = new AccumulatorAverage(10);
        harmonicFork.addDataSink(harmonicAvg);
        boltz = new BoltzmannProcessor();
        boltz.setTemperature(1.0);
        harmonicFork.addDataSink(boltz);
        AccumulatorAverage harmonicBoltzAvg = new AccumulatorAverage(10);
        boltz.setDataSink(harmonicBoltzAvg);

        //start simulation
        int nSteps = (int) (simTime / sim.integrator.getTimeStep());
        sim.activityIntegrate.setMaxSteps(nSteps);
        sim.getController().actionPerformed();

        //get averages and confidence limits for harmonic energy
        double avgHarmonicEnergy = ((DataDouble)((DataGroup)harmonicAvg.getData()).getData(AccumulatorAverage.StatType.AVERAGE.index)).x;
        double errorHarmonicEnergy = ((DataDouble)((DataGroup)harmonicAvg.getData()).getData(AccumulatorAverage.StatType.ERROR.index)).x;
        System.out.println("avg harmonic energy: "+avgHarmonicEnergy+" +/- "+errorHarmonicEnergy);
        
        //compute free-energy quantities, independent-mode approximation
        DataDoubleArray harmonicModesAvg = (DataDoubleArray)((DataGroup)harmonicSingleAvg.getData()).getData(StatType.AVERAGE.index);
        DataDoubleArray harmonicModesErr = (DataDoubleArray)((DataGroup)harmonicSingleAvg.getData()).getData(StatType.ERROR.index);
        double deltaA = 0;
        double deltaAerr = 0;
        int nData = harmonicModesAvg.getLength();
        for (int i=0; i<nData; i++) {
            deltaA += Math.log(harmonicModesAvg.getValue(i));
            deltaAerr += harmonicModesErr.getValue(i)/harmonicModesAvg.getValue(i);
        }
        System.out.println("Harmonic free energy correction (independent approx): "+deltaA+" +/- "+deltaAerr);
        
        double[][] eVals = normalModes.getEigenvalues(sim.phase);
        double[] coeffs = normalModes.getWaveVectorFactory().getCoefficients();
        double AHarmonic = 0.5*Math.log(nA) - 0.5*(nA-1)*Math.log(2.0*Math.PI);
        if(nA % 2 == 0) AHarmonic += 0.5*Math.log(2.0);
        int coordinateDim = 1;
        for(int i=0; i<eVals.length; i++) {
            for(int j=0; j<coordinateDim; j++) {
                AHarmonic -= coeffs[i]*Math.log(eVals[i][j]/coeffs[i]);//coeffs in log?
            }
        }

        //results for averaging without independent-mode approximation
        deltaA = ((DataDouble)((DataGroup)harmonicBoltzAvg.getData()).getData(StatType.AVERAGE.index)).x;
        deltaAerr = ((DataDouble)((DataGroup)harmonicBoltzAvg.getData()).getData(StatType.ERROR.index)).x/deltaA;
        deltaA = Math.log(deltaA);
        
        System.out.println("Harmonic free energy correction: "+deltaA+" +/- "+deltaAerr);
        System.out.println("Harmonic free energy correction per atom: "+deltaA/nA+" +/- "+deltaAerr/nA);
        
        System.out.println("Harmonic-reference free energy: "+AHarmonic);
        
        if(D==1) {
            double AHR = -(nA-1)*Math.log(nA/density-nA) + SpecialFunctions.lnFactorial(nA) ;
            System.out.println("Hard-rod free energy: "+AHR);
        }

        try {
            // write averages of exp(-u/kT) for each normal mode
            FileWriter fileWriterE = new FileWriter(filename+".e");
            int[] idx = new int[2];
            for (int i=0; i<harmonicModesAvg.getArrayShape(0); i++) {
                idx[0] = i;
                idx[1] = 0;
                fileWriterE.write(Double.toString(harmonicModesAvg.getValue(idx)));
                for (int j=1; j<harmonicModesAvg.getArrayShape(1); j++) {
                    idx[1] = j;
                    fileWriterE.write(" "+harmonicModesAvg.getValue(idx));
                }
                fileWriterE.write("\n");
            }
            fileWriterE.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Oops, failed to write data "+e);
        }
    }

    private static final long serialVersionUID = 1L;
    public IntegratorMD integrator;
    public ActivityIntegrate activityIntegrate;
    public Phase phase;
    public BoundaryRectangularPeriodic bdry;
    public BravaisLattice lattice;
}
