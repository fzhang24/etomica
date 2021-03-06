/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.virial.simulations;

import etomica.action.IAction;
import etomica.data.types.DataDoubleArray;
import etomica.data.types.DataGroup;
import etomica.integrator.IntegratorListenerAction;
import etomica.potential.P2HardAssociationCone;
import etomica.potential.P2MoleculeMonatomic;
import etomica.space.Space;
import etomica.space3d.Space3D;
import etomica.util.ParameterBase;
import etomica.virial.ClusterAbstract;
import etomica.virial.ConfigurationClusterMove;
import etomica.virial.MayerEGeneral;
import etomica.virial.MayerEHardSphere;
import etomica.virial.MayerGeneral;
import etomica.virial.MayerHardSphere;
import etomica.virial.SpeciesFactoryOrientedSpheres;
import etomica.virial.cluster.Standard;

/** 
 * Virial coefficients with single attraction site AssociationCone Potential using Mayer Sampling 
 * @author hmkim3
 * */

public class VirialHardAssociationCone {



    public static void main(String[] args) {
        VirialLJParam params = new VirialLJParam();
		final int nPoints;
		double temperature;
		double sigmaHSRef;
		double wellConstant;
		long steps ;
		
		if (args.length == 0) {
			nPoints = params.nPoints;
			temperature = params.temperature;
			steps = params.numSteps;
			sigmaHSRef = params.sigmaHSRef;
			wellConstant = params.wellConstant;
		}
		else if (args.length == 5) {

        	nPoints = Integer.parseInt(args[0]);
            temperature = Double.parseDouble(args[1]);
            steps = Integer.parseInt(args[2]);
            sigmaHSRef = Double.parseDouble(args[3]);
            wellConstant = Double.parseDouble(args[4]);
            params.writeRefPref = true;
             
        } else {
        	throw new IllegalArgumentException("Wrong number of arguments");
        }
		double sigma=1;
		double epsilon=1;
        

        final double[] HSB = new double[9];
        HSB[2] = Standard.B2HS(sigmaHSRef);
        HSB[3] = Standard.B3HS(sigmaHSRef);
        HSB[4] = Standard.B4HS(sigmaHSRef);
        HSB[5] = Standard.B5HS(sigmaHSRef);
        HSB[6] = Standard.B6HS(sigmaHSRef);
        HSB[7] = Standard.B7HS(sigmaHSRef);
        HSB[8] = Standard.B8HS(sigmaHSRef);
        System.out.println("sigmaHSRef: "+sigmaHSRef);
        System.out.println("B2HS: "+HSB[2]);
        System.out.println("B3HS: "+HSB[3]+" = "+(HSB[3]/(HSB[2]*HSB[2]))+" B2HS^2");
        System.out.println("B4HS: "+HSB[4]+" = "+(HSB[4]/(HSB[2]*HSB[2]*HSB[2]))+" B2HS^3");
        System.out.println("B5HS: "+HSB[5]+" = 0.110252 B2HS^4");
        System.out.println("B6HS: "+HSB[6]+" = 0.03881 B2HS^5");
        System.out.println("B7HS: "+HSB[7]+" = 0.013046 B2HS^6");
        System.out.println("B8HS: "+HSB[8]+" = 0.004164 B2HS^7");
        System.out.println("Lennard Jones overlap sampling B"+nPoints+" at T="+temperature);
        System.out.println("Square-Well epsilon =" +wellConstant+ "*epsilon");
		
        Space space = Space3D.getInstance();
        
        MayerHardSphere fRef = new MayerHardSphere(sigmaHSRef);
        MayerEHardSphere eRef = new MayerEHardSphere(sigmaHSRef);
        P2HardAssociationCone pTarget = new P2HardAssociationCone(space, sigma, epsilon, Double.POSITIVE_INFINITY, wellConstant);
        P2MoleculeMonatomic pMolecule = new P2MoleculeMonatomic(pTarget);
        MayerGeneral fTarget = new MayerGeneral(pMolecule);
        MayerEGeneral eTarget = new MayerEGeneral(pMolecule);
        ClusterAbstract targetCluster = Standard.virialCluster(nPoints, fTarget, nPoints>3, eTarget, true);
        targetCluster.setTemperature(temperature);
        ClusterAbstract refCluster = Standard.virialCluster(nPoints, fRef, nPoints>3, eRef, true);
        refCluster.setTemperature(temperature);

        System.out.println((steps*1000)+" steps ("+steps+" blocks of 1000)");
		
        //final SimulationVirialOverlap sim = new SimulationVirialOverlap(space,new SpeciesFactorySpheres(), temperature,refCluster,targetCluster);
        final SimulationVirialOverlap sim = new SimulationVirialOverlap(space, new SpeciesFactoryOrientedSpheres(), temperature, refCluster, targetCluster);
        //sim.integratorOS.setStepFreq0(0.5);
        //sim.integratorOS.setAdjustStepFreq(false);
        /**sim.integratorOS.setNumSubSteps(1000);
          if (true) {
            sim.box[0].getBoundary().setDimensions(space.makeVector(new double[]{10,10,10}));
            sim.box[1].getBoundary().setDimensions(space.makeVector(new double[]{10,10,10}));
            SimulationGraphic simGraphic = new SimulationGraphic(sim, SimulationGraphic.TABBED_PANE, space, sim.getController());
            simGraphic.getDisplayBox(sim.box[0]).setShowBoundary(false);
            simGraphic.getDisplayBox(sim.box[1]).setShowBoundary(false);
            
            ((IAtomTypeSphere)pMolecule).setDiameter(sigma);
            ((IAtomTypeSphere)pMolecule).setDiameter(sigma);
            simGraphic.makeAndDisplayFrame();

            sim.integratorOS.setNumSubSteps(1000);
            sim.setAccumulatorBlockSize(1000);
                
            // if running interactively, set filename to null so that it doens't read
            // (or write) to a refpref file
            sim.getController().removeAction(sim.ai);
            sim.getController().addAction(new IAction() {
                public void actionPerformed() {
                    sim.initRefPref(null, 100);
                    sim.equilibrate(null, 200);
                    sim.ai.setMaxSteps(Long.MAX_VALUE);
                }
            });
            sim.getController().addAction(sim.ai);
            if ((Double.isNaN(sim.refPref) || Double.isInfinite(sim.refPref) || sim.refPref == 0)) {
                throw new RuntimeException("Oops");
            }

            return;
        }
        **/
		ConfigurationClusterMove configuration = new ConfigurationClusterMove(space, sim.getRandom());
		configuration.initializeCoordinates(sim.box[1]);
		sim.setAccumulatorBlockSize((int)steps*10);
        sim.integratorOS.setNumSubSteps(1000);
        // if running interactively, don't use the file
        String refFileName = params.writeRefPref ? "refpref"+nPoints+"_"+temperature : null;
        // this will either read the refpref in from a file or run a short simulation to find it
//        sim.setRefPref(1.0082398078547523);
        sim.initRefPref(refFileName, steps/100);
        // run another short simulation to find MC move step sizes and maybe narrow in more on the best ref pref
        // if it does continue looking for a pref, it will write the value to the file
        sim.equilibrate(refFileName, steps/4);
        
        System.out.println("equilibration finished");

        IAction progressReport = new IAction() {
            public void actionPerformed() {
                System.out.print(sim.integratorOS.getStepCount()+" steps: ");
                double[] ratioAndError = sim.dsvo.getOverlapAverageAndError();
                System.out.println("abs average: "+ratioAndError[0]*HSB[nPoints]+", error: "+ratioAndError[1]*HSB[nPoints]);
            }
        };
        IntegratorListenerAction progressReportListener = new IntegratorListenerAction(progressReport);
        progressReportListener.setInterval((int)(steps/10));
        sim.integratorOS.getEventManager().addListener(progressReportListener);

        sim.ai.setMaxSteps(steps);
        for (int i=0; i<2; i++) {
            System.out.println("MC Move step sizes "+sim.mcMoveTranslate[i].getStepSize());
        }
        sim.getController().actionPerformed();

        System.out.println("final reference step frequency "+sim.integratorOS.getStepFreq0());
        System.out.println("actual reference step frequency "+sim.integratorOS.getActualStepFreq0());
        
        double[] ratioAndError = sim.dsvo.getOverlapAverageAndError();
        System.out.println("ratio average: "+ratioAndError[0]+", error: "+ratioAndError[1]);
        System.out.println("abs average: "+ratioAndError[0]*HSB[nPoints]+", error: "+ratioAndError[1]*HSB[nPoints]);
        DataGroup allYourBase = (DataGroup)sim.accumulators[0].getData(0);
        System.out.println("hard sphere ratio average: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].RATIO.index)).getData()[1]
                          +" error: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].RATIO_ERROR.index)).getData()[1]);
        System.out.println("hard sphere   average: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].AVERAGE.index)).getData()[0]
                          +" stdev: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].STANDARD_DEVIATION.index)).getData()[0]
                          +" error: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].ERROR.index)).getData()[0]);
        System.out.println("hard sphere overlap average: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].AVERAGE.index)).getData()[1]
                          +" stdev: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].STANDARD_DEVIATION.index)).getData()[1]
                          +" error: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[0].ERROR.index)).getData()[1]);

        allYourBase = (DataGroup)sim.accumulators[1].getData(0);
        System.out.println("lennard jones ratio average: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].RATIO.index)).getData()[1]
                          +" error: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].RATIO_ERROR.index)).getData()[1]);
        System.out.println("lennard jones average: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].AVERAGE.index)).getData()[0]
                          +" stdev: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].STANDARD_DEVIATION.index)).getData()[0]
                          +" error: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].ERROR.index)).getData()[0]);
        System.out.println("lennard jones overlap average: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].AVERAGE.index)).getData()[1]
                          +" stdev: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].STANDARD_DEVIATION.index)).getData()[1]
                          +" error: "+((DataDoubleArray)allYourBase.getData(sim.accumulators[1].ERROR.index)).getData()[1]);
	}

   
    public static class VirialLJParam extends ParameterBase {
        public int nPoints = 4;
        public double temperature = 1.5;
        public long numSteps = 1000000;
        public double sigmaHSRef = 1.5;
        public double wellConstant = 16.0;
        public boolean writeRefPref = false;
    }
}
