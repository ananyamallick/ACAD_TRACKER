package acadtracker.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Creates a student or faculty account and links the chosen subjects.
 */
public interface RegistrationDAO {

    /**
     * Subject catalog rows: id, name, code, concerned faculty name.
     */
    List<String[]> findSubjects() throws SQLException;

    /**
     * @param subjectIds existing courses to link
     * @param newSubjects faculty-only courses to create, each {name, code}
     * @return the new user id
     */
    int register(
            String name,
            String email,
            String passwordHash,
            String role,
            List<Integer> subjectIds,
            List<String[]> newSubjects
    ) throws SQLException;
}
