package eg.edu.alexu.cse.oop.db;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;


public class MyDatabase implements Database {
    private SQL sql;
    private String path;

    public MyDatabase() {
        this.sql = new SQL();
        this.path = "";
    }
    public void setPath(Path path) {
        this.path = path.toString() + '/';
    }


    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        boolean z = false;
        if (query.contains("CREATE TABLE")) {
            String x = query.substring(0,query.indexOf("("));
            String[] first = x.split("\\s+");
            String name = first[2];
            System.out.println(name); //////take name from here
            String g = query.replaceAll("\\s+", " ");
            g = g.replaceAll("\\s", ",");
            g = g.replaceAll(",+", ",");
            String p = g.substring(g.indexOf("(") + 1);
            p = "," + p.substring(0, p.indexOf(")"));
            p= p.replaceAll(",+",",");
            String str = "varchar";
            String in = "int";
            String[] out = p.split(",");
            ArrayList<String> column = new ArrayList();
            ArrayList<String> datatype = new ArrayList();
            for (int i = 1; i < out.length; i++) {
                if (i % 2 != 0)
                    column.add(out[i]);
                else if (str.equalsIgnoreCase(out[i]))
                    datatype.add("string");
                else if (in.equalsIgnoreCase(out[i]))
                    datatype.add("integer");
                else System.out.println("Unsported datatype");
            }
            for (int i = 0; i < datatype.size(); i++)
                System.out.println(datatype.get(i));
            for (int i = 0; i < column.size(); i++)
                System.out.println(column.get(i));
            SQL sql = new SQL();
            z = sql.createTable(column, datatype, name,path);

        } else if (query.contains("DROP")) {
            String g = query.replaceAll("\\s+", ",");
            g = g.replaceAll(";", ",");
            g = g.replaceAll(",+", ",");
            String[] out = g.split(",");
            String name = out[2];
            System.out.println(name);
            SQL sql = new SQL();
            z = sql.dropTable(name,path);
            return z;
        }
        return z;
    }


    @Override
    public Object[][] executeRetrievalQuery(String query) throws SQLException {
        if (query.contains("SELECT")) {
            String g = query.replaceAll("\\s+", " ");
            g = g.replaceAll("\\s", ",");
            g = g.replaceAll(";", "");
            g = g.replaceAll(",+", ",");
            String[] fout = g.split(",");
            String name = fout[fout.length - 3];
            System.out.println(name);
            String condition = fout[fout.length - 1];
            String LHS = new String();
            String RHS = new String();
            String operator = new String();
            if (condition.contains("=")) {
                LHS = condition.substring(0, condition.indexOf("="));
                operator = "=";
                RHS = condition.substring(condition.indexOf("=") + 1);
                RHS = RHS.replaceAll("'", "");
            } else if (condition.contains(">")) {
                LHS = condition.substring(0, condition.indexOf(">"));
                operator = ">";
                RHS = condition.substring(condition.indexOf(">") + 1);
                RHS = RHS.replaceAll("'", "");
            } else if (condition.contains("<")) {
                LHS = condition.substring(0, condition.indexOf("<"));
                operator = "<";
                RHS = condition.substring(condition.indexOf("<") + 1);
                RHS = RHS.replaceAll("'", "");
            }

            System.out.println(condition);
            System.out.println(LHS);
            System.out.println(operator);
            System.out.println(RHS);
            String b = g.substring(g.indexOf(",") + 1);
            b = b.substring(0, b.indexOf("FROM") - 1);
            String[] col = b.split(",");
            ArrayList columns = new ArrayList();
            for (int i = 0; i < col.length; i++)
                columns.add(col[i]);
            for (int i = 0; i < columns.size(); i++)
                System.out.println(columns.get(i));
            SQL sql = new SQL();
            Object[][] out =sql.select(LHS,operator,RHS,columns,name,path);
            return out;

        }
        else{
            System.out.println("Invalid Input");
            return new Object[0][0];
        }

    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        int z=0;
        if (query.contains("INSERT INTO")) {
            String x = query.substring(0,query.indexOf("("));
            String[] first = x.split("\\s+");
            String name = first[2];
            System.out.println(name); //////take name from here
            String g = query.replaceAll("\\s+", " ");
            g = g.replaceAll("\\s", ",");
            g = g.replaceAll(",+", ",");
            System.out.println(g);
            String col = g.substring(g.indexOf("(") + 1);
            col = col.substring(0, col.indexOf(")"));
            System.out.println(col);
            String[] out = col.split(",");
            ArrayList column = new ArrayList();
            for (int i = 0; i < out.length; i++) {
                column.add(out[i]);
                System.out.println(out[i]);
            }
            String val = query.substring(g.indexOf("(", g.indexOf("(") + 1) + 1);
            val = val.substring(val.indexOf("(")+1, val.indexOf(")"));
            val = val.replaceAll("\\s+","");
            System.out.println(val);
            String[] valout = val.split(",");
            System.out.println(valout.length);
            ArrayList values = new ArrayList();
            for (int i = 0; i < valout.length; i++) {
                values.add(valout[i]);
                System.out.println(valout[i]);
            }
            SQL sql = new SQL();
            z = sql.insert(column, values, name,path);
        } else if (query.contains("DELETE FROM")) {
            String g = query.replaceAll("\\s+", ",");
            g = g.replaceAll(";", "");
            g = g.replaceAll(",+", ",");
            String[] out = g.split(",");
            String name = out[2];
            String condition = out[4];
            String LHS = new String();
            String RHS = new String();
            String operator = new String();
            if (condition.contains("=")) {
                LHS = condition.substring(0, condition.indexOf("="));
                operator = "=";
                RHS = condition.substring(condition.indexOf("=") + 1);
                RHS = RHS.replaceAll("'", "");
            } else if (condition.contains(">")) {
                LHS = condition.substring(0, condition.indexOf(">"));
                operator = ">";
                RHS = condition.substring(condition.indexOf(">") + 1);
                RHS = RHS.replaceAll("'", "");
            } else if (condition.contains("<")) {
                LHS = condition.substring(0, condition.indexOf("<"));
                operator = "<";
                RHS = condition.substring(condition.indexOf("<") + 1);
                RHS = RHS.replaceAll("'", "");
            }
            System.out.println(name);
            System.out.println(condition);
            System.out.println(LHS);
            System.out.println(operator);
            System.out.println(RHS);
            SQL sql = new SQL();
            z = sql.delete(LHS, operator, RHS, name,path);
        }
        return z;
    }
    public static void main(String[] args) throws SQLException {
        MyDatabase data = new MyDatabase();
        String input = "INSERT INTO test1 (column1, column2, column3) VALUES (Ahmed, 55, 20);";
        int out = data.executeUpdateQuery(input);
        System.out.println(out);


    }
}