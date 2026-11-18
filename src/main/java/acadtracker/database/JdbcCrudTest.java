
package acadtracker.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcCrudTest {

    public static void main(String[] args) {

        String email = "jdbc.test@gmail.com";

        try (Connection con =
                DBConnection.getConnection()) {

            // Start transaction
            con.setAutoCommit(false);

            try {

                // INSERT
                String insertSQL =
                    "INSERT INTO users " +
                    "(name, email, password_hash, role) " +
                    "VALUES (?, ?, ?, ?)";

                try (PreparedStatement ps =
                        con.prepareStatement(insertSQL)) {

                    ps.setString(1, "JDBC Test Student");
                    ps.setString(2, email);
                    ps.setString(3, "TEMP_TEST_HASH");
                    ps.setString(4, "STUDENT");

                    ps.executeUpdate();

                    System.out.println(
                        "INSERT successful."
                    );
                }

                // UPDATE
                String updateSQL =
                    "UPDATE users SET name = ? " +
                    "WHERE email = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(updateSQL)) {

                    ps.setString(1, "Updated JDBC Student");
                    ps.setString(2, email);

                    ps.executeUpdate();

                    System.out.println(
                        "UPDATE successful."
                    );
                }

                // SELECT
                String selectSQL =
                    "SELECT * FROM users WHERE email = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(selectSQL)) {

                    ps.setString(1, email);

                    try (ResultSet rs =
                            ps.executeQuery()) {

                        if (rs.next()) {

                            System.out.println(
                                "Student Name: " +
                                rs.getString("name")
                            );
                        }
                    }
                }

                // DELETE
                String deleteSQL =
                    "DELETE FROM users WHERE email = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(deleteSQL)) {

                    ps.setString(1, email);

                    ps.executeUpdate();

                    System.out.println(
                        "DELETE successful."
                    );
                }

                // Undo all test operations
                con.rollback();

                System.out.println(
                    "Transaction rolled back."
                );

            } catch (SQLException e) {

                con.rollback();

                System.out.println(
                    "Transaction failed and rolled back."
                );

                throw e;
            }

        } catch (SQLException e) {

            System.out.println(
                "Database error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
