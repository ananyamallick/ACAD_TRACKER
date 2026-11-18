
package acadtracker.dao;

import acadtracker.model.LoginRecord;
import java.sql.SQLException;

public interface AuthDAO {

    LoginRecord findByEmail(String email)
            throws SQLException;
}
