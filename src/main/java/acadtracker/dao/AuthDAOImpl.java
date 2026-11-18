
package acadtracker.dao;

import acadtracker.database.DBConnection;
import acadtracker.model.AuthenticatedUser;
import acadtracker.model.LoginRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAOImpl implements AuthDAO {

    @Override
    public LoginRecord findByEmail(String email)
            throws SQLException {

        String sql = """
                SELECT user_id, name, email, password_hash, role
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    AuthenticatedUser user =
                            new AuthenticatedUser(
                                    result.getInt("user_id"),
                                    result.getString("name"),
                                    result.getString("email"),
                                    result.getString("role")
                            );

                    String passwordHash =
                            result.getString("password_hash");

                    return new LoginRecord(user, passwordHash);
                }
            }
        }

        return null;
    }
}
