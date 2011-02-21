package etomica.virial;

import etomica.action.MoleculeActionTranslateTo;
import etomica.api.IAtom;
import etomica.api.IAtomList;
import etomica.api.IBox;
import etomica.api.IMolecule;
import etomica.api.IMoleculeList;
import etomica.api.IRandom;
import etomica.api.IVectorMutable;
import etomica.atom.AtomPositionGeometricCenter;
import etomica.atom.IAtomPositionDefinition;
import etomica.atom.MoleculePair;
import etomica.models.water.PNWaterGCPMThreeSite;
import etomica.models.water.SpeciesWater4P;
import etomica.space.ISpace;
import etomica.space.IVectorRandom;
import etomica.space.RotationTensor;

public class ConfigurationClusterWertheimGCPM4Pt extends ConfigurationCluster {
	
	protected IRandom random;
	protected PNWaterGCPMThreeSite associationPotential;
	protected PNWaterGCPMThreeSite associationPotential2;
	protected PNWaterGCPMThreeSite associationPotential3;
	protected PNWaterGCPMThreeSite nonAssociationPotential;
	protected double diagramIndex;

	public ConfigurationClusterWertheimGCPM4Pt(ISpace _space, IRandom random, PNWaterGCPMThreeSite associationPotential) {
		super(_space);
		this.associationPotential = associationPotential;
		this.random = random;
	}

	public ConfigurationClusterWertheimGCPM4Pt(ISpace _space, IRandom random, PNWaterGCPMThreeSite associationPotential,PNWaterGCPMThreeSite associationPotential2 ) {
		super(_space);
		this.associationPotential = associationPotential;
		this.associationPotential2 = associationPotential2;
		this.random = random;
	}
	public ConfigurationClusterWertheimGCPM4Pt(ISpace _space, IRandom random, PNWaterGCPMThreeSite associationPotential,PNWaterGCPMThreeSite associationPotential2, PNWaterGCPMThreeSite associationPotential3 ) {
		super(_space);
		this.associationPotential = associationPotential;
		this.associationPotential2 = associationPotential2;
		this.associationPotential3 = associationPotential3;
		this.random = random;
	}
	
