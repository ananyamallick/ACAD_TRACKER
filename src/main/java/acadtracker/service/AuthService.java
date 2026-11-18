
package acadtracker.service;

import acadtracker.dao.AuthDAO;
import acadtracker.dao.AuthDAOImpl;
import acadtracker.exceptions.AuthenticationException;
import acadtracker.model.AuthenticatedUser;
import acadtracker.model.LoginRecord;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService() {
        this.authDAO = new AuthDAOImpl();
    }

    public AuthenticatedUser login(String email, String password)
            throws SQLException, AuthenticationException {

        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Email is required.");
        }

        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is required.");
        }

        LoginRecord record =
                authDAO.findByEmail(email.trim());

        // Use the same message for unknown email and wrong password.
        if (record == null) {
            throw new AuthenticationException(
                    "Invalid email or password."
            );
        }

        String storedHash = record.getPasswordHash();

        if (storedHash == null || !storedHash.startsWith("$2")) {
            throw new AuthenticationException(
                    "This account has not been configured for secure login."
            );
        }

        boolean passwordMatches;

        try {
            passwordMatches =
                    BCrypt.checkpw(password, storedHash);
        } catch (IllegalArgumentException e) {
            passwordMatches = false;
        }

        if (!passwordMatches) {
            throw new AuthenticationException(
                    "Invalid email or password."
            );
        }

        return record.getUser();
    }
}
