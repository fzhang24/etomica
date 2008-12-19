package etomica.atom;

import etomica.api.IAtomKinetic;
import etomica.api.IVectorMutable;

/**
 * Interface for an Atom that has a position, orientation, velocity and angular
 * velocity.
 */
public interface IAtomOrientedKinetic extends IAtomKinetic, IAtomOriented {

    //XXX angular velocity is not a vector.  enjoy!
    public IVectorMutable getAngularVelocity(); //angular velocity vector in space-fixed frame
    
}