/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.potential;

import etomica.atom.IAtom;
import etomica.atom.IAtomOriented;
import etomica.box.Box;
import etomica.space.Boundary;
import etomica.space.Space;
import etomica.space.Vector;
import etomica.units.BohrRadius;
import etomica.units.Degree;
import etomica.units.Kelvin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// The H2-H2 interaction potential generated by this program has been presented in the paper "Potential energy surface for interactions between two hydrogen molecules"
//by Konrad Patkowski, Wojciech Cencek, Piotr Jankowski, Krzysztof Szalewicz,
// James B. Mehl, Giovanni Garberoglio, and Allan H. Harvey.
// submitted to J. Chem. Phys.
// Subroutine potH2H2 computes 
//   Eint - the value of interaction potential (in K) of the H2-H2 complex
//   th1, th2, phi - input angles (all in degrees), and
//   R - input intermolecular distance (in bohrs).
// The initial call with the parameter icall=0 should be done to establish
// conversion factors for units of distance, angles, and energy using
// parameters R, th1, and Eint, respectively. If one wants to use
// default units these parameters should be equal 1.0d0. 
// Subsequent calls should be done with icall=1.
//
// The definitions of parameters describing the geometry of the complex
// can be found in the paper.
//
// A sample program using the procedure potH2H2 is given below.
//
// The routines used to compute values of interaction energy
// are based on the fitting program written by Robert Bukowski et al.
public class P2HydrogenPatkowskiAtomic implements IPotentialAtomic {
//    public static void main(String[] args) {
//        ISpace space = Space3D.getInstance();
//        P2HydrogenPatkowskiAtomic p2 = new P2HydrogenPatkowskiAtomic(space);
//        double dth1 = 0;
//        double dth2 = 0;
//        double dphi = 0;
//        try {
//            FileWriter file1 = new FileWriter("/usr/users/rsubrama/Desktop/Acads/Phd/hydrogen/HP/feb2015/H2Potential.dat");            
//            file1.write("Theta 1 = "+dth1+" Theta 2 = "+ dth2 + " Phi = "+dphi+"\n");
//            for (double i=1.0; i<=5.0; i+= 0.1) {
//                double th1 = Degree.UNIT.toSim(dth1);
//                double th2 = Degree.UNIT.toSim(dth2);
//                double phi = Degree.UNIT.toSim(dphi);
//                double E = p2.vH2H2(i, th1, th2, phi);
//                double Ek = Kelvin.UNIT.fromSim(E);
//                file1.write(i+" "+Ek+"\n");
//            }
//            dth1 = 90;
//            file1.write("Theta 1 = "+dth1+" Theta 2 = "+ dth2 + " Phi = "+dphi+"\n");
//            for (double i=1.0; i<=5.0; i+= 0.1) {
//                double th1 = Degree.UNIT.toSim(dth1);
//                double th2 = Degree.UNIT.toSim(dth2);
//                double phi = Degree.UNIT.toSim(dphi);
//                double E = p2.vH2H2(i, th1, th2, phi);
//                double Ek = Kelvin.UNIT.fromSim(E);
//                file1.write(i+" "+Ek+"\n");
//            }
//            dth1 = 45;
//            dth2 = 45;
//            dphi = 45;
//            file1.write("Theta 1 = "+dth1+" Theta 2 = "+ dth2 + " Phi = "+dphi+"\n");
//            for (double i=1.0; i<=5.0; i+= 0.1) {
//                double th1 = Degree.UNIT.toSim(dth1);
//                double th2 = Degree.UNIT.toSim(dth2);
//                double phi = Degree.UNIT.toSim(dphi);
//                double E = p2.vH2H2(i, th1, th2, phi);
//                double Ek = Kelvin.UNIT.fromSim(E);
//                file1.write(i+" "+Ek+"\n");
//            }            
//            file1.flush();
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    protected static final int n_min=4,n_max=9,llex=5,lcex=10,llsp=8,lcsp=32;
    protected static final int nex=5,irpowex=1,nsp=8,irpowsp=3;
    protected static final int [][] lex = new int [][] {{0,2,2,2,2},{0,0,2,2,2},{0,2,0,2,4}};
    protected static final int [][] lsp = new int [][] {{0,2,2,2,2,4,4,4},{0,0,2,2,2,2,4,4},{0,2,0,2,4,2,0,2}};
    protected final int [][] ldum = new int [3][50];
    protected static final double [] cex = new double [] {0.77579784301669E+01,-0.88189157160519E+00, 0.13122186973231E+00, 0.89486310729923E-01,-0.96795230459705E-01, 0.17072367066010E-01, 0.52439301173471E-01,-0.80620486754867E-02, 0.11852775602107E-01,-0.82388526351054E-02};
    protected static final double [] csp = new double [] {-0.21467304760234E+00, 0.26572695744944E+00,-0.36123695424879E-01, 0.11727111200529E-02,-0.22192315452241E+00, 0.15183242755333E+00,-0.42295421944021E-01, 0.29805326577099E-02, 0.63384644263290E-01,0.96475875397283E-02,-0.59716159807931E-02, 0.45233188049943E-03,-0.87078416677117E-01, 0.24049596224259E-01,-0.27967850167989E-02, 0.12157557700573E-03, 0.52461054176386E-01,-0.41148262485107E-01, 0.10412915218231E-01,-0.70506371095752E-03, 0.95452458073845E-01,-0.42983067248415E-01, 0.63863122576647E-02,-0.29836021374821E-03, 0.75585315872044E-01,-0.39656085571124E-01, 0.68991446201765E-02,-0.39579003546218E-03,-0.30341308582235E-01, 0.13957028702569E-01,-0.22033211385438E-02, 0.11728578945138E-03};
    protected final double [] cdum = new double[50];
    protected final double [] f = new double[41];
    protected final double [][][][] w3j = new double[14][14][14][28];
    protected final int [][] max_l = new int [10][2];
    protected final double [][][][] Vnlll = new double[10][7][7][9];
    protected final double [] rrlong = new double[20];
    protected final double [][] p = new double[51][51];
    protected final double [][] p1 = new double[51][51];
    protected final double [][] p2 = new double[51][51];
    protected final double [] rr = new double[10];
    protected final double [] elong = new double[20];
    protected Boundary boundary;
    protected static final double rMin = 1.4;
    protected final Vector dr,com0,com1,hh0,hh1,n0,n1;
    protected static double[][] xPos = {{0.0374,-0.2422,0.2792},{-0.0374,0.2422,-0.2792},{-0.016,-1.2877,2.9799},{-0.0196,-2.0073,2.7949},{-0.1047,1.5911,2.54},{0.063,1.7936,3.2349}};
    protected boolean print = false;
    public FileWriter file = null;
    //     from /home/teojan/H2-H2/fit_h22/opt.out.07
    //
    // ----------------------------------------------------------------------------
    public P2HydrogenPatkowskiAtomic(Space space) {
        dr = space.makeVector();
        com0 = space.makeVector();
        com1 = space.makeVector();  
        hh0 = space.makeVector();
        hh1 = space.makeVector();  
        n0 = space.makeVector();
        n1 = space.makeVector();

        fill_Vlongdata();
        fct(40);
        fill3j(13,13,13);  
        int ipow = irpowex+1;  
        for (int i=0; i<nex; i++) {
            cdum[i] = cex[ipow*(i+1)-ipow+1];
            for (int k=0; k<3; k++) {
                ldum[k][i] = lex[k][i];
            }
        }
    }
    //-------------------------------------------------------------------------
    //Isotropic version of the potential energy surface given in the file
    //potH2H2.f.
    // 
    //The H2-H2 interaction potential generated by this program has been
    //presented in the paper
    //"Potential energy surface for interactions between two hydrogen molecules"
    //by Konrad Patkowski, Wojciech Cencek, Piotr Jankowski, Krzysztof Szalewicz,
    //James B. Mehl, Giovanni Garberoglio, and Allan H. Harvey.
    //
    //-------------------------------------------------------------------------

