package etomica.modules.sam;

import etomica.api.IAtomList;
import etomica.api.IPotentialAtomic;
import etomica.api.IVector;
import etomica.integrator.IntegratorBox;
import etomica.potential.PotentialCalculationForceSum;
import etomica.potential.PotentialSoft;

public class PotentialCalculationForceSumWall extends
        PotentialCalculationForceSum {

    public PotentialCalculationForceSumWall(P1WCAWall wallPotential) {
        this.wallPotential = wallPotential;
    }
    
    public P1WCAWall getWallPotential() {
        return wallPotential;
    }

    public void reset() {
        super.reset();
        wallForceSum = 0;
    }
    
    public double getWallForce() {
        return wallForceSum;
    }
    
    public void doCalculation(IAtomList atoms, IPotentialAtomic potential) {
        PotentialSoft potentialSoft = (PotentialSoft)potential;
        int nBody = potential.nBody();
        IVector[] f = potentialSoft.gradient(atoms);
        switch(nBody) {
            case 1:
                if (f[0].squared() > 2.5e9) {
                    double scale = 50000/Math.sqrt(f[0].squared());
                    ((IntegratorBox.Forcible)integratorAgentManager.getAgent(atoms.getAtom(0))).force().PEa1Tv1(-scale, f[0]);
                    if (potential == wallPotential) {
                        wallForceSum += scale*f[0].x(wallPotential.getWallDim());
                    }
                }
                else {
                    ((IntegratorBox.Forcible)integratorAgentManager.getAgent(atoms.getAtom(0))).force().ME(f[0]);
                    if (potential == wallPotential) {
                        wallForceSum += f[0].x(wallPotential.getWallDim());
                    }
                }
                break;
            case 2:
                if (f[0].squared() > 2.5e9) {
                    double scale = 50000/Math.sqrt(f[0].squared());
                    ((IntegratorBox.Forcible)integratorAgentManager.getAgent(atoms.getAtom(0))).force().PEa1Tv1(-scale, f[0]);
                    ((IntegratorBox.Forcible)integratorAgentManager.getAgent(atoms.getAtom(1))).force().PEa1Tv1(-scale, f[1]);
                }
                else {
                    ((IntegratorBox.Forcible)integratorAgentManager.getAgent(atoms.getAtom(0))).force().ME(f[0]);
                    ((IntegratorBox.Forcible)integratorAgentManager.getAgent(atoms.getAtom(1))).force().ME(f[1]);
                }
                break;
            default:
                throw new RuntimeException("we don't do N-body");
        }
    }

    protected final P1WCAWall wallPotential;
    protected double wallForceSum;
}
