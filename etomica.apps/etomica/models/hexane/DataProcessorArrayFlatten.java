package etomica.models.hexane;

import etomica.data.Data;
import etomica.data.DataInfo;
import etomica.data.DataProcessor;
import etomica.data.DataTag;
import etomica.data.types.DataDoubleArray;
import etomica.data.types.DataDoubleArray.DataInfoDoubleArray;

/**
 * Takes an N dimensional array and flattens it out by flattening the last two
 * dimensions and creating an N-1 dimensional array.
 * (N.B. It just changes the jumpcount values; it never actually touches the
 * actual stored numbers.)
 * @author nancycribbin
 *
 */
public class DataProcessorArrayFlatten extends DataProcessor {

    /**
     * Constructor that doesn't do anything.
     */
    public DataProcessorArrayFlatten(){
        tag = new DataTag();
    }
    
    protected Data processData(Data inputData) {
        outputData = new DataDoubleArray(shapeNew, ((DataDoubleArray)inputData).getData());
        return outputData;
    }

    protected DataInfo processDataInfo(DataInfo inputDataInfo) {
        if(!(inputDataInfo instanceof DataInfoDoubleArray)){
            throw new IllegalArgumentException("DataProcessorArrayFlatten needs DataDoubleArray argument");
        }
        //Get the shape of the data we expect to come in, reduce it by one, then 
        // create a new DataDoubleArray of that shape.
        int[] shapeOld = ((DataInfoDoubleArray)inputDataInfo).getArrayShape();
        shapeNew = new int[shapeOld.length-1];
//        shapeNew[0] = shapeOld[0] * shapeOld[1];
//        for(int i = 1; i < shapeNew.length; i++){
//            shapeNew[i] = shapeOld[i+1];
//        }
        
        //move over all the pieces of information
        for(int i = 0; i < shapeNew.length; i++){
            shapeNew[i] = shapeOld[i];
        }
        
        //Make the last entry of the new array equal to the last 2 entries of the old array.
        shapeNew[shapeNew.length-1] = shapeOld[shapeOld.length-1] * shapeOld[shapeOld.length-2];
        
        outputDataInfo = new DataInfoDoubleArray("unlabeled", inputDataInfo.getDimension(), shapeNew);
        
        return outputDataInfo;
    }

    public DataTag getTag() {
        return tag;
    }

    /**
     * returns null (non-Javadoc)
     */
    public DataProcessor getDataCaster(DataInfo dataInfo) {
        return null;
    }

    private DataDoubleArray outputData;
    private DataInfoDoubleArray outputDataInfo;
    private int[] shapeNew;
    protected final DataTag tag;
}
