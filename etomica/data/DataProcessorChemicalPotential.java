package etomica.data;

import etomica.api.IData;
import etomica.data.types.DataFunction;
import etomica.data.types.DataFunction.DataInfoFunction;
import etomica.integrator.IntegratorBox;

/**
 * DataProcessor that takes as input the average density and average Boltzmann
 * factor profiles and calculates the chemical potential profile as the sum of
 * the ideal gas contribution (kT ln(<rho>)) and the excess chemical
 * potential, (-kT ln(<exp(-beta U)>)).  The incoming data is expected to be
 * the Boltzmann factor, while the density is taken from a DataSource.
 * 
 * @author Andrew Schultz
 */
public class DataProcessorChemicalPotential extends DataProcessor {

    public void setDensityProfileDump(IEtomicaDataSource newDensityProfileSource) {
        densityProfileSource = newDensityProfileSource;
    }

    public IEtomicaDataSource getDenstiyProfileDump() {
        return densityProfileSource;
    }
    
    public void setIntegrator(IntegratorBox newIntegrator) {
        integrator = newIntegrator;
    }
    
    protected IData processData(IData inputData) {
        double[] oldY = ((DataFunction)inputData).getData();
        double[] newY = data.getData();
        IData densityData = densityProfileSource.getData();
        double temp = integrator.getTemperature();
        for (int i=0; i<oldY.length; i++) {
            double density = densityData.getValue(i);
            if (density*oldY[i] == 0) {
                newY[i] = Double.NaN;
            }
            else {
                newY[i] = temp * Math.log(density/oldY[i]);
            }
        }
        return data;
    }

    protected IEtomicaDataInfo processDataInfo(IEtomicaDataInfo inputDataInfo) {
        data = new DataFunction(new int[]{inputDataInfo.getLength()});
        dataInfo = inputDataInfo.getFactory().makeDataInfo();
        dataInfo.addTag(tag);
        return dataInfo;
    }

    public DataPipe getDataCaster(IEtomicaDataInfo inputDataInfo) {
        return null;
    }

    protected IEtomicaDataSource densityProfileSource;
    protected DataFunction data;
    protected IntegratorBox integrator;
}
