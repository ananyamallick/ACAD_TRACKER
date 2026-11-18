
package acadtracker.main;

import acadtracker.database.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordSetupTest {

    public static void main(String[] args) {

        // Temporary demonstration passwords.
        // Change these before using the application for real accounts.
        setPassword(1, "Student@123");
        setPassword(2, "Faculty@123");
    }

    private static void setPassword(int userId, String password) {

        String hash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, hash);
            statement.setInt(2, userId);

            int rows = statement.executeUpdate();

            if (rows == 1) {
                System.out.println(
                        "Password configured for user ID: " + userId
                );
            } else {
                System.out.println(
                        "No user found with ID: " + userId
                );
            }

        } catch (SQLException e) {
            System.out.println(
                    "Unable to configure password: " + e.getMessage()
            );
        }
    }
}
