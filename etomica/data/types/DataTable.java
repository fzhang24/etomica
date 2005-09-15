package etomica.data.types;

import java.io.Serializable;

import etomica.data.Data;
import etomica.data.DataFactory;
import etomica.data.DataInfo;
import etomica.units.Dimension;
import etomica.util.Function;

/**
 * Data object that holds <tt>double[]</tt> arrays as if they are columns in a
 * table. The inner class <tt>Column</tt> wraps the array along with a
 * descriptive String heading and a Dimension instance that indicates the
 * physical dimensions of the data in the column.  Descriptive immutable String 
 * labels can also be associated with the rows (row headers).
 * be 
 * <p>
 * The number of columns is set at construction and cannot be changed.
 * <p>
 * All columns are of equal length (i.e., the length of the <tt>double[]</tt>
 * arrays is the same for all columns). The common length of the columns is
 * set at construction and cannot be changed.
 * <p>
 * The label in the DataInfo for this object should be descriptive of the table
 * as a whole; the dimension in the DataInfo will be Dimension.MIXED if the dimensions
 * of the data in the columns are not all the same, otherwise it will be the common
 * dimension of the data in the columns.
 * 
 * @author David Kofke and Andrew Schultz
 *  
 */

/*
 * History Created on Jul 28, 2005 by kofke
 */
public class DataTable extends Data implements DataArithmetic, Serializable {

    /**
     * Creates a new table using the given arguments. The length of the DataInfo
     * array specifies the number of columns.  No row headers are defined.
     * 
     * @param label
     *            a descriptive label for the table
     * @param columnDataInfo
     *            set of DataInfo instances used to set the heading and
     *            dimension of each column
     * @param nRows
     *            the length of each and every column
     */
    public DataTable(String label, DataInfo[] columnDataInfo, int nRows) {
        this(label, evaluateDimension(columnDataInfo), 
                        makeColumns(columnDataInfo, nRows));
    }
    
    //bridge to the next private constructor; makes this instances columns the same as
    //the prototype in the factory
    private DataTable(String label, Dimension dimension, Column[] columns) {
        this(label, dimension, columns, new Factory(columns));
    }
    
    //used by Factory and by constructor above
    private DataTable(String label, Dimension dimension, Column[] columns, Factory factory) {
        super(new DataInfo(label, dimension, factory));
        rowHeaders = factory.rowHeaders;
        myColumns = columns;
    }
    
    //determines if a given set of DataInfo are all of the same dimension;
    //if not the same, returns Dimension.MIXED
    //used by constructor above.
    private static Dimension evaluateDimension(DataInfo[] dataInfo) {
        Dimension dim = null;
        for(int i=0; i<dataInfo.length; i++) {
            if((dataInfo[i].getDimension() != dim) && (dim != null)) {
                return Dimension.MIXED;
            }
            dim = dataInfo[i].getDimension();
        }
        return dim;
    }
    //used by constructor above
    private static Column[] makeColumns(DataInfo[] columnDataInfo,
            int nRows) {
        Column[] columns = new Column[columnDataInfo.length];
        for (int i = 0; i < columnDataInfo.length; i++) {
            columns[i] = new Column(new double[nRows], columnDataInfo[i]);
        }
        return columns;
    }

    /**
     * Creates a new table with a specified number of columns all of a given
     * length. The assigned heading for each column is its ordinal (starting
     * from 0), e.g., the heading of the first column is '0'.  All columns are
     * assigned the given Dimension, which is also the Dimension of the DataTable
     * (as kept in DataInfo).  No row headers are defined.
     * 
     * @param label
     *            a descriptive label for the table
     * @param dimension
     *            indicates the common Dimension for all columns
     * @param nColumns
     *            the number of columns to make
     * @param nRows
     *            the length of each and every column
     */
    public DataTable(String label, Dimension dimension, int nColumns, int nRows) {
        super(new DataInfo(label, dimension, new Factory(makeColumns(
                dimension, nColumns, nRows))));
        myColumns = ((DataTable.Factory) dataInfo.getDataFactory()).prototypeColumns;
        rowHeaders = ((DataTable.Factory) dataInfo.getDataFactory()).rowHeaders;
    }
    
