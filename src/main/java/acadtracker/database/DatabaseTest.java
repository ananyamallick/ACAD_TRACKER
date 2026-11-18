
package acadtracker.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        System.out.println("Connecting to MySQL...");

        try (Connection con =
                DBConnection.getConnection()) {

            if (con != null && !con.isClosed()) {

                System.out.println(
                    "MySQL Connection Successful!"
                );

                System.out.println(
                    "Database: " +
                    con.getCatalog()
                );
            }

        } catch (SQLException e) {

            System.out.println(
                "Database Connection Failed!"
            );

            System.out.println(
                "Reason: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
