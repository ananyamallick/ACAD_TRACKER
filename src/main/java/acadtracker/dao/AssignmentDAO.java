package acadtracker.dao;

import acadtracker.model.Assignment;
import java.sql.SQLException;
import java.util.List;

public interface AssignmentDAO {

    boolean createAssignment(Assignment assignment)
            throws SQLException;

    List<Assignment> findAll()
            throws SQLException;

    List<Assignment> findBySubject(int subjectId)
            throws SQLException;

    boolean assignmentExists(int assignmentId)
            throws SQLException;

    // NEW: Update existing assignment
    boolean updateAssignment(Assignment assignment)
            throws SQLException;
}