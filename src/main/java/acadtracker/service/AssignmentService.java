
package acadtracker.service;

import acadtracker.dao.AssignmentDAO;
import acadtracker.dao.AssignmentDAOImpl;
import acadtracker.dao.StudentAssignmentDAO;
import acadtracker.dao.StudentAssignmentDAOImpl;
import acadtracker.model.Assignment;
import acadtracker.model.AssignmentStatus;
import acadtracker.model.StudentAssignment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AssignmentService {

    private final AssignmentDAO assignmentDAO;
    private final StudentAssignmentDAO studentAssignmentDAO;

    public AssignmentService() {
        this.assignmentDAO = new AssignmentDAOImpl();
        this.studentAssignmentDAO =
            new StudentAssignmentDAOImpl();
    }

    // Create an assignment
    public boolean createAssignment(Assignment assignment)
            throws SQLException {

        if (assignment == null) {
            throw new IllegalArgumentException(
                "Assignment cannot be null."
            );
        }

        if (assignment.getSubjectId() <= 0) {
            throw new IllegalArgumentException(
                "Invalid subject ID."
            );
        }

        if (assignment.getTitle() == null ||
                assignment.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Assignment title is required."
            );
        }

        if (assignment.getAssignedDate() == null ||
                assignment.getDueDate() == null) {
            throw new IllegalArgumentException(
                "Assignment dates are required."
            );
        }

        if (assignment.getDueDate().isBefore(
                assignment.getAssignedDate())) {
            throw new IllegalArgumentException(
                "Due date cannot be before assigned date."
            );
        }

        if (assignment.getAssignedDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Assigned date cannot be in the future."
            );
        }

        return assignmentDAO.createAssignment(assignment);
    }

    // Retrieve all assignments
    public List<Assignment> getAllAssignments()
            throws SQLException {

        return assignmentDAO.findAll();
    }

    // Retrieve assignments for a subject
    public List<Assignment> getAssignmentsBySubject(int subjectId)
            throws SQLException {

        if (subjectId <= 0) {
            throw new IllegalArgumentException(
                "Invalid subject ID."
            );
        }

        return assignmentDAO.findBySubject(subjectId);
    }

    // Update a student's assignment progress
    public boolean updateStudentStatus(
            int assignmentId,
            int studentId,
            AssignmentStatus status) throws SQLException {

        if (assignmentId <= 0 || studentId <= 0) {
            throw new IllegalArgumentException(
                "Invalid assignment or student ID."
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                "Assignment status is required."
            );
        }

        if (!assignmentDAO.assignmentExists(assignmentId)) {
            throw new IllegalArgumentException(
                "Assignment does not exist."
            );
        }

        return studentAssignmentDAO.updateStatus(
            assignmentId,
            studentId,
            status.name()
        );
    }

    // Retrieve a student's assignment report
    public List<StudentAssignment> getStudentAssignmentReport(
            int studentId) throws SQLException {

        if (studentId <= 0) {
            throw new IllegalArgumentException(
                "Invalid student ID."
            );
        }

        return studentAssignmentDAO.findByStudent(studentId);
    }

    public boolean updateAssignment(Assignment assignment)
        throws SQLException {

    return assignmentDAO.updateAssignment(assignment);
}

}