    /**
     * Creates a new table with a specified number of columns and with row
     * headers as given. Number of rows is determined by the length of the
     * rowHeaders array. The assigned heading for each column is its ordinal
     * (starting from 0), e.g., the heading of the first column is '0'. All
     * columns are assigned the given Dimension, which is also the Dimension of
     * the DataTable (as kept in DataInfo).
     * 
     * @param label
     *            a descriptive label for the table
     * @param dimension
     *            indicates the common Dimension for all columns
     * @param nColumns
     *            the number of columns to make
     * @param rowHeaders
     *            array of Strings with descriptive labels for the rows; the
     *            number of rows is the length of this array
     */
    public DataTable(String label, Dimension dimension, int nColumns, String[] rowHeaders) {
        super(new DataInfo(label, dimension, 
                new Factory(makeColumns(dimension, nColumns, rowHeaders.length), rowHeaders)));
        myColumns = ((DataTable.Factory) dataInfo.getDataFactory()).prototypeColumns;
        this.rowHeaders = ((DataTable.Factory) dataInfo.getDataFactory()).rowHeaders;
    }
    
    //used by constructor above
    private static Column[] makeColumns(Dimension dimension, int nColumns,
            int nRows) {
        Column[] columns = new Column[nColumns];
        for (int i = 0; i < nColumns; i++) {
            columns[i] = new Column(new double[nRows], Integer.toString(i), dimension);
        }
        return columns;
    }


    /**
     * Constructs a new table using the data in the given columns.  No row headers are defined.
     * 
     * @param label
     *            a descriptive label for the table
     * @param myColumns
     *            the columns used to make the table (the array instance is cloned,
     *            but the columns are not).
     * 
     * @throws IllegalArgumentException
     *             if given columns are not all of the same length
     */
    public DataTable(String label, DataTable.Column[] myColumns) {
        super(new DataInfo(label, evaluateDimension(myColumns), new Factory(myColumns)));
        for(int i=1; i<myColumns.length; i++) {
            if(myColumns[i].data.length != myColumns[i-1].data.length) {
                throw new IllegalArgumentException("DataTable requires that all columns be of the same length");
            }
        }
        this.myColumns = (DataTable.Column[])myColumns.clone();
        rowHeaders = ((DataTable.Factory) dataInfo.getDataFactory()).rowHeaders;
    }
    //determines if a given set of Columns are all of the same dimension;
    //if not the same, returns Dimension.MIXED
    //used by constructor above.
    private static Dimension evaluateDimension(Column[] columns) {
        Dimension dim = null;
        for(int i=0; i<columns.length; i++) {
            if((columns[i].dimension != dim) && (dim != null)) {
                return Dimension.MIXED;
            }
            dim = columns[i].dimension;
        }
        return dim;
    }

    /**
     * Copy constructor. Makes a new DataTable with new Column instances having
     * the same data values and row headings as in the given DataTable.
     */
    public DataTable(DataTable dataTable) {
        super(dataTable);
        myColumns = new DataTable.Column[dataTable.myColumns.length];
        for (int i = 0; i < myColumns.length; i++) {
            myColumns[i] = new DataTable.Column(dataTable.myColumns[i]);
        }
        rowHeaders = ((DataTable.Factory) dataInfo.getDataFactory()).rowHeaders;
    }

    /**
     * Makes a copy of this DataTable using the copy constructor.
     */
    public Data makeCopy() {
        return new DataTable(this);
    }
    
    /**
     * Returns the row header (a descriptive string for the row) for the i-th row
     * of the table (numbered from zero).  Returns and empty string if no row
     * headers were specified at construction.
     */
    public String getRowHeaders(int i) {
        return (rowHeaders == null) ? "" : rowHeaders[i];
    }
    
    public void setRowHeaders(String[] rowHeaders) {
        if (rowHeaders == null) {
            this.rowHeaders = null;
            return;
        }
        if (getNRows() != rowHeaders.length) {
            throw new IllegalArgumentException("number of row headers must match number of rows");
        }
        this.rowHeaders = (String[])rowHeaders.clone();
    }

    /**
     * Copies all data values from the given table to this one.
     * 
     * @throws ClassCastException
     *             if the argument is not an instance of DataTable
     */
    public void E(Data data) {
        DataTable table = (DataTable) data;
        for (int i = 0; i < myColumns.length; i++) {
            System.arraycopy(table.myColumns[i].data, 0, myColumns[i].data, 0,
                    myColumns[i].data.length);
        }
    }

    /**
     * Returns the i-th column of the table, numbering from zero.
     */
    public Column getColumn(int i) {
        return myColumns[i];
    }

    /**
     * Returns the number of columns in the table.
     */
    public int getNColumns() {
        return myColumns.length;
    }

    /**
     * Returns the number of rows in each and every column of the table.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             if the DataTable has zero columns
     */
    public int getNRows() {
        return myColumns[0].data.length;
    }
    
