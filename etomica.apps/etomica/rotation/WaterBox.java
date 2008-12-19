package etomica.rotation;

import java.awt.Color;

import etomica.action.BoxImposePbc;
import etomica.action.activity.ActivityIntegrate;
import etomica.api.ISpecies;
import etomica.box.Box;
import etomica.graphics.ColorSchemeByType;
import etomica.graphics.SimulationGraphic;
import etomica.integrator.IntegratorRigidIterative;
import etomica.models.water.DipoleSourceWater;
import etomica.models.water.OrientationCalcWater3P;
import etomica.models.water.P2WaterSPCSoft;
import etomica.models.water.SpeciesWater3POriented;
import etomica.potential.P2MoleculeSoftTruncatedSwitched;
import etomica.potential.P2ReactionFieldDipole;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.space.BoundaryRectangularPeriodic;
import etomica.space.Space;
import etomica.space3d.Space3D;
import etomica.units.Electron;
import etomica.units.Kelvin;
import etomica.util.Constants;

public class WaterBox {

    public static SimulationGraphic makeWaterBox() {
        Space space = Space3D.getInstance();
        Simulation sim = new Simulation(Space3D.getInstance());
        Box box = new Box(new BoundaryRectangularPeriodic(sim.getSpace(), 10), space);
        sim.addBox(box);
        SpeciesWater3POriented species = new SpeciesWater3POriented(sim.getSpace(), true);
        sim.getSpeciesManager().addSpecies(species);
        box.setNMolecules(species, 256);
        box.setDensity(1/18.0*Constants.AVOGADRO/1E24);
        ConfigurationWater256 configFile = new ConfigurationWater256();
        configFile.initializeCoordinates(box);
//        SpeciesSpheresMono speciesOrient = new SpeciesSpheresMono(sim);
//        sim.getSpeciesManager().addSpecies(speciesOrient);
//        box.setNMolecules(speciesOrient, 1);
        PotentialMaster potentialMaster = new PotentialMaster();
        double timeInterval = 0.004;
        int maxIterations = 20;
        IntegratorRigidIterative integrator = new IntegratorRigidIterative(sim, potentialMaster, timeInterval, 1, space);
        integrator.printInterval = 10;
        integrator.setMaxIterations(maxIterations);
        integrator.setBox(box);
        OrientationCalcWater3P calcer = new OrientationCalcWater3P(sim.getSpace());
//        integrator.setOrientAtom((IAtomPositioned)((IMolecule)box.getMoleculeList(speciesOrient).getAtom(0)).getChildList().getAtom(0));
        integrator.setOrientationCalc(species, calcer);
//        integrator.setIsothermal(true);
        integrator.setTemperature(Kelvin.UNIT.toSim(298));
        integrator.setThermostatInterval(100);
        ActivityIntegrate ai = new ActivityIntegrate(integrator);
//        System.out.println("using rigid with dt="+dt);
        sim.getController().addAction(ai);
//        System.out.println("h1 at "+((IAtomPositioned)box.getLeafList().getAtom(0)).getPosition());
//        System.out.println("o at "+((IAtomPositioned)box.getLeafList().getAtom(2)).getPosition());

        P2WaterSPCSoft p2Water = new P2WaterSPCSoft(sim.getSpace());

        BoxImposePbc pbc = new BoxImposePbc(box, space);
        pbc.setApplyToMolecules(true);
        integrator.addIntervalAction(pbc);

        double boxlength = box.getBoundary().getDimensions().x(0);
        System.out.println(boxlength);

        DipoleSourceWater dipoleSource = new DipoleSourceWater(sim.getSpace());
        dipoleSource.setDipoleStrength(2*Electron.UNIT.toSim(0.41)*Math.cos(109.5/2.0*Math.PI/180));
        P2ReactionFieldDipole pNRF = new P2ReactionFieldDipole(sim.getSpace());
        pNRF.setDipoleSource(dipoleSource);
        pNRF.setRange(boxlength*0.49);
        pNRF.setDielectric(78.4);

        P2MoleculeSoftTruncatedSwitched p2Switched = new P2MoleculeSoftTruncatedSwitched(pNRF, boxlength*0.49, space);
        p2Switched.setSwitchFac(0.5);
        potentialMaster.addPotential(p2Switched, new ISpecies[]{species, species});
        potentialMaster.lrcMaster().addPotential(pNRF.makeP0());

        p2Switched = new P2MoleculeSoftTruncatedSwitched(p2Water, boxlength*0.49, space);
        p2Switched.setSwitchFac(0.5);
        potentialMaster.addPotential(p2Switched, new ISpecies[]{species,species});

        ai.setSleepPeriod(2);
        SimulationGraphic graphic = new SimulationGraphic(sim, "Rigid", 1, space, sim.getController());
        ((ColorSchemeByType)graphic.getDisplayBox(box).getColorScheme()).setColor(species.getHydrogenType(), Color.WHITE);
        ((ColorSchemeByType)graphic.getDisplayBox(box).getColorScheme()).setColor(species.getOxygenType(), Color.RED);
        return graphic;
    }
    public static void main(String[] args) {
        SimulationGraphic graphic = makeWaterBox();
        graphic.makeAndDisplayFrame();
    }
    
    public static class Applet extends javax.swing.JApplet {

        public void init() {
            SimulationGraphic graphic = makeWaterBox();

            getContentPane().add(graphic.getPanel());
        }

        private static final long serialVersionUID = 1L;
    }
}
