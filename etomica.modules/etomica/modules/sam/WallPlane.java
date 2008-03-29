package etomica.modules.sam;

import etomica.api.IVector;
import etomica.math.geometry.Plane;

/**
 * Wrap a P1WCAWall and make it look like a Plane.  A boatload of
 * plane methods aren't overriden (there are a lot of them!) and calling them
 * will return garbage (or perhaps even crash).
 * DisplayBoxCanvasG3DSys only calls distanceTo and getD.
 *
 * @author Andrew Schultz
 */
public class WallPlane extends Plane {
    public WallPlane(P1WCAWall wallPotential) {
        super(wallPotential.getSpace());
        this.wallPotential = wallPotential;
    }
    
    // DisplayBoxCanvasG3DSys calls this
    public double distanceTo(IVector v) {
        return v.x(1) - wallPotential.getWallPosition()+1;
    }
    
    public double getA() {
        return 0;
    }
    
    public double getB() {
        return 1;
    }
    
    public double getC() {
        return 0;
    }

    // DisplayBoxCanvasG3DSys calls this
    public double getD() {
        return -wallPotential.getWallPosition();
    }
    
    private static final long serialVersionUID = 1L;
    protected final P1WCAWall wallPotential;
}
