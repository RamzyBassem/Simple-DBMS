package eg.edu.alexu.cse.oop.db;

import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Abdelrehim on 4/20/2017.
 */

/*dependencies -> Parser.java
executes sql commands functionalities*/
public class SQL {

    private Parser parser;

    public SQL() {

        this.parser = new Parser();

    }

    //checks whether the given table exist TODO DONE
    private boolean tableExists(String tableName, String path) {

        Path xmlPath = Paths.get(path + tableName + ".xml");
        Path xsdPath = Paths.get(path + tableName + ".xsd");

        if (Files.exists(xmlPath) && Files.exists(xsdPath)) {
            // files exist
            return true;
        }

        else {
            // files do not exist
            return false;
        }

    }

    //Returns true if the table is successfully created TODO DONE
    public boolean createTable(List<String> tableElements, List<String> dataTypes, String tableName, String path) {

        try {
            parser.writeXML(tableName, path);
            parser.writeXSD (tableElements, dataTypes, tableName, path);

            //existingTables.add(tableName);
            return true;
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    //checks first if the table was previously added then calls parser.deleteTable returns true TODO DONE
    public boolean dropTable(String tableName, String path) {

        if(tableExists(tableName, path)) {

            parser.deleteFiles(tableName, path);
            return true;
        }
        else {
            // tables does not exist
            return false;
        }

    }

    //inserts a given row into a given table and returns the updated rows count in a given table TODO DONE
    public int insert(List<String> columns, List<String> values, String tableName, String path) {

        try {
            boolean x = parser.addRow(columns,values,tableName,path);
            return parser.countRows(tableName, path);
        } catch (IOException e) {
            e.printStackTrace();
            return  -1;
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return  -2;
        } catch (SAXException e) {
            e.printStackTrace();
            return -3;
        }

    }

    // deletes all rows where the condition holds from table TODO DONE
    public int delete(String conditionLHS, String operator, String conditionRHS, String tableName, String path) {

        try {
            if(tableExists(tableName, path)) {

                parser.deleteRow(conditionLHS, operator, conditionRHS, tableName, path);
                return parser.countRows(tableName, path);

            }
            else {
                System.out.println("Table does not exist");
                return -1;
            }
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            System.out.println("xml stream exception");
            return -2;
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("i-o exception");
            return -3;
        }

    }

    // gets user selected columns where a condition holds in an object @D array TODO DONE
    public Object[][] select(String conditionLHS, String operator, String conditionRHS,
                             List<String> requiredColumns ,String tableName, String path) {

       Object[][] selected;

       if (tableExists(tableName, path)) {

           try {
               selected = parser.getSelected(conditionLHS, operator, conditionRHS, requiredColumns, tableName, path);
               return selected;
           }
           catch (Exception e) {
               e.printStackTrace();
               System.out.println("no such row");
               selected = new Object[0][0];
               return selected;
           }

       }
       else {
           selected = new Object[0][0];
           return selected;
       }

    }

    //for testing purposes
    public static void main(String[] args) {

        SQL sql = new SQL();
        List cols = new ArrayList();
        List vals = new ArrayList();
        cols.add("first");
        cols.add("second");
        cols.add("third");
        vals.add("  sma7");
        vals.add("5");
        vals.add(" mde7a");
        sql.insert(cols, vals, "test", "");
        //sql.delete("second", "=", "90", "test", "");


    }

}
