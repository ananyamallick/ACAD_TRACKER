package acadtracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {

    // Establish connection with MySQL
    public static Connection getConnection()
            throws SQLException {

        Properties properties = new Properties();

        // Load database configuration
        try (InputStream input =
                DBConnection.class.getResourceAsStream(
                        "/db.properties")) {

            if (input == null) {
                throw new SQLException(
                        "Database configuration file not found."
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new SQLException(
                    "Unable to load database configuration.", e
            );
        }

        // Read database details
        String url = properties.getProperty("db.url");
        String username =
                properties.getProperty("db.username");
        String password =
                properties.getProperty("db.password");

        if (url == null || username == null ||
                password == null) {

            throw new SQLException(
                    "Database configuration is incomplete."
            );
        }

        Connection connection = DriverManager.getConnection(
                url, username, password
        );

        try {
            // Keep older databases compatible with sign-up and editing.
            SchemaManager.ensure(connection);
            return connection;
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
    }
}