	public ConfigurationClusterWertheimGCPM4Pt(ISpace _space, IRandom random, PNWaterGCPMThreeSite associationPotential,PNWaterGCPMThreeSite associationPotential2, double diagramIndex ) {
		super(_space);
		this.associationPotential = associationPotential;
		this.associationPotential2 = associationPotential2;
		this.random = random;
		this.diagramIndex = diagramIndex;
	}
	public void initializeCoordinates(IBox box) {
		super.initializeCoordinates(box);
		associationPotential.setBox(box);
		BoxCluster clusterBox =(BoxCluster) box;
		IMoleculeList list = box.getMoleculeList();
		MoleculePair pair = new MoleculePair();
		pair.atom0 = list.getMolecule(0);
		pair.atom1 = list.getMolecule(1);
		double[] d = new double[] {3.5,0,0};
		double[] e = new double[] {5.0,5.0,0};
		double[] f = new double[] {0,5.0,0};
		translation(d,e,f,box);
		association(pair,box);
		clusterBox.trialNotify();
		clusterBox.acceptNotify();
		System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
	
	public void initializeCoordinatesER(IBox box) {
		super.initializeCoordinates(box);
		associationPotential.setBox(box);
		BoxCluster clusterBox =(BoxCluster) box;
		double[] d = new double[] {3.5,0,0};
		double[] e = new double[] {5.0,5.0,0};
		double[] f = new double[] {0,5.0,0};
		translation(d,e,f,box);
		clusterBox.trialNotify();
		clusterBox.acceptNotify();
		System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
	
	public void initializeCoordinates2(IBox box) {
		super.initializeCoordinates(box);
		associationPotential.setBox(box);
		associationPotential2.setBox(box);
		BoxCluster clusterBox =(BoxCluster) box;
		IMoleculeList list = box.getMoleculeList();
		MoleculePair pair1 = new MoleculePair();
		MoleculePair pair2 = new MoleculePair();
		pair1.atom0 = list.getMolecule(0);
		pair1.atom1 = list.getMolecule(1);
		pair2.atom0= list.getMolecule(1);
		pair2.atom1= list.getMolecule(2);
		double[] d = new double[] {3.5,0,0};
		double[] e = new double[] {3.5,3.5,0};
		double[] f = new double[] {0,10.0,0};
		translation(d,e,f,box);
		association(pair1,box);
		association2(pair2,box);
		clusterBox.trialNotify();
		clusterBox.acceptNotify();
		System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
	public void initializeCoordinates3(IBox box) {
		super.initializeCoordinates(box);
		associationPotential.setBox(box);
		associationPotential2.setBox(box);
		associationPotential3.setBox(box);
		BoxCluster clusterBox =(BoxCluster) box;
		IMoleculeList list = box.getMoleculeList();
		MoleculePair pair1 = new MoleculePair();
		MoleculePair pair2 = new MoleculePair();
		MoleculePair pair3 = new MoleculePair();
		pair1.atom0 = list.getMolecule(0);
		pair1.atom1 = list.getMolecule(1);
		pair2.atom0= list.getMolecule(1);
		pair2.atom1= list.getMolecule(2);
		pair3.atom0= list.getMolecule(2);
		pair3.atom1= list.getMolecule(3);
		double[] d = new double[] {3.5,0,0};
		double[] e = new double[] {7,0,0};
		double[] f = new double[] {10.5,0,0};
		translation(d,e,f,box);
		association(pair1,box);
		association2(pair2,box);
		association3(pair3,box);
		clusterBox.trialNotify();
		clusterBox.acceptNotify();
		System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
	public void initializeCoordinatesRef(IBox box) {
		super.initializeCoordinates(box);
		associationPotential.setBox(box);
		associationPotential2.setBox(box);
		BoxCluster clusterBox =(BoxCluster) box;
		IMoleculeList list = box.getMoleculeList();
		for (int i=1;i<list.getMoleculeCount();i++){
			((IVectorMutable)list.getMolecule(i).getChildList().getAtom(0).getPosition()).setX(0, 0.9*i);
		 }
		clusterBox.trialNotify();
		clusterBox.acceptNotify();
		System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
	public void initializeCoordinates4(IBox box) {
		super.initializeCoordinates(box);
		associationPotential.setBox(box);
		associationPotential2.setBox(box);
		BoxCluster clusterBox =(BoxCluster) box;
		IMoleculeList list = box.getMoleculeList();
		MoleculePair pair1 = new MoleculePair();
		MoleculePair pair2 = new MoleculePair();
		pair1.atom0 = list.getMolecule(0);
		pair1.atom1 = list.getMolecule(1);
		pair2.atom0= list.getMolecule(2);
		pair2.atom1= list.getMolecule(3);
		double[] d = new double[] {3.5,0,0};
		double[] e = new double[] {3.5,10.0,0};
		double[] f = new double[] {0,10.0,0};
		translation(d,e,f,box);
		association(pair1,box);
		association3(pair2,box);
		clusterBox.trialNotify();
		clusterBox.acceptNotify();
		System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
	
	public void translation(double[] d,double[] e,double[] f, IBox box){//place molecule1,2,3 at some position
		IMoleculeList list = box.getMoleculeList();
        MoleculeActionTranslateTo translationA = new MoleculeActionTranslateTo(space);
        MoleculeActionTranslateTo translationB = new MoleculeActionTranslateTo(space);
        MoleculeActionTranslateTo translationC = new MoleculeActionTranslateTo(space);
        IVectorMutable a = space.makeVector();
        IVectorMutable b = space.makeVector();
        IVectorMutable c = space.makeVector();
        a.E(d);
        b.E(e);
        c.E(f);
        translationA.setDestination(a);
        translationB.setDestination(b);
        translationC.setDestination(c);
		IMolecule mol1 = list.getMolecule(1);
		IMolecule mol2 = list.getMolecule(2);
		IMolecule mol3 = list.getMolecule(3);
        translationA.actionPerformed(mol1);
        translationB.actionPerformed(mol2);
        translationC.actionPerformed(mol3);
	}
	public void association(MoleculePair pair, IBox box){
		RotationTensor rotationTensor = space.makeRotationTensor();
		IVectorMutable r0 = space.makeVector();
		IAtomPositionDefinition positionDefinition = new AtomPositionGeometricCenter(space);
		IMoleculeList list = box.getMoleculeList();
		pair.atom0 = list.getMolecule(0);
		IMolecule water = list.getMolecule(1);
		pair.atom1 = water;
		
        while (true){
	        IVectorRandom positionWater = (IVectorRandom)space.makeVector();
	        positionWater.setRandomInSphere(random);
	        positionWater.TE(4.0);//place water molecule within a sphere with r = 4A
	        MoleculeActionTranslateTo translation = new MoleculeActionTranslateTo(space);
	        translation.setDestination(positionWater);
	        translation.actionPerformed(water);
	       
	        if (associationPotential.energy(pair) != 0.0){//when there is an association, fix the position of water
        		break;
        	}
	        double dTheta = (2*random.nextDouble() - 1.0)*Math.PI;
	        rotationTensor.setAxial(r0.getD() == 3 ? random.nextInt(3) : 2,dTheta);

	        r0.E(positionDefinition.position(water));
		    IAtomList childList = water.getChildList();
		    for (int iChild = 0; iChild<childList.getAtomCount(); iChild++) {//free rotation until finding association
		        IAtom a = childList.getAtom(iChild);
		        IVectorMutable r = a.getPosition();
		        r.ME(r0);
		        box.getBoundary().nearestImage(r);
		        rotationTensor.transform(r);
		        r.PE(r0);
	    	}
	        if (associationPotential.energy(pair) != 0.0){
        		break;
        	}
		}
	}
	
	
	public void association2(MoleculePair pair, IBox box){
		RotationTensor rotationTensor = space.makeRotationTensor();
		IVectorMutable r0 = space.makeVector();
		IAtomPositionDefinition positionDefinition = new AtomPositionGeometricCenter(space);
		IMoleculeList list = box.getMoleculeList();
		pair.atom0 = list.getMolecule(1);
		IMolecule water = list.getMolecule(2);
		pair.atom1 = water;
	
        while (true){
        	IVectorRandom positionWater = (IVectorRandom)space.makeVector();
	        positionWater.setRandomInSphere(random);
	        positionWater.TE(4.0);//place water molecule within a sphere with r = 4A
	        positionWater.PE(pair.atom0.getChildList().getAtom(0).getPosition());
	        MoleculeActionTranslateTo translation = new MoleculeActionTranslateTo(space);
	        translation.setDestination(positionWater);
	        translation.actionPerformed(water);

	       
	        if (associationPotential2.energy(pair) != 0.0){//when there is an association, fix the position of water
        		break;
        	}
	        double dTheta = (2*random.nextDouble() - 1.0)*Math.PI;
	        rotationTensor.setAxial(r0.getD() == 3 ? random.nextInt(3) : 2,dTheta);

	        r0.E(positionDefinition.position(water));
		    IAtomList childList = water.getChildList();
		    for (int iChild = 0; iChild<childList.getAtomCount(); iChild++) {//free rotation until finding association
		        IAtom a = childList.getAtom(iChild);
		        IVectorMutable r = a.getPosition();
		        r.ME(r0);
		        box.getBoundary().nearestImage(r);
		        rotationTensor.transform(r);
		        r.PE(r0);
	    	}
	        if (associationPotential2.energy(pair) != 0.0){
        		break;
        	}
		}
	}
	
	public void association3(MoleculePair pair, IBox box){
		RotationTensor rotationTensor = space.makeRotationTensor();
		IVectorMutable r0 = space.makeVector();
		IAtomPositionDefinition positionDefinition = new AtomPositionGeometricCenter(space);
		IMoleculeList list = box.getMoleculeList();
		pair.atom0 = list.getMolecule(2);
		IMolecule water = list.getMolecule(3);
		pair.atom1 = water;
	
        while (true){
        	IVectorRandom positionWater = (IVectorRandom)space.makeVector();
	        positionWater.setRandomInSphere(random);
	        positionWater.TE(4.0);//place water molecule within a sphere with r = 8A
	        positionWater.PE(pair.atom0.getChildList().getAtom(0).getPosition());
	        MoleculeActionTranslateTo translation = new MoleculeActionTranslateTo(space);
	        translation.setDestination(positionWater);
	        translation.actionPerformed(water);

	       
	        if (associationPotential3.energy(pair) != 0.0){//when there is an association, fix the position of water
        		break;
        	}
	        double dTheta = (2*random.nextDouble() - 1.0)*Math.PI;
	        rotationTensor.setAxial(r0.getD() == 3 ? random.nextInt(3) : 2,dTheta);

	        r0.E(positionDefinition.position(water));
		    IAtomList childList = water.getChildList();
		    for (int iChild = 0; iChild<childList.getAtomCount(); iChild++) {//free rotation until finding association
		        IAtom a = childList.getAtom(iChild);
		        IVectorMutable r = a.getPosition();
		        r.ME(r0);
		        box.getBoundary().nearestImage(r);
		        rotationTensor.transform(r);
		        r.PE(r0);
	    	}
	        if (associationPotential3.energy(pair) != 0.0){
        		break;
        	}
		}
	}
	
	public void association4(MoleculePair pair,MoleculePair pair2, IBox box){
		RotationTensor rotationTensor = space.makeRotationTensor();
		IVectorMutable r0 = space.makeVector();
		IAtomPositionDefinition positionDefinition = new AtomPositionGeometricCenter(space);
		IMoleculeList list = box.getMoleculeList();
		pair.atom0 = list.getMolecule(1);
		IMolecule water = list.getMolecule(2);
		pair.atom1 = water;
		pair2.atom0 =list.getMolecule(0);
		pair2.atom1 = water;
		
	
        while (true){
        	IVectorRandom positionWater = (IVectorRandom)space.makeVector();
	        positionWater.setRandomInSphere(random);
	        positionWater.TE(4.0);//place water molecule within a sphere with r = 8A
	        positionWater.PE(pair.atom0.getChildList().getAtom(0).getPosition());
	        MoleculeActionTranslateTo translation = new MoleculeActionTranslateTo(space);
	        translation.setDestination(positionWater);
	        translation.actionPerformed(water);

	       
	        if (associationPotential2.energy(pair) != 0.0){//when there is an association, fix the position of water
        		break;
        	}
	        double dTheta = (2*random.nextDouble() - 1.0)*Math.PI;
	        rotationTensor.setAxial(r0.getD() == 3 ? random.nextInt(3) : 2,dTheta);

	        r0.E(positionDefinition.position(water));
		    IAtomList childList = water.getChildList();
		    for (int iChild = 0; iChild<childList.getAtomCount(); iChild++) {//free rotation until finding association
		        IAtom a = childList.getAtom(iChild);
		        IVectorMutable r = a.getPosition();
		        r.ME(r0);
		        box.getBoundary().nearestImage(r);
		        rotationTensor.transform(r);
		        r.PE(r0);
	    	}
	        if (associationPotential2.energy(pair) != 0.0 &&nonAssociationPotential.energy(pair2) != 0.0){
        		break;
        	}
		}
	}
	public void association5(MoleculePair pair,MoleculePair pair2, IBox box){
		RotationTensor rotationTensor = space.makeRotationTensor();
		IVectorMutable r0 = space.makeVector();
		IAtomPositionDefinition positionDefinition = new AtomPositionGeometricCenter(space);
		IMoleculeList list = box.getMoleculeList();
		pair.atom0 = list.getMolecule(1);
		IMolecule water = list.getMolecule(2);
		pair.atom1 = water;
		pair2.atom0 =list.getMolecule(0);
		pair2.atom1 = water;
	
        while (true){
        	IVectorRandom positionWater = (IVectorRandom)space.makeVector();
	        positionWater.setRandomInSphere(random);
	        positionWater.TE(3.5);//place water molecule within a sphere with r = 4A
	        //positionWater.PE(pair.atom0.getChildList().getAtom(0).getPosition());
	        MoleculeActionTranslateTo translation = new MoleculeActionTranslateTo(space);
	        translation.setDestination(positionWater);
	        translation.actionPerformed(water);

	       
	        if (associationPotential2.energy(pair) != 0.0 && associationPotential2.energy(pair2) != 0.0 ){//when there is an association, fix the position of water
        		break;
        	}
	        double dTheta = (2*random.nextDouble() - 1.0)*Math.PI;
	        rotationTensor.setAxial(r0.getD() == 3 ? random.nextInt(3) : 2,dTheta);

	        r0.E(positionDefinition.position(water));
		    IAtomList childList = water.getChildList();
		    for (int iChild = 0; iChild<childList.getAtomCount(); iChild++) {//free rotation until finding association
		        IAtom a = childList.getAtom(iChild);
		        IVectorMutable r = a.getPosition();
		        r.ME(r0);
		        box.getBoundary().nearestImage(r);
		        rotationTensor.transform(r);
		        r.PE(r0);
	    	}
	        if (associationPotential2.energy(pair) != 0.0&& associationPotential2.energy(pair2) != 0.0 ){
        		break;
        	}
		}
	}
}
