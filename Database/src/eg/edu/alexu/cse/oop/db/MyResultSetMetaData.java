package eg.edu.alexu.cse.oop.db;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by Mohamed Abdelrehim on 4/28/2017.
 */
public class MyResultSetMetaData implements ResultSetMetaData {

    private Object[][] result;
    private int currentRow;

    public MyResultSetMetaData(Object[][] result, int currentRow) {
        this.result = result;
        this.currentRow = currentRow;
    }

    @Override //TODO
    public int getColumnCount() throws SQLException {
        return this.result[0].length;
    }

    @Override //TODO
    public String getColumnLabel(int column) throws SQLException {
        return (String) this.result[0][column - 1];
    }

    @Override //TODO
    public String getColumnName(int column) throws SQLException {
        return (String) this.result[0][column - 1];
    }

    @Override //TODO
    public String getTableName(int column) throws SQLException {
        return null;
    }

    @Override //TODO
    public int getColumnType(int column) throws SQLException {
        return 0;
    }










    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return 0;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }


    @Override
    public String getSchemaName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }



    @Override
    public String getCatalogName(int column) throws SQLException {
        return null;
    }



    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return null;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    public static void main(String[] args) {

    }
}
