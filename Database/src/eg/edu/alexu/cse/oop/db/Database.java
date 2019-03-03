package eg.edu.alexu.cse.oop.db;


import java.sql.SQLException;

/**
 * Created by Mohamed Abdelrehim on 4/15/2017.
 */
public interface Database {

    /**
     * Creates/drops table or database.
     * @param query structure definition query (e.g., create or drop table)
     * @returns true if success, or false otherwise
     * @throws SQLException syntax error
     */
    boolean executeStructureQuery(String query) throws java.sql.SQLException;


    /**
     * Select from table
     * @param query data retrieval query (e.g., select)
     * @return the selected records or an empty array if no records match. Columns
    types must be preserved, i.e, Columns of “int” data type return
    Integer objects, and columns of “varchar” data type return String
    objects)
     * @throws SQLException syntax error
     */
    Object[][] executeRetrievalQuery(String query) throws java.sql.SQLException;


    /**
     * Insert or update or delete the data
     * @param query data manipulation query (e.g., insert, update or delete)
     * @return the updated rows count
     * @throws SQLException syntax error
     */
    int executeUpdateQuery(String query) throws java.sql.SQLException;

}
