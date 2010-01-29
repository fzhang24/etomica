package etomica.models.nitrogen;

import etomica.action.BoxInflate;
import etomica.api.IBox;
import etomica.api.IPotentialMaster;
import etomica.api.IRandom;
import etomica.api.ISimulation;
import etomica.api.ISpecies;
import etomica.api.IVectorMutable;
import etomica.atom.iterator.AtomIterator;
import etomica.atom.iterator.AtomIteratorLeafAtoms;
import etomica.data.meter.MeterPotentialEnergy;
import etomica.integrator.mcmove.MCMoveBoxStep;
import etomica.space.ISpace;
import etomica.units.Dimension;
import etomica.units.Kelvin;
import etomica.units.Pressure;

/**
 * Monte Carlo volume-change move for simulations in the NPT ensemble.
 * for Nitrogen molecule.
 * This class is created to take the energy correction into account. 
 *
 * @author Tai Boon Tan
 */
public class MCMoveVolumeN2 extends MCMoveBoxStep {

    public MCMoveVolumeN2(ISimulation sim, IPotentialMaster potentialMaster,
    		            ISpace _space) {
        this(potentialMaster, sim.getRandom(), _space, 1.0);
    }
    
    /**
     * @param potentialMaster an appropriate PotentialMaster instance for calculating energies
     * @param space the governing space for the simulation
     */
    public MCMoveVolumeN2(IPotentialMaster potentialMaster, IRandom random,
    		            ISpace _space, double pressure) {
        super(potentialMaster);
        this.random = random;
        this.D = _space.D();
        inflate = new BoxInflate(_space);
        energyMeter = new MeterPotentialEnergy(potentialMaster);
        setStepSizeMax(1.0);
        setStepSizeMin(0.0);
        setStepSize(0.10);
        setPressure(pressure);
        energyMeter.setIncludeLrc(true);
        affectedAtomIterator = new AtomIteratorLeafAtoms();
        coeff = new double[4];
        rScale = _space.makeVector();
    }
    
    public void setBox(IBox p) {
        super.setBox(p);
        energyMeter.setBox(p);
        inflate.setBox(p);
        affectedAtomIterator.setBox(p);
        if(species==null){
        	throw new RuntimeException("<MCMoveVolumeN2.java> Must set Species First");
        }
        numMolec = p.getNMolecules(species);
        double rho = numMolec/p.getBoundary().volume();
        
        if (numMolec == 32){
        	caseNumMolec = 1;
        	coeff[0] = -2.6662784873e-1;
        	coeff[1] = -3.5164057559e2;
        	coeff[2] = -2.9386151219e5;
        	coeff[3] = -6.767248762e4;
        	setLatticeCorrec(Kelvin.UNIT.fromSim(uCorrection(rho)));
        	
        } else if (numMolec == 108){
        	caseNumMolec = 2;
        	coeff[0] = -1.3294323226e-1;
        	coeff[1] = -1.3902715906e3;
        	coeff[2] = -2.9491796677e4;
        	coeff[3] = -3.9889114522e5;
        	setLatticeCorrec(Kelvin.UNIT.fromSim(uCorrection(rho)));
        } else if (numMolec == 256){
        	caseNumMolec = 3;
        }
        
    }
    
    public boolean doTrial() {
        double vOld = box.getBoundary().volume();

        double rhoOld = numMolec/vOld;
        uOld = energyMeter.getDataAsScalar() + uCorrection(rhoOld);
        hOld = uOld + pressure*vOld;
        
        if(isVolChange){
	        vScale = (2.*random.nextDouble()-1.)*stepSize;
	        vNew = vOld * Math.exp(vScale); //Step in ln(V)
	        double scale = Math.exp(vScale/D);
	        rScale.E(new double[]{scale,scale,scale});
	        
        }
        
        if (isXYZChange){
            double xOld = box.getBoundary().getBoxSize().getX(0);
            double yOld = box.getBoundary().getBoxSize().getX(1);
            double zOld = box.getBoundary().getBoxSize().getX(2);
        	xScale = Math.exp((2.*random.nextDouble()-1.)*stepSize);
        	yScale = Math.exp((2.*random.nextDouble()-1.)*stepSize);
        	zScale = Math.exp((2.*random.nextDouble()-1.)*stepSize);
        	
        	vNew = (xOld*xScale)*(yOld*yScale)*(zOld*zScale);
        	rScale.E(new double[]{xScale,yScale,zScale});
        }
        
        rhoNew = numMolec/vNew;
        inflate.setVectorScale(rScale);
        inflate.actionPerformed();
        
        uNew = energyMeter.getDataAsScalar() + uCorrection(rhoNew);
        hNew = uNew + pressure*vNew;
        return true;
    }//end of doTrial
    
    public double getA() {
        return Math.exp((box.getMoleculeList().getMoleculeCount()+1)*vScale);
    }
    
    public double getB() {
        return -(hNew - hOld);
    }
    
    public void acceptNotify() {  /* do nothing */}
    
    public void rejectNotify() {
        inflate.undo();
    }

    public double energyChange() {return uNew - uOld;}
    
    public AtomIterator affectedAtoms() {
        return affectedAtomIterator;
    }
    
    private double uCorrection(double rho){
    	double rho2 = rho*rho;
    	double rho3 = rho2*rho;
    	
    	if (caseNumMolec==0){
    		return  0.0;
    	
    	} else {
    		/*
    		 * return the correction energy for the total system
    		 * NOT the correction energy per molecule
    		 * The coeff was fitted with energy in K
    		 * so we have to convert the unit to simulation unit 
    		 */
    		return  Kelvin.UNIT.toSim(numMolec*(coeff[0]+coeff[1]*rho+coeff[2]*rho2+coeff[3]*rho3));
    	
    	}
    }
    
    public ISpecies getSpecies() {
		return species;
	}

	public void setSpecies(ISpecies species) {
		this.species = species;
	}
	
    public double getLatticeCorrec() {
		return latticeCorrec;
	}

	public void setLatticeCorrec(double latticeCorrec) {
		this.latticeCorrec = latticeCorrec;
	}
	
	public void setXYZChange(){
		isVolChange = false;
		isXYZChange = true;
	}
	
	private IVectorMutable rScale;
	private double[] coeff;
    private int caseNumMolec = 0;
    private ISpecies species;
    protected int numMolec;
	protected double latticeCorrec;
    
    private static final long serialVersionUID = 2L;
    protected double pressure;
    private MeterPotentialEnergy energyMeter;
    protected final BoxInflate inflate;
    private final int D;
    private IRandom random;
    protected final AtomIteratorLeafAtoms affectedAtomIterator;

    private transient double uOld, hOld, vNew, vScale, hNew, rhoNew, xScale, yScale, zScale;
    private transient double uNew = Double.NaN;
    private boolean isVolChange = true;
    private boolean isXYZChange = false;
	
	public void setPressure(double p) {pressure = p;}
    public final double getPressure() {return pressure;}
    public Dimension getPressureDimension() {return Pressure.DIMENSION;}
}