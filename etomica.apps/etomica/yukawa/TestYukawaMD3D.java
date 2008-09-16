package etomica.yukawa;

import java.awt.Color;

import etomica.api.IAction;
import etomica.api.IBox;
import etomica.api.ISpecies;

import etomica.action.SimulationRestart;
import etomica.action.activity.ActivityIntegrate;
import etomica.config.ConfigurationLattice;
import etomica.graphics.ColorSchemeByType;
import etomica.graphics.DeviceNSelector;
import etomica.graphics.DisplayBox;
import etomica.graphics.SimulationGraphic;
import etomica.integrator.IntegratorVelocityVerlet;
import etomica.lattice.LatticeCubicFcc;
import etomica.nbr.list.NeighborListManager;
import etomica.nbr.list.PotentialMasterList;
import etomica.box.Box;
import etomica.potential.P2SoftSphericalTruncated;
import etomica.simulation.Simulation;
import etomica.space.Space;
import etomica.space3d.Space3D;
import etomica.species.SpeciesSpheresMono;

/**
 * A Yukawa MD Simulation
 * 
 * adapted from HSMD3D simulation prototype.  employs neighbor listing.
 * 
 * @author msellers
 *
 */

public class TestYukawaMD3D extends Simulation{

    private static final long serialVersionUID = 1L;
    private static final String APP_NAME = "Test Yukawa MD3D";
    public final IBox box;
	public final IntegratorVelocityVerlet integrator;
	public final SpeciesSpheresMono species;
	public final P2Yukawa potential;
	
	public TestYukawaMD3D(){
		super(Space3D.getInstance(), true);
        PotentialMasterList potentialMaster = new PotentialMasterList(this, 1.6, space);
		
		int numAtoms = 256;
		double neighborRangeFac = 1.6;
		
		double l = 14.4573*Math.pow((numAtoms/2020.0),1.0/3.0);
		
		potentialMaster.setRange(neighborRangeFac);
		
		integrator = new IntegratorVelocityVerlet(this, potentialMaster, space);
		integrator.setIsothermal(false);
		integrator.setTimeStep(0.01);
		
		
		ActivityIntegrate activityIntegrate = new ActivityIntegrate(integrator);
		activityIntegrate.setSleepPeriod(1);
		getController().addAction(activityIntegrate);
		
		species = new SpeciesSpheresMono(this, space);
        getSpeciesManager().addSpecies(species);
		box = new Box(this, space);
        box.getBoundary().setDimensions(space.makeVector(new double[]{l,l,l}));
        addBox(box);
        box.setNMolecules(species, numAtoms);
        NeighborListManager nbrManager = potentialMaster.getNeighborManager(box);
        integrator.addNonintervalListener(nbrManager);
        integrator.addIntervalAction(nbrManager);
		potential = new P2Yukawa(space);
		
		double truncationRadius = 2.5*potential.getKappa();
		if(truncationRadius > 0.5*box.getBoundary().getDimensions().x(0)){
			throw new RuntimeException("Truncaiton radius too large.  Max allowed is "+0.5*box.getBoundary().getDimensions().x(0));
		}
		P2SoftSphericalTruncated potentialTruncated = new P2SoftSphericalTruncated(space, potential, truncationRadius);
		potentialMaster.setCellRange(3);
		potentialMaster.setRange(potentialTruncated.getRange()*1.2);
		potentialMaster.addPotential(potentialTruncated, new ISpecies[] {species, species});
		
		new ConfigurationLattice(new LatticeCubicFcc(space), space).initializeCoordinates(box);
		integrator.setBox(box);
	}
	
	public static void main(String[] args){
		TestYukawaMD3D sim = new TestYukawaMD3D();
		final SimulationGraphic simGraphic = new SimulationGraphic(sim, APP_NAME, sim.space, sim.getController());
		IAction repaintAction = simGraphic.getPaintAction(sim.box);

        DeviceNSelector nSelector = new DeviceNSelector(sim.getController());
        nSelector.setResetAction(new SimulationRestart(sim, sim.space));
        nSelector.setPostAction(repaintAction);
        nSelector.setSpecies(sim.species);
        nSelector.setBox(sim.box);
		simGraphic.add(nSelector);
		simGraphic.getController().getReinitButton().setPostAction(repaintAction);
		simGraphic.makeAndDisplayFrame(APP_NAME);
		ColorSchemeByType colorScheme = ((ColorSchemeByType)((DisplayBox)simGraphic.displayList().getFirst()).getColorScheme());
		colorScheme.setColor(sim.species.getLeafType(), Color.red);
	}
}