    /**
     * Plus-equals (+=) operation.
     */
    public void PE(DataArithmetic y) {
        if(myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        DataTable table = (DataTable) y;
        for (int i = 0; i < myColumns.length; i++) {
            for(int j=0; j<nRows; j++) {
                myColumns[i].data[j] += table.myColumns[i].data[j];
            }
        }
    }

    /**
     * Minus-equals (-=) operation.
     */
    public void ME(DataArithmetic y) {
        if(myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        DataTable table = (DataTable) y;
        for (int i = 0; i < myColumns.length; i++) {
            for(int j=0; j<nRows; j++) {
                myColumns[i].data[j] -= table.myColumns[i].data[j];
            }
        }
    }

    /**
     * Times-equals (*=) operation.
     */
    public void TE(DataArithmetic y) {
        if(myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        DataTable table = (DataTable) y;
        for (int i = 0; i < myColumns.length; i++) {
            for(int j=0; j<nRows; j++) {
                myColumns[i].data[j] *= table.myColumns[i].data[j];
            }
        }
    }

    /**
     * Divide-equals (/=) operation.
     */
    public void DE(DataArithmetic y) {
        if(myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        DataTable table = (DataTable) y;
        for (int i = 0; i < myColumns.length; i++) {
            for(int j=0; j<nRows; j++) {
                myColumns[i].data[j] /= table.myColumns[i].data[j];
            }
        }
    }

    /**
     * Equals (=) operation, sets all values in data equal to the given value.
     */
    public void E(double y) {
        {
            if(myColumns.length == 0) {
                return;
            }
            int nRows = getNRows();
            for (int i = 0; i < myColumns.length; i++) {
                for(int j=0; j<nRows; j++) {
                    myColumns[i].data[j] = y;
                }
            }
        }
    }

    /**
     * Plus-equals (+=) operation, adding given value to all values in data.
     */
    public void PE(double y) {
        if (myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        for (int i = 0; i < myColumns.length; i++) {
            for (int j = 0; j < nRows; j++) {
                myColumns[i].data[j] += y;
            }
        }       
    }

    /**
     * Times-equals (*=) operation, multiplying all values in data by given
     * value.
     */
    public void TE(double y) {
        if (myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        for (int i = 0; i < myColumns.length; i++) {
            for (int j = 0; j < nRows; j++) {
                myColumns[i].data[j] *= y;
            }
        }       
    }

    /**
     * Maps the function on all data values, replace each with the value given
     * by the function applied to it.
     */
    public void map(Function function) {
        if (myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        for (int i = 0; i < myColumns.length; i++) {
            Column column = myColumns[i];
            for (int j = 0; j < nRows; j++) {
                column.data[j] = function.f(column.data[j]);
            }
        }       

    }

    /**
     * Returns the number of values held by the data instance.
     */
    public int getLength() {
        if(myColumns.length == 0) {
            return 0;
        }
        return getNRows() * myColumns.length;
    }

    /**
     * Returns the i-th data value, where the values are numbered by counting down the first column,
     * then the next, and so on. 
     * <p>
     * Specifically, returns column[i/nRows].data[i % nRows].
     * 
     * @param i
     *            index of the desired data value
     * @return the data value
     * @throws IllegalArgumentException
     *             if i >= getLength()
     */
    public double getValue(int i) {
        if(i < 0 || i >= getLength()) {
            throw new IllegalArgumentException("Illegal index for get value. Maximum is "+getLength());
        }
        int nRows = getNRows();
        return myColumns[i/nRows].data[i % nRows];
    }

    /**
     * Returns a new array formed from the values held by the data instance.
     * 
     * @throws IllegalArgumentException
     *             if the length of the array is not equal to getLength
     */
    public void assignTo(double[] array) {
        if(array.length != getLength()) {
            throw new IllegalArgumentException("Illegal array size; length must be: "+getLength());
        }
        if(myColumns.length == 0) {
            return;
        }
        int nRows = getNRows();
        for(int i=0; i<myColumns.length; i++) {
            System.arraycopy(myColumns[i].data, 0, array, i*nRows, nRows);
        }
    }

    /**
     * Returns true if any data value is true for Double.isNaN
     */
    public boolean isNaN() {
        if(myColumns.length == 0) {
            return true;
        }
        int nRows = getNRows();
        for(int i=0; i<myColumns.length; i++) {
            for(int j=0; j<nRows; j++) {
                if(Double.isNaN(myColumns[i].data[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    //make package protected so that CastToTable can access
    //shadows myColumns field in Factory, which is accessible via DataInfo
    final DataTable.Column[] myColumns;
    private String[] rowHeaders;

    /**
     * Returns a new DataFactory that makes DataTable instances with the shape
     * indicates by the given set of columns, and using the header/dimension
     * info for the given columns.  Data arrays in the new instances are independent
     * of the given columns' data (except for being initialized to those values).
     */
    public static DataFactory getFactory(Column[] columns) {
        return new Factory(columns);
    }

    /**
     * DataFactory that makes DataTable instances of a specific shape.  Each DataTable
     * constructed by the factory has its own DataColumn instances, with data that
     * are initialized to a prototype set of data.  All constructed instances reference
     * the same rowHeader array (if not null).
     */
    public static class Factory implements DataFactory, Serializable {

        private final Column[] prototypeColumns;
        private final String[] rowHeaders;

        Factory(Column[] myColumns) {
            this(myColumns, null);
        }
        
        Factory(Column[] myColumns, String[] rowHeaders) {
            this.prototypeColumns = (Column[])myColumns.clone();
            if(rowHeaders != null) {
                this.rowHeaders = (String[])rowHeaders.clone();
            } else {
                this.rowHeaders = null;
            }
        }

        /**
         * Makes a new DataTable, assigning the descriptive label to it.  If dimension
         * is null, columns in new table will have dimensions given by the corresponding
         * columns in the factory's prototype table; if dimension is not null, all columns
         * in the new table will have the given dimension.
         */
        public Data makeData(String label, Dimension dimension) {
            DataTable.Column[] newColumns = new DataTable.Column[prototypeColumns.length];
            for (int i = 0; i < newColumns.length; i++) {
                if(dimension == null) {
                    newColumns[i] = new DataTable.Column(prototypeColumns[i]);
                } else {
                    newColumns[i] = new DataTable.Column((double[])prototypeColumns[i].data.clone(), 
                            prototypeColumns[i].heading, dimension);
                }
            }
            if(dimension == null) {
                dimension = newColumns[0].getDimension();
                for(int i = 1; i < newColumns.length; i++) {
                    if((newColumns[i].getDimension() != dimension)) {
                        dimension = Dimension.MIXED;
                        break;
                    }
                }
            }
            return new DataTable(label, dimension, newColumns, this);
        }

        /**
         * Returns DataTable.class.
         */
        public Class getDataClass() {
            return DataTable.class;
        }

        /**
         * Returns the number of columns in the DataTable made by this
         * DataFactory.
         * 
         * @return
         */
        public int getNColumns() {
            return prototypeColumns.length;
        }

        /**
         * Returns the length of each and every row in the DataTable made by
         * this DataFactory.
         */
        public int getNRows() {
            return prototypeColumns[0].data.length;
        }
        
        public String[] getRowHeaders() {
            return rowHeaders;
        }
    }//end of Factory

    /**
     * Class that wraps a <tt>double[]</tt> array, a descriptive String that
     * is suitable as a heading for the column, and a Dimension indicating the
     * physical dimensions of the data. The wrapped data array is final, so the
     * length of the column cannot be changed after construction.
     */
    public static class Column implements Serializable {

        /**
         * Makes a Column wrapping the given array and using the DataInfo to
         * determine the heading and dimension.  Given data array is used
         * directly, not a copy.
         */
        Column(double[] data, DataInfo dataInfo) {
            this(data, dataInfo.getLabel(), dataInfo.getDimension());
        }

        /**
         * Makes a Column wrapping the given array and using the given values
         * for the heading and dimension.  Given data array is used directly,
         * not a copy.
         */
        Column(double[] data, String label, Dimension dimension) {
            this.data = data;
            this.heading = label;
            this.dimension = dimension;
        }

        /**
         * Copy constructor. Makes independent copies of all data in column.
         */
        public Column(Column column) {
            this.data = (double[]) column.data.clone();
            this.heading = column.heading;
            this.dimension = column.dimension;
        }

        /**
         * Mutator method for the heading.
         */
        public void setHeading(String heading) {
            this.heading = heading;
        }

        /**
         * Accessor method for the heading.
         */
        public String getHeading() {
            return heading;
        }

        /**
         * Accessor method for the (immutable) dimension.
         */
        public Dimension getDimension() {
            return dimension;
        }

        /**
         * Returns the wrapped array.
         */
        public double[] getData() {
            return data;
        }

        private final double[] data;
        private String heading;
        private final Dimension dimension;
    }//end of Column

}

