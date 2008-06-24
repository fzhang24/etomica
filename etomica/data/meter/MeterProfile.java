package etomica.data.meter;
import etomica.api.IBoundary;
import etomica.api.IBox;
import etomica.api.IVector;
import etomica.data.Data;
import etomica.data.DataSource;
import etomica.data.DataSourceIndependent;
import etomica.data.DataSourcePositioned;
import etomica.data.DataSourceUniform;
import etomica.data.DataTag;
import etomica.data.IDataInfo;
import etomica.data.DataSourceUniform.LimitType;
import etomica.data.types.DataDoubleArray;
import etomica.data.types.DataFunction;
import etomica.data.types.DataDouble.DataInfoDouble;
import etomica.data.types.DataDoubleArray.DataInfoDoubleArray;
import etomica.data.types.DataFunction.DataInfoFunction;
import etomica.space.ISpace;
import etomica.units.Length;

/**
 * Meter that takes a (scalar) Meter and records its property as a
 * 1-dimensional function of position in the simulation volume. The measured
 * property must be a quantity that can be associated with a position in the
 * box. The position coordinate lies along one dimension (x,y,z).
 * 
 * @author Rob Riggleman
 * @author Andrew Schultz
 */
public class MeterProfile implements DataSource, DataSourceIndependent, java.io.Serializable {
    
    /**
     * Default constructor sets profile along the x-axis, with 100 points in
     * the profile.
     */
    public MeterProfile(ISpace space) {
        xDataSource = new DataSourceUniform("x", Length.DIMENSION);
        position = space.makeVector();
        tag = new DataTag();
        xDataSource.setTypeMax(LimitType.HALF_STEP);
        xDataSource.setTypeMin(LimitType.HALF_STEP);
    }
    
    public IDataInfo getDataInfo() {
        return dataInfo;
    }
    
    public DataTag getTag() {
        return tag;
    }

    /**
     * The meter that defines the profiled quantity
     */
    public DataSourcePositioned getDataSource() {return meter;}
    
    /**
     * Accessor method for the meter that defines the profiled quantity.
     */
    public void setDataSource(DataSourcePositioned m) {
        if (!(m.getPositionDataInfo() instanceof DataInfoDouble)) {
            throw new IllegalArgumentException("data source must return a DataDouble");
        }
        meter = m;
        if (box != null) {
            meter.setBox(box);
        }
        reset();
    }
    
    /**
     * Accessor method for vector describing the direction along which the profile is measured.
     * Each atom position is dotted along this vector to obtain its profile abscissa value.
     */
    public int getProfileDim() {return profileDim;}
    
    /**
     * Accessor method for vector describing the direction along which the profile is measured.
     * Each atom position is dotted along this vector to obtain its profile abscissa value.
     * The given vector is converted to a unit vector, if not already.
     */
    public void setProfileDim(int dim) {
        profileDim = dim;
        reset();
    }
    
    /**
     * Returns the profile for the current configuration.
     */
    public Data getData() {
        IBoundary boundary = box.getBoundary();
        data.E(0);
        double[] y = data.getData();
        Data xData = xDataSource.getData();
        
        for (int i=0; i<y.length; i++) {
            double x = xData.getValue(i);
            IVector pos = boundary.randomPosition();
            pos.setX(profileDim, x);
            y[i] += meter.getData(pos).getValue(0);
        }
        return data;
    }

    public DataDoubleArray getIndependentData(int i) {
        return (DataDoubleArray)xDataSource.getData();
    }
    
    public DataInfoDoubleArray getIndependentDataInfo(int i) {
        return (DataInfoDoubleArray)xDataSource.getDataInfo();
    }
    
    public int getIndependentArrayDimension() {
        return 1;
    }
    
    /**
     * @return Returns the box.
     */
    public IBox getBox() {
        return box;
    }
    /**
     * @param box The box to set.
     */
    public void setBox(IBox box) {
        this.box = box;
        if (meter != null) {
            meter.setBox(box);
        }
    }
    
    public void reset() {
        if (box == null) return;
        
        double halfBox = 0.5*box.getBoundary().getDimensions().x(profileDim);
        xDataSource.setXMin(-halfBox);
        xDataSource.setXMax(halfBox);
        
        if (meter != null) {
            data = new DataFunction(new int[] {xDataSource.getNValues()});
            dataInfo = new DataInfoFunction(meter.getPositionDataInfo().getLabel()+" Profile", meter.getPositionDataInfo().getDimension(), this);
            dataInfo.addTag(meter.getTag());
            dataInfo.addTag(tag);
        }
    }

    private static final long serialVersionUID = 1L;
    private IBox box;
    private DataSourceUniform xDataSource;
    private DataFunction data;
    private IDataInfo dataInfo;
    /**
     * Vector describing the orientation of the profile.
     * For example, (1,0) is along the x-axis.
     */
    protected int profileDim;
    protected final IVector position;
    /**
     * Meter that defines the property being profiled.
     */
    protected DataSourcePositioned meter;
    protected final DataTag tag;
}
