package etomica.virial;

import etomica.MeterScalar;
import etomica.SimulationElement;
import etomica.units.Dimension;

/**
 * @author kofke
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MeterSign extends MeterScalar {

	private Cluster cluster;
	private double temperature, beta;
	
	/**
	 * Constructor for MeterSign.
	 * @param parent
	 */
	public MeterSign(SimulationElement parent, Cluster cluster) {
		super(parent);
		setCluster(cluster);
		setTemperature(1.0); //almost always, temperature is irrelevant.  Sign of f does not normally depend on temperature.
	}

	/**
	 * @see etomica.MeterScalar#currentValue()
	 */
	public double currentValue() {
		return (cluster.value(((PhaseCluster)phase).getPairSet(), beta)>0) ? +1.0 : -1.0;
	}

	/**
	 * @see etomica.MeterAbstract#getDimension()
	 */
	public Dimension getDimension() {
		return null;
	}

	/**
	 * Returns the cluster.
	 * @return Cluster
	 */
	public Cluster getCluster() {
		return cluster;
	}

	/**
	 * Returns the temperature.
	 * @return double
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * Sets the cluster.
	 * @param cluster The cluster to set
	 */
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	/**
	 * Sets the temperature.
	 * @param temperature The temperature to set
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
		beta = 1.0/temperature;
	}

}
