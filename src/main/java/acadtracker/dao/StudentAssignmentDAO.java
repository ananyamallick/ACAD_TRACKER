
package acadtracker.dao;

import acadtracker.model.StudentAssignment;

import java.sql.SQLException;
import java.util.List;

public interface StudentAssignmentDAO {

    boolean updateStatus(
        int assignmentId,
        int studentId,
        String status
    ) throws SQLException;

    /**
     * Faculty update of status and grade. A null grade clears the grade.
     */
    boolean updateRecord(
        int assignmentId,
        int studentId,
        String status,
        String grade
    ) throws SQLException;

    List<StudentAssignment> findByStudent(int studentId)
        throws SQLException;
}
