package acadtracker.service;

import acadtracker.model.Assignment;
import acadtracker.model.AuthenticatedUser;

import java.sql.SQLException;
import java.time.LocalDate;

public class FacultyAssignmentService {

    private final FacultyDashboardService dashboardService;
    private final AssignmentService assignmentService;

    public FacultyAssignmentService(AuthenticatedUser faculty) {

        dashboardService =
                new FacultyDashboardService(faculty);

        assignmentService =
                new AssignmentService();
    }

    // CREATE ASSIGNMENT

    public boolean createAssignment(Assignment assignment)
            throws SQLException {

        validateAssignment(assignment);

        if (!dashboardService.ownsSubject(
                assignment.getSubjectId())) {

            throw new IllegalArgumentException(
                    "You cannot create assignments for this subject.");
        }

        if (assignment.getAssignedDate().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Assigned date cannot be in the future.");
        }

        return assignmentService.createAssignment(assignment);
    }

    // NEW: UPDATE ASSIGNMENT

    public boolean updateAssignment(Assignment assignment)
            throws SQLException {

        validateAssignment(assignment);

        if (assignment.getAssignmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Invalid assignment ID.");
        }

        if (!dashboardService.ownsSubject(
                assignment.getSubjectId())) {

            throw new IllegalArgumentException(
                    "You cannot edit assignments for this subject.");
        }

        if (assignment.getAssignedDate().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Assigned date cannot be in the future.");
        }

        return assignmentService.updateAssignment(assignment);
    }

    // VALIDATION

    private void validateAssignment(Assignment assignment) {

        if (assignment == null) {

            throw new IllegalArgumentException(
                    "Assignment cannot be null.");
        }

        if (assignment.getTitle() == null ||
                assignment.getTitle().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Assignment title cannot be empty.");
        }

        if (assignment.getAssignedDate() == null ||
                assignment.getDueDate() == null) {

            throw new IllegalArgumentException(
                    "Please enter valid assignment dates.");
        }

        if (assignment.getDueDate().isBefore(
                assignment.getAssignedDate())) {

            throw new IllegalArgumentException(
                    "Due date cannot be before assigned date.");
        }
    }
}