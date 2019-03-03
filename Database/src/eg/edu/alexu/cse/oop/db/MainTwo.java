package eg.edu.alexu.cse.oop.db;

import java.sql.*;

/**
 * Created by Mohamed Abdelrehim on 4/30/2017.
 */
public class MainTwo {
    public static void  main (String[] args) {

        Driver d = new MyDriver();
        try {
            Connection c = d.connect("file:///D:/myFolder", null);
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT col1, col2, col3 FROM test WHERE col2='1';");
            System.out.println("part2");
            System.out.println(r.findColumn("col2"));
            r.next();
            System.out.println(r.getInt(2));
            r.next();
            System.out.println(r.getInt("col3"));
            ResultSetMetaData metaData = r.getMetaData();
            System.out.println(metaData.getColumnName(2));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
