
package acadtracker.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRetrievalTest {

    public static void main(String[] args) {

        String sql =
            "SELECT user_id, name, email, role " +
            "FROM users WHERE role = ?";

        try (
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(sql)
        ) {

            // Set the role parameter
            ps.setString(1, "STUDENT");

            // Execute SELECT query
            try (ResultSet rs = ps.executeQuery()) {

                System.out.println(
                    "======= STUDENT RECORDS ======="
                );

                while (rs.next()) {

                    int id = rs.getInt("user_id");

                    String name =
                        rs.getString("name");

                    String email =
                        rs.getString("email");

                    String role =
                        rs.getString("role");

                    System.out.println(
                        "ID: " + id
                    );

                    System.out.println(
                        "Name: " + name
                    );

                    System.out.println(
                        "Email: " + email
                    );

                    System.out.println(
                        "Role: " + role
                    );

                    System.out.println("------------------------");
                }
            }

        } catch (SQLException e) {

            System.out.println(
                "Unable to retrieve student records."
            );

            e.printStackTrace();
        }
    }
}
