package etomica.data.meter;
import etomica.EtomicaElement;
import etomica.EtomicaInfo;
import etomica.data.DataSourceScalar;
import etomica.data.DataSourceTensorVirialHard;
import etomica.data.types.DataTensor;
import etomica.integrator.IntegratorHard;
import etomica.phase.Phase;
import etomica.space.Space;
import etomica.space.Tensor;
import etomica.units.Dimension;

/**
 * This is a meter to measure the surface tension for a hard potential.  
 * It uses the meter that measures the pressure tensor to do this
 * and returns the result as PV/N, which has dimensions of energy.
 * Assumes a slab geometry for the liquid, so surface tension is divided by 2 to account for both interfaces.
 *
 * @author Rob Riggleman
 */

public class MeterSurfaceTensionHard extends DataSourceScalar implements EtomicaElement {
    
    public MeterSurfaceTensionHard(Space space, IntegratorHard integrator) {
        super("Surface Tension",Dimension.ENERGY);
        velocityTensor = new MeterTensorVelocity(space);
        velocityTensor.setPhase(integrator.getPhase());
        virialTensor = new DataSourceTensorVirialHard(space,integrator);
        pressureTensor = space.makeTensor();
    }
    
    public static EtomicaInfo getEtomicaInfo() {
        EtomicaInfo info = new EtomicaInfo("Measures surface tension for a collision-based potential");
        return info;
    }

    /**
     * Gives current value of the surface tension, obtained by summing velocity and virial contributions.
     * Virial contribution includes sum over all collisions since last call to this method, while
     * velocity contribution is based on atom velocities in current configuration.
     * Surface tension is given by difference between normal and tangential stress components.
     * Written for 2-, or 3-dimensional systems; assumes that normal to interface is along x-axis.
     */
    public double getDataAsScalar() {
        pressureTensor.E(((DataTensor)velocityTensor.getData()).x);
        pressureTensor.PE(((DataTensor)virialTensor.getData()).x);
        switch (pressureTensor.length()) {
            case 1:
                surfaceTension = pressureTensor.component(0, 0);
                break;
            case 2:
                surfaceTension = 0.5*(pressureTensor.component(0, 0) - pressureTensor.component(1, 1));
                break;
            case 3:
                surfaceTension = 0.5*(pressureTensor.component(0, 0) - 0.5*(pressureTensor.component(1, 1) + pressureTensor.component(2, 2)));
                break;
        }
        return surfaceTension;
    }
    
    /**
     * @return Returns the phase.
     */
    public Phase getPhase() {
        return velocityTensor.getPhase();
    }

    public void setIntegrator(IntegratorHard integrator) {
        velocityTensor.setPhase(integrator.getPhase());
        virialTensor.setIntegrator(integrator);
    }
    
    private final Tensor pressureTensor;
    private final MeterTensorVelocity velocityTensor;
    private final DataSourceTensorVirialHard virialTensor;
    private double surfaceTension;
}