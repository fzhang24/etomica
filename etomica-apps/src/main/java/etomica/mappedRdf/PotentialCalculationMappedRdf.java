/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.mappedRdf;

import etomica.atom.AtomLeafAgentManager;
import etomica.atom.AtomPair;
import etomica.atom.IAtom;
import etomica.atom.IAtomList;
import etomica.box.Box;
import etomica.integrator.IntegratorVelocityVerlet;
import etomica.potential.*;
import etomica.simulation.Simulation;
import etomica.space.Space;
import etomica.space.Vector;
import etomica.space3d.Space3D;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by aksharag on 5/16/17.
 */
public class PotentialCalculationMappedRdf implements PotentialCalculation{
    protected final Box box;
    protected final IteratorDirective allAtoms;
    protected final AtomLeafAgentManager<Vector> forceManager;
    protected final Space space;
    protected double beta;
    protected final Vector dr;
    protected double c1;
    protected final double[] cumint;
    protected final AtomPair pair;
    protected double vol;
    protected double q;
    protected double qp;
    protected double qp_q;
    protected final int nbins;
    protected double sum;
    protected double x0, vCut;
    protected double vShift;
    protected double R, uR;

    public PotentialCalculationMappedRdf(Space space, Box box, int nbins, AtomLeafAgentManager<Vector> forceManager) {
        this.space = space;
        this.box = box;
        this.nbins = nbins;
        pair = new AtomPair();
        this.forceManager = forceManager;
        allAtoms = new IteratorDirective();
        dr = space.makeVector();
        cumint = new double[nbins+1];
        vol = box.getBoundary().volume();

    }

    public static void main (String[] args) throws IOException {
        Simulation sim = new Simulation(Space3D.getInstance());
        Box box = new Box(sim.getSpace());

        PotentialCalculationMappedRdf pc = new PotentialCalculationMappedRdf(sim.getSpace(),box, 1000000, null);
        P2LennardJones potential = new P2LennardJones(sim.getSpace());
        P2SoftSphericalTruncated p2Truncated = new P2SoftSphericalTruncated(sim.getSpace(), potential, 4);

        pc.setVolume(99999.99999999997);
        pc.setTemperature(2, p2Truncated);
        double rc = p2Truncated.getRange();
        double x0 = rc;

        FileWriter fw = new FileWriter("vu.dat");
        for (int i=10; i<45; i++) {
            double r = i*0.1;
            if (r>=4) r = 3.99999999;
                       System.out.println(r+" "+pc.calcXu(r, p2Truncated.u(r*r))+" ");
                       fw.write(r+" "+pc.calcXu(r, p2Truncated.u(r*r))+"\n");
                   }

        fw.close();

          }

    public double getX0() {
        return x0;
    }

    public void setVCut(double newVCut) {
        vCut = newVCut;
    }

    /**
     * Sets volume to an arbitrary value (instead of the box volume).  This is
     * used for 1/(1+q/V) terms.
     */
    public void setVolume(double newVol) {
        vol = newVol;
    }

    public void setTemperature(double T, Potential2SoftSpherical p2) {
        beta = 1/T;
        double rc = p2.getRange();
        R = 3;
        uR = p2.u(R);
        x0 = rc;
        q = 0;
        qp = 0;
        qp_q = 0;

        if (vCut==0) vCut = x0;
        vShift = -p2.u(vCut*vCut)+0.0494908;
        c1 = Math.log(rc+1)/nbins;
        int D = space.D();
        for (int i=1; i<=nbins; i++) {
            double r = Math.exp(c1*i)-1;
            double r2 = r*r;
            if (r >= p2.getRange() ) {
                if (i<nbins) throw new RuntimeException("oops "+i+" "+nbins+" "+r+" "+p2.getRange());
                r = Math.exp(c1*i*0.9999)-1;
                r2 = r*r;
            }
            double u = p2.u(r2);
            double evm1 = 0;
            double v = calcV(r,u);
            evm1 = Math.exp(-beta*v);
            q += (D==2 ? r : r2)*(evm1-1)*c1*(r+1);

        }

        q *= (D==2?2:4)*Math.PI*vol;
        q += vol*vol;

        for (int i=1; i<=nbins; i++) {
            double r = Math.exp(c1*i)-1;
            double r2 = r*r;
            if (r >= p2.getRange()) {
                if (i<nbins) throw new RuntimeException("oops "+i+" "+nbins+" "+r+" "+p2.getRange());
                r = Math.exp(c1*i*0.9999)-1;
                r2 = r*r;
            }
            double u = p2.u(r2);
            double evm1 = 0;
            double v = calcV(r,u);
            evm1 = Math.exp(-beta*v);
            cumint[i] = cumint[i-1] + (D==2 ? r : r2)*(evm1)*c1*(r+1);

        }

    }

    protected double calcXu(double r, double u) {

        double y = cumint(r);
        double v = calcV(r,u);
        double vR = calcV(R,uR);
        double evm1 = Math.exp(-beta*v);
        double evmR = Math.exp(-beta*vR);

        if(r<R)
         return y*(evmR/evm1)*(R/r)*(R/r)*beta*4*Math.PI/q*-1;
        else
         return beta*(R/r)*(R/r)*(evmR/evm1)*(1-(4*Math.PI/q*y));

    }

    protected double calcV(double r,double u){

        if(r>vCut)
            return 0;
        return u+vShift;
    }

    protected double cumint( double r) {
        // r = (exp(c1*i) - 1)
        double i = Math.log(r+1)/c1;
        int ii = (int)i;
        double y = cumint[ii] + (cumint[ii+1]-cumint[ii])*(i-ii);
        return y;
    }

    public void reset() {
        sum = 0;
    }

    public void doCalculation(IAtomList atoms, IPotentialAtomic potential) {
        if (!(potential instanceof Potential2SoftSpherical)) return;
        Potential2SoftSpherical p2 = (Potential2SoftSpherical)potential;
        IAtom a = atoms.getAtom(0);
        IAtom b = atoms.getAtom(1);
//        System.out.println("volume "+vol);
        dr.Ev1Mv2(b.getPosition(),a.getPosition());
        box.getBoundary().nearestImage(dr);
        double r2 = dr.squared();
        double r = Math.sqrt(r2);
        if (r > p2.getRange()) return;
        double fij = p2.du(r2);
        double up = fij/r;
        double vp = up;
        double u= p2.u(r2);
        double v = calcV(r,u);
        sum += v-u;

        if (r<x0) {
            Vector fi = forceManager.getAgent(a);
            Vector fj = forceManager.getAgent(b);
            //  System.out.println(u+" "+r);
            double fifj = (fi.dot(dr) - fj.dot(dr))/r;
            double xs = calcXu(r, u);
            double wp = 0.5*fifj;
            sum += xs*beta*(vp-wp);

        }

    }

    public double getRdf() {

        return sum;

    }
}