    public double vH2H2(double R,double th1,double th2,double phi) {
        if (R < rMin) R = rMin; // rMin = 1.4 angstroms , not so quite hard core
        double pR = BohrRadius.UNIT.fromSim(R);
        double vTot = potentot(pR,th1,th2,phi);
        return Kelvin.UNIT.toSim(vTot);
    }
    // -------------------------------------------------------------------------
    //
    //   Calculate the total damped asymptotics from elst and dispind..
    //   th1, th2, phi in radians...., R in Angstroms.
    //
    protected double asymp(double R,double th1,double th2,double phi) {
        //
        //---- call the asymptotics procedures.....
        //
        longH2H2(R,th1,th2,phi,elong);//
        //---- Compute the damping constant for a given geometry
        //
        double dump = 0.0;
        for (int i=0; i<nex; i++) {
            double glam = al1l2l0(ldum[0][i],ldum[1][i],ldum[2][i],th1,th2,phi);
            int iphase = 1;
            if (ldum[0][i]+ldum[1][i] % 2 ==1) iphase = -1;
            glam = glam + iphase*al1l2l0(ldum[0][i],ldum[1][i],ldum[2][i],th2,th1,phi);
            dump = dump - cdum[i]*glam;
        }
        //
        //--- Damping factor ready, proceed to the asymptotics...
        //

        //---- restrict electrostatics...
        double value = 0.0;
        for (int i=n_min; i<=n_max; i++) {
            double ddd = d(i+1,dump,R);
            value = value + ddd*elong[i];
        }
        return value;
    }

