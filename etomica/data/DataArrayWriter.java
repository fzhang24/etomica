package etomica.data;

import java.io.FileWriter;
import java.io.IOException;

import etomica.api.IData;
import etomica.data.DataLogger.DataWriter;
import etomica.data.types.CastGroupToDoubleArray;
import etomica.data.types.CastToDoubleArray;
import etomica.data.types.DataDoubleArray;
import etomica.data.types.DataDoubleArray.DataInfoDoubleArray;
import etomica.data.types.DataGroup.DataInfoGroup;

/**
 * A DataWriter that writes out data as a table with optional column headings
 */
public class DataArrayWriter implements DataWriter, java.io.Serializable {

    public DataArrayWriter() {
        super();
        setIncludeHeader(true);
        reset();
    }

    /**
     * Directs the writer to include column headers or not
     */
    public void setIncludeHeader(boolean newIncludeHeader) {
        includeHeader = newIncludeHeader;
    }
    
    /**
     * Returns whether the writer includes column headers or not
     */
    public boolean getIncludeHeader() {
        return includeHeader;
    }
    
    public void putDataInfo(IEtomicaDataInfo newDataInfo) {
        dataInfo = newDataInfo;
    }

    public DataPipe getDataCaster(IEtomicaDataInfo newDataInfo) {
        if (newDataInfo instanceof DataInfoDoubleArray) {
            // we like tables
            return null;
        }
        else if (newDataInfo instanceof DataInfoGroup) {
            if (((DataInfoGroup)newDataInfo).getNDataInfo() == 0) {
                //it's empty, turn it into an empty array
                return new CastGroupToDoubleArray();
            }
            IEtomicaDataInfo dataInfo0 = ((DataInfoGroup)newDataInfo).getSubDataInfo(0);
            for (int i = 1; i<((DataInfoGroup)newDataInfo).getNDataInfo(); i++) {
                IEtomicaDataInfo subDataInfo = ((DataInfoGroup)newDataInfo).getSubDataInfo(0);
                if (subDataInfo.getClass() != dataInfo0.getClass()){
                    throw new IllegalArgumentException("DataSinkTable can only handle homogeneous groups");
                }
            }
            // turn group into a multi-dimensional array, which we'll cast to a table
            // with a CastToTable
            return new CastGroupToDoubleArray();
        }
        return new CastToDoubleArray();
    }
    
    public void putData(IData data) {
        DataDoubleArray dataArray = (DataDoubleArray)data;
        try {
            int nColumns = dataArray.getArrayShape(1);
            if (nColumns < 1) {
                return;
            }
            if (firstWrite && includeHeader) {
                // if this is the first write to a file, start with the column headers
                fileWriter.write(dataInfo.getLabel()+" "+dataInfo.getDimension()+"\n");
            }
            firstWrite = false;
            int[] indices = new int[2];
            for (int i=0; i<dataArray.getArrayShape(0); i++) {
                indices[0] = i;
                indices[1] = 0;
                fileWriter.write(Double.toString(dataArray.getValue(indices)));
                for (int j=1; j<nColumns; j++) {
                    indices[1] = j;
                    fileWriter.write("  "+Double.toString(dataArray.getValue(indices)));
                }
                fileWriter.write("\n");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public void setFileWriter(FileWriter newFileWriter) {
        fileWriter = newFileWriter;
    }
    
    public void reset() {
        firstWrite = true;
    }

    private static final long serialVersionUID = 1L;
    private FileWriter fileWriter;
    private boolean firstWrite;
    private IEtomicaDataInfo dataInfo;
    private boolean includeHeader;
}
