/*
 * Created on Mar 29, 2005 
 */
package etomica.models.hexane;

import etomica.api.IVectorMutable;
import etomica.config.ConformationChainZigZag;
import etomica.space.ISpace;


/**
 * Defines the vectors used to create a hexane molecule according to Dr. Monson's
 * data
 * 
 * @author nancycribbin
 */
public class ConformationHexane extends ConformationChainZigZag {

	public ConformationHexane(ISpace space){
		super(space);
        
        //In sigma units
        v1.setX(0, -0.188545552452977);
        v1.setX(1, -0.296942436305470);
        v1.setX(2, -0.190461975657022);
       
        v2.setX(0, -0.037596210819776);
        v2.setX(1, 0.092687873027807);
        v2.setX(2, -0.387292503317033);
        
        
		//In Angstroms
//		 v1.setX(0, 1.88545552452977);
//		 v1.setX(1, 2.96942436305470);
//		 v1.setX(2, 1.90461975657022);
//		 
//		 v2.setX(0, 0.37596210819776);
//		 v2.setX(1, -0.92687873027807);
//		 v2.setX(2, 3.87292503317033);
	}
    
	public IVectorMutable getV1(){
	    return v1;
    }

    public IVectorMutable getV2(){
        return v2;
    }
}
