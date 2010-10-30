package etomica.virial;

import etomica.api.IAtom;
import etomica.api.IAtomList;
import etomica.api.IVectorMutable;
import etomica.config.IConformation;
import etomica.space.ISpace;
import etomica.units.Electron;

 /**
  *  Conformation for CO2
  *  Reference paper: Partial molar ~~, Stubbs Darke-Wilhelm, Siepmann, JCP
  * 
 * @author Shu Yang
 *
 */
public class ConformationCO2 implements IConformation, java.io.Serializable{
	
	public ConformationCO2(ISpace space){
		this.space = space;
		vector = space.makeVector();
	}

	public void initializePositions(IAtomList atomList) {
			
		IAtom n1 = atomList.getAtom(SpeciesTraPPECO2.indexC);
		n1.getPosition().E(new double[] {0, 0, 0});
		
		IAtom n2 = atomList.getAtom(SpeciesTraPPECO2.indexOleft);
		n2.getPosition().E(new double[] {-bondlength, 0, 0});
		
		IAtom n3 = atomList.getAtom(SpeciesTraPPECO2.indexOright);
		n3.getPosition().E(new double[] {bondlength, 0, 0});
		
		
	}
	
    public final static double [] Echarge = new double [3];
    static {
    	
    	//add + charge to C, - charge to O, what is the unit of the pt charge?
    	
        ConformationCO2.Echarge[SpeciesTraPPECO2.indexC] = Electron.UNIT.toSim( 0.70);
        
        ConformationCO2.Echarge[SpeciesTraPPECO2.indexOleft] = Electron.UNIT.toSim( -0.75);
        ConformationCO2.Echarge[SpeciesTraPPECO2.indexOright] = Electron.UNIT.toSim( -0.75);

        }
	
	protected final ISpace space;
	protected static final double bondlength = 1.16;

	
	protected IVectorMutable vector;
	
	private static final long serialVersionUID = 1L;
}