package eg.edu.alexu.cse.oop.db;

import java.sql.SQLException;
import java.util.Scanner;

/**3
 * Created by shaheer on 30/04/2017.
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        MyDatabase data = new MyDatabase();
        int z = 0;
        while (z!=6){
            System.out.println("Please select one of the following options");
            System.out.println("1-CREATE TABLE");
            System.out.println("2-DROP TABLE");
            System.out.println("3-INSERT INTO");
            System.out.println("4-DELETE FROM TABLE");
            System.out.println("5-SELECT FROM");
            System.out.println("6-EXIT");
            Scanner scanner= new Scanner(System.in);
            z = scanner.nextInt();
            String sqlquery;
            if (z==1){
                System.out.println("Please ENter PROPER SQL STATMENT");
                Scanner input = new Scanner(System.in);
                sqlquery = input.nextLine();
                if (sqlquery.contains("CREATE TABLE")) {
                    Boolean x = data.executeStructureQuery(sqlquery);
                    System.out.println(x);
                }
                else System.out.println("Invalid SQL");
            }
            else if (z==2){
                System.out.println("Please ENter PROPER SQL STATMENT");
                Scanner input = new Scanner(System.in);
                sqlquery = input.nextLine();
                if (sqlquery.contains("DROP TABLE")) {
                    Boolean x = data.executeStructureQuery(sqlquery);
                    System.out.println(x);
                }
                else System.out.println("Invalid SQL");
            }
            else if (z==3){
                System.out.println("Please ENter PROPER SQL STATMENT");
                Scanner input = new Scanner(System.in);
                sqlquery = input.nextLine();
                if (sqlquery.contains("INSERT INTO")) {
                    int x = data.executeUpdateQuery(sqlquery);
                    if (x==0)
                        System.out.println("Process Success");
                }
                else System.out.println("Invalid SQL");
            }
            else if (z==4){
                System.out.println("Please ENter PROPER SQL STATMENT");
                Scanner input = new Scanner(System.in);
                sqlquery = input.nextLine();
                if (sqlquery.contains("DELETE FROM")) {
                    int x = data.executeUpdateQuery(sqlquery);
                    System.out.println(x);
                }
                else System.out.println("Invalid SQL");
            }
            else if (z==5){
                System.out.println("Please ENter PROPER SQL STATMENT");
                Scanner input = new Scanner(System.in);
                sqlquery = input.nextLine();
                if (sqlquery.contains("SELECT")) {
                    Object[][] x = data.executeRetrievalQuery(sqlquery);

                }
                else System.out.println("Invalid SQL");
            }
        }

    }
}