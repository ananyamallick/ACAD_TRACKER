
package acadtracker.model;

public class LoginRecord {

    private final AuthenticatedUser user;
    private final String passwordHash;

    public LoginRecord(
            AuthenticatedUser user,
            String passwordHash
    ) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