    // ----------------------------------------------------------------------------
    //
    protected double d(int n,double beta, double r) {
        //
        //     calculate the damping factor (small R correct)
        //
        double br=beta*r;
        double sum=1.00;
        double term=1.00;
        int ncn=n;
        for (int i=1; i<=ncn; i++) {
            term=term*br/i;
            sum=sum+term;
        }
        double d1 = 1.00 - Math.exp(-br)*sum;
        if(Math.abs(d1) < 1.0e-8) {
            d1=0.00;
            for (int i=ncn+1; i<=1000; i++) {
                term=term*br/i;
                d1=d1+term;
                if(term/d1 < 1.0e-8) break;
            }
            if(term/d1 >= 1.0e-8) throw new RuntimeException("No convergence in d");
            d1=d1*Math.exp(-br);
        }
        return d1;
    }
    // ---------------------------------------------------------------------------
    protected double dd(int n,double b,double r) {
        double br = b*r;
        return b*Math.exp(-br)*Math.pow(br,n)/f[n];
    }
    // ----------------------------------------------------------------------------
    //
    //  A short subroutine to calculate the total interaction
    //  potential from the exchange, "spherical", and
    //  asymptotic parts...
    //
    protected double potentot(double r,double th1, double th2,double phi) {
        //
        double vex = 0.0;
        double vsp = 0.0;
        double valas = 0.0;
        rr[0] = 1.0;  
        for (int i=1;i<=irpowsp+1;i++) {
            rr[i] = rr[i-1]*r;
        }
        //---- Compute the exchange part
        int ipow = irpowex+1;
        double rrev = 1;
        vex = 0.0;
        double damp = 0.0;
        for (int i=0; i<nex; i++) {
            int iphase = 1;
            if ((lex[0][i]+lex[1][i]) % 2 == 1) iphase = -1;
            double glam = al1l2l0(lex[0][i],lex[1][i],lex[2][i],th1,th2,phi);
            glam = glam + iphase*al1l2l0(lex[0][i],lex[1][i],lex[2][i],th2,th1,phi);
            damp = damp + glam*cex[2*i];
            for (int k=0; k<ipow; k++) {
                vex = vex + glam*cex[ipow*(i+1)-ipow+k]*rr[k]*rrev;
            }
        }
        vex = Math.exp(vex);
        //---- Compute the "spherical" part
        ipow = irpowsp + 1;
        rrev = 1;  
        vsp = 0.0;
        for (int i=0; i<nsp; i++) {
            int iphase = 1;
            if ((lsp[0][i]+lsp[1][i]) % 2 == 1) iphase = -1;
            double glam = al1l2l0(lsp[0][i],lsp[1][i],lsp[2][i],th1,th2,phi);
            glam = glam + iphase*al1l2l0(lsp[0][i],lsp[1][i],lsp[2][i],th2,th1,phi);
            for (int k=0; k<ipow; k++) {
                //---- update gradient if proved successful
                vsp = vsp + glam*csp[ipow*(i+1)-ipow+k]*rr[k]*rrev;
            }
        }
        vsp = vex*vsp;
        //----- Compute the asymptotics...
        valas = asymp(r,th1,th2,phi);
        //        System.out.println(vsp+" "+valas);
        return (vsp+valas);
    }

