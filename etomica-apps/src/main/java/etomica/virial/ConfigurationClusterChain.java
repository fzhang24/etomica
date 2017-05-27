/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.virial;

import etomica.api.IAtomList;
import etomica.api.IVector;
import etomica.box.Box;
import etomica.space.Space;

public class ConfigurationClusterChain extends ConfigurationCluster {

	public ConfigurationClusterChain(Space _space) {
		super(_space);
	}

	public void initializeCoordinates(Box box) {
		super.initializeCoordinates(box);
		BoxCluster clusterBox =(BoxCluster) box;
		IAtomList list = box.getLeafList();
		for (int i=1;i<list.getAtomCount();i++){
			((IVector)list.getAtom(i).getPosition()).setX(0, 0.9*i);
		 }
		 clusterBox.trialNotify();
		 clusterBox.acceptNotify();
		 //System.out.println("box "+clusterBox.getSampleCluster().value(clusterBox));
	}
}