    // --------------------------------------------------------------------------
    //
    // This subroutine fills up the matrix w3j with values of 3-j coefficient
    //
    protected void fill3j(int l1max,int l2max,int lmax) {
        for (int l1=0; l1<=l1max; l1++) {
            for (int l2=0; l2<=l2max; l2++) {
                int lmin=l1-l2;
                if (lmin<0) lmin = -lmin;
                int mm=l1>l2?l2:l1; 
                int llmax=lmax>(l1+l2)?(l1+l2):lmax;
                for (int l=lmin; l<=llmax; l++) {
                    for (int m=0; m<=mm; m++) {
                        int m1=m;
                        int m2=-m;
                        int mmm=0;
                        double c = cgc(l1,m1,l2,m2,l,mmm,1);
                        w3j[m][l1][l2][l]=c;
                    }
                }
            }
        }
    }
    // ----------------------------------------------------------------------------
    // 
    // Compute the set of associated Legendre polynomials P_lm
    // for l=0,1,...,lmax, and m=0,1,...,l. First the standard
    // polynomials
    //
    // P^m_l(x) = (1/2^l l!)(1-x^2)^(m/2) (d^(l+m)/d x^(l+m))(x^2 -1)^l
    //
    // are computed, and then multiplied by
    //
    //  (-1)^m sqrt[(2l+1)(l-m)!/2(l+m)!]/sqrt(2Pi)
    //
    // to get the P_lm polynomials....
    //
    protected void plmrb(double [][] p,double x,int lmax) {
        // inverse of Math.sqrt(2Pi)
        double twopinv = 0.39894228040140;
        //
        // starting value
        //
        p[0][0] = 1.0;
        double u = Math.sqrt(1-x*x);
        //
        // compute the diagonal elements
        //      
        for (int l=1; l<=lmax; l++) {
            p[l][l] = (2*l-1)*p[l-1][l-1]*u;
        }
        //
        // compute P_lm along the columns with fixed m
        //

        for (int m=0; m<lmax;m++) {
            for (int l=m; l<lmax;l++) {
                double pp = 0.0;    
                if((l-1)>=m) {        
                    pp = p[l-1][m];
                }
                p[l+1][m] = ((2*l+1)*x*p[l][m]-(l+m)*pp)/(l-m+1);
            }
        }
        //
        // Renormalize values...
        //
        for (int l=0; l<=lmax; l++) {
            int mm = 1;
            for (int m=0; m<=l; m++) {
                double dnorm = f[l-m]*(2*l+1)/(2*f[l+m]);
                p[l][m] = mm*twopinv*Math.sqrt(dnorm)*p[l][m];
                mm = -mm;
            }
        }
    }
    // -------------------------------------------------------------------------
    //
    // compute the matrix of N!
    protected void fct(int nmax) {
        f[0] = 1.0;
        for (int i=1; i<=nmax; i++) {
            f[i] = f[i-1]*i;
        }
    }
    // -------------------------------------------------------------------------
    //
    // Calculate the Clebsh-Gordan coefficient (or the 3-j symbol)
    // The parameter ind3j.eq.1 indicates that the 3-J symbol is returned
    //
    protected double cgc(int j1,int m1,int j2,int m2,int j,int m,int ind3j) {
        //
        double d3jfact = 1.0;
        if(ind3j == 1) {
            d3jfact = 1.0/Math.sqrt(2*j+1);
            if ((j1-j2-m)%2 ==1) d3jfact = -d3jfact;
            m = -m;
        }
        //
        // Check the triangle conditions
        //        
        if(j>(j1+j2)) throw new RuntimeException("triangle violated");
        if(j*j<(j1-j2)*(j1-j2)) throw new RuntimeException("triangle violated");
        if((m1+m2) != m) {
            return 0.0;
        }
        //
        // Calculation proper... the pre-sum factor....
        //
        double facn = (2*j+1)*f[j1+j2-j]*f[j1-m1]*f[j2-m2]*f[j+m]*f[j-m];
        double facd = f[j1+j2+j+1]*f[j+j1-j2]*f[j+j2-j1]*f[j1+m1]*f[j2+m2];
        double fac = Math.sqrt(facn/facd);

        //
        // determine the limit of k summation...
        //
        int kmax = (j2+j-m1)>(j-m)?(j-m):(j2+j-m1);
        if (kmax > (j1-m1)) kmax = (j1-m1);
        if(kmax<0) kmax = 0;
        int kmin = (-j1-m1)>(-j2+j-m1)?(-j1-m1):(-j2+j-m1);
        if (kmin < 0) kmin = 0;

        //
        // perform the summation (at least one cycle must be completed...
        //
        double sum = 0.0;
        for (int k=kmin; k<=kmax; k++) {
            facn = f[j1+m1+k]*f[j2+j-m1-k];
            facd = f[k]*f[j-m-k]*f[j1-m1-k]*f[j2-j+m1+k];
            sum = sum + (facn/facd)*(k%2==0?1:-1);
        }
        double value = d3jfact*fac*sum;
        if ((j1-m1)%2 == 1) value = -value;
        return value;
    }
    // -------------------------------------------------------------------------
    //
    // Calculate the function Al1l2L for a given set of angles...
    // It is assumed that the th1 and th2 angles are between the monomer bond
    // and the "inner" part of the intermolecular axis.
    //
    protected double al1l2l0(int l1,int l2,int l,double th1,double th2,double phi) {
        double pifact=12.5663706143590;
        //
        double c1 = Math.cos(th1);
        double c2 = Math.cos(th2);
        plmrb(p1,c1,l1);
        plmrb(p2,c2,l2);
        int mmax = l1>l2?l2:l1;
        double sum = 0.0;
        double value = 0.0;
        int dummy = -1;
        for (int m=1; m<=mmax; m++) {
            value=w3j[m][l1][l2][l];
            sum = sum + dummy*value*p1[l1][m]*p2[l2][m]*Math.cos(m*phi);
            dummy = -dummy;
        }
        value=w3j[0][l1][l2][l];
        sum = 2*sum + value*p1[l1][0]*p2[l2][0];
        double dummy1= sum*pifact/Math.sqrt((2.0*l1+1.0)*(2.0*l2+1.0));
        if ((l1+l2+l)%2 == 1) dummy1 = -dummy1;
        return dummy1;
    }
    //
    // -------------------------------------------------------------------------
    //
    protected void longH2H2(double R, double th1, double th2, double phi, double [] elong) {
        //
        // conversion factor (K -> a.u.) (1 K = 3.16669^(-6) a.u.)
        double xK2au=3.16669e-6 ;
        double Econv=1.00/xK2au   ;
        // conversion factor for angles

        double pth2=Math.PI-th2;

        double rrev = 1.0/R;
        rrlong[0] = rrev;
        for (int i=1; i<13; i++) {
            rrlong[i] = rrlong[i-1]*rrev;
        }
        for (int i=0; i<20; i++) {
            elong[i] = 0.0;
        }
        for (int n=n_min; n<=n_max; n++) {
            for (int lb=0; lb<=max_l[n][0]; lb+=2) { 
                for (int la=0; la<=lb; la+=2) {
                    for (int l=0; l<=max_l[n][1]; l+=2) {
                        if (l*l>=(la-lb)*(la-lb) && (l <= (la+lb))) {
                            double xl=Math.sqrt(2*l+1);
                            if ((la-lb) % 2 == 1) xl = -xl;
                            double angterm=al1l2l0(la,lb,l,th1,pth2,phi);
                            elong[n]=elong[n] + xl*Vnlll[n][la][lb][l]*angterm;
                            if (la != lb) {             
                                angterm=al1l2l0(la,lb,l,pth2,th1,phi);
                                elong[n]=elong[n] + xl*Vnlll[n][la][lb][l]*angterm;
                            }
                        }
                    }
                }
            }
            elong[n]=elong[n]*rrlong[n]*Econv;
        }
        double edisp6 = BishopPipin(R,th1,pth2,phi);
        elong[5]=edisp6*rrlong[5]*Econv;
    }
    // ----------------------------------------------------------------------------
    protected void fill_Vlongdata() {
        max_l[4][0]=2;
        max_l[5][0]=2;
        max_l[6][0]=4;
        max_l[7][0]=4;
        max_l[8][0]=6;
        max_l[9][0]=6;
        max_l[4][1]=4;
        max_l[5][1]=4;
        max_l[6][1]=6;
        max_l[7][1]=6;
        max_l[8][1]=8;
        max_l[9][1]=8;
        Vnlll[4][2][2][4]= 1.94806; //Komasa & Thakkar
        Vnlll[5][0][0][0]=-1.202e+1;
        Vnlll[5][0][2][2]=-1.216e+0;
        Vnlll[5][2][2][0]=-0.058e+0;
        Vnlll[5][2][2][2]=-0.070e+0;
        Vnlll[5][2][2][4]=-0.560e+0;
        Vnlll[6][2][4][6]= 3.37672e+0; //Komasa & Thakkar
        Vnlll[7][0][0][0]=-2.136e+2;
        Vnlll[7][0][2][2]=-6.320e+1;
        Vnlll[7][0][4][4]=-4.022e+0;
        Vnlll[7][2][2][0]=-1.550e+0;
        Vnlll[7][2][2][2]= 2.829e+0;
        Vnlll[7][2][2][4]=-1.300e+1;
        Vnlll[7][2][4][2]=-0.082e+0;
        Vnlll[7][2][4][4]=-0.156e+0;
        Vnlll[7][2][4][6]=-2.721e+0;
        Vnlll[8][4][4][8]= 11.22326e+0; //Komasa & Thakkar
        Vnlll[8][2][6][8]= 9.78761e+0;  //Komasa & Thakkar
        Vnlll[9][0][0][0]=-4.700e+3;
        Vnlll[9][0][2][2]=-1.570e+3;
        Vnlll[9][0][4][4]=-7.255e+1;
        Vnlll[9][0][6][6]=-4.413e+0;
        Vnlll[9][2][2][0]=-3.668e+1;
        Vnlll[9][2][2][2]= 1.202e+2;
        Vnlll[9][2][2][4]=-7.914e+2;
        Vnlll[9][2][4][2]=-2.695e+0;
        Vnlll[9][2][4][4]= 5.293e+0;
        Vnlll[9][2][4][6]=-5.769e+1;
        Vnlll[9][2][6][4]=-0.063e+0;
        Vnlll[9][2][6][6]=-0.183e+0;
        Vnlll[9][2][6][8]=-4.135e+0;
        Vnlll[9][4][4][0]=-0.113e+0;
        Vnlll[9][4][4][2]= 0.087e+0;
        Vnlll[9][4][4][4]=-0.118e+0;
        Vnlll[9][4][4][6]=-0.595e+0;
        Vnlll[9][4][4][8]=-1.921e+1;
    }
    // -------------------------------------------------------------------------
    protected double BishopPipin(double R,double dth1,double dth2,double dphi) {
        double dc1=Math.cos(dth1);
        double dc2=Math.cos(dth2);
        double dcp=Math.cos(dphi);  
        double ds1=Math.sin(dth1);
        double ds2=Math.sin(dth2);

        // Bishop & Pipin
        double c600=12.0581680;
        double c620=1.21940/2.00;
        double c602=1.21940/2.00;
        double c622=0.38980/2.00;
        double x1 = 2.00*dc1*dc2-ds1*ds2*dcp;
        double c6=c600 +(3.00*dc1*dc1-1.00)*c620 +(3.00*dc2*dc2-1.00)*c602 +(x1*x1-dc1*dc1-dc2*dc2)*c622;
        double edisp6=-c6;
        return edisp6;
    }

    public double getRange() {

        return Double.POSITIVE_INFINITY;
    }

    public void setBox(Box box) {
        boundary = box.getBoundary();
    }

    public int nBody() { 
        return 2;
    }


    public double energy(List<IAtom> atoms) {
        IAtom m0 = atoms.get(0);
        IAtom m1 = atoms.get(1);
        Vector hh0 = ((IAtomOriented)m0).getOrientation().getDirection();
        Vector hh1 = ((IAtomOriented)m1).getOrientation().getDirection();
        Vector com0 = m0.getPosition();
        Vector com1 = m1.getPosition();

        dr.Ev1Mv2(com1, com0);            
        boundary.nearestImage(dr);    
        double r01 = Math.sqrt(dr.squared());
        if (r01 == 0) return Double.POSITIVE_INFINITY;
        dr.normalize();
        double cth1 = dr.dot(hh0);
        double cth2 = -dr.dot(hh1);
        if (cth1 > 1.0) cth1 = 1.0;
        if (cth1 < -1.0) cth1 = -1.0;
        double th1 = Math.acos(cth1);
        if (cth2 > 1.0) cth2 = 1.0;
        if (cth2 < -1.0) cth2 = -1.0;
        double th2 = Math.acos(cth2);

        n0.E(hh0);
        n0.PEa1Tv1(-cth1, dr);            
        n0.normalize();

        n1.E(hh1);
        n1.PEa1Tv1(cth2, dr);
        n1.normalize();
        if (n0.isNaN() || n1.isNaN()) throw new RuntimeException("oops");

        double cphi = n0.dot(n1);
        if (cphi > 1.0) cphi = 1.0;
        if (cphi < -1.0) cphi = -1.0;
        double phi = Math.acos(cphi);

        if (th1 > (Math.PI/2.0)) {
            th1 = Math.PI - th1;
            phi = Math.PI - phi;
        } 
        if (th2 > (Math.PI/2.0)) {
            th2 = Math.PI - th2;
            phi = Math.PI - phi;
        }
        if (th2 == 0) phi = 0;

        if (r01 < rMin) r01 = rMin; // rMin = 1.4 angstroms , not so quite hard core            
        // same as Garberoglio
        double E = vH2H2(r01,th1,th2,phi);

        if (E < -20000 || Double.isNaN(E)) {
            vH2H2(r01,th1,th2,phi);
        }

        if (print && Math.random() < 0.0001) {
            try {
                if (file == null) file = new FileWriter("configurations.dat");                    
                double rBohr = BohrRadius.UNIT.fromSim(r01);
                double Ek = Kelvin.UNIT.fromSim(E);
                double dth1 = Degree.UNIT.fromSim(th1);
                double dth2 = Degree.UNIT.fromSim(th2);
                double dphi = Degree.UNIT.fromSim(phi);
                file.write(rBohr+" "+dth1+" "+dth2+" "+dphi+" "+Ek+"\n");
                file.flush();                    
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return E;
    } 

}






