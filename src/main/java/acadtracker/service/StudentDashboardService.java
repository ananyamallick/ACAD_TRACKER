package acadtracker.service;

import acadtracker.dao.StudentAssignmentDAO;
import acadtracker.dao.StudentAssignmentDAOImpl;
import acadtracker.database.DBConnection;
import acadtracker.model.AuthenticatedUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardService {

    private final AuthenticatedUser user;
    private final StudentAssignmentDAO studentAssignmentDAO;

    public StudentDashboardService(
            AuthenticatedUser user) {

        if (user == null ||
                !"STUDENT".equals(user.getRole())) {

            throw new IllegalArgumentException(
                    "A valid student account is required."
            );
        }

        this.user = user;

        this.studentAssignmentDAO =
                new StudentAssignmentDAOImpl();
    }

    // =========================================================
    // SUBJECTS
    // =========================================================

    public List<String[]> getSubjects()
            throws SQLException {

        List<String[]> subjects =
                new ArrayList<>();

        String sql = """
                SELECT
                    s.subject_id,
                    s.subject_name,
                    s.subject_code,
                    u.name AS faculty_name
                FROM subjects s
                JOIN enrollments e
                    ON s.subject_id = e.subject_id
                JOIN users u
                    ON s.faculty_id = u.user_id
                WHERE e.student_id = ?
                ORDER BY s.subject_name
                """;

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    user.getUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    subjects.add(
                            new String[]{
                                    String.valueOf(
                                            result.getInt(
                                                    "subject_id"
                                            )
                                    ),
                                    result.getString(
                                            "subject_name"
                                    ),
                                    result.getString(
                                            "subject_code"
                                    ),
                                    result.getString(
                                            "faculty_name"
                                    )
                            }
                    );
                }
            }
        }

        return subjects;
    }

    // =========================================================
    // ATTENDANCE
    // =========================================================

    public List<String[]> getAttendance()
            throws SQLException {

        List<String[]> attendance =
                new ArrayList<>();

        String sql = """
                SELECT
                    s.subject_name,
                    a.attendance_date,
                    a.status
                FROM attendance a
                JOIN enrollments e
                    ON a.enrollment_id = e.enrollment_id
                JOIN subjects s
                    ON e.subject_id = s.subject_id
                WHERE e.student_id = ?
                ORDER BY a.attendance_date DESC
                """;

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    user.getUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    attendance.add(
                            new String[]{
                                    result.getString(
                                            "subject_name"
                                    ),
                                    result.getDate(
                                            "attendance_date"
                                    ).toString(),
                                    result.getString(
                                            "status"
                                    )
                            }
                    );
                }
            }
        }

        return attendance;
    }

    // =========================================================
    // ASSIGNMENTS
    // =========================================================

    public List<String[]> getAssignments()
            throws SQLException {

        List<String[]> assignments =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.assignment_id,
                    a.title,
                    s.subject_name,
                    a.due_date,
                    ast.status,
                    ast.submission_date
                FROM assignment_status ast
                JOIN assignments a
                    ON ast.assignment_id =
                       a.assignment_id
                JOIN subjects s
                    ON a.subject_id =
                       s.subject_id
                WHERE ast.student_id = ?
                ORDER BY a.due_date
                """;

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    user.getUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    java.sql.Date submissionDate =
                            result.getDate(
                                    "submission_date"
                            );

                    assignments.add(
                            new String[]{
                                    String.valueOf(
                                            result.getInt(
                                                    "assignment_id"
                                            )
                                    ),
                                    result.getString(
                                            "title"
                                    ),
                                    result.getString(
                                            "subject_name"
                                    ),
                                    result.getDate(
                                            "due_date"
                                    ).toString(),
                                    result.getString(
                                            "status"
                                    ),
                                    submissionDate == null
                                            ? "Not submitted"
                                            : submissionDate.toString()
                            }
                    );
                }
            }
        }

        return assignments;
    }

    // =========================================================
    // UPDATE ASSIGNMENT STATUS
    // =========================================================

    public boolean updateAssignmentStatus(
            int assignmentId,
            String status)
            throws SQLException {

        if (assignmentId <= 0) {

            throw new IllegalArgumentException(
                    "Invalid assignment ID."
            );
        }

        if (status == null ||
                status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Assignment status is required."
            );
        }

        acadtracker.model.AssignmentStatus requested;

        try {
            requested = acadtracker.model.AssignmentStatus
                    .valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid assignment status."
            );
        }

        /*
         * Students may only move one step:
         * Assigned -> Pending -> Completed.
         * The assignment must belong to this student.
         */
        String ownershipSql = """
                SELECT status
                FROM assignment_status
                WHERE assignment_id = ?
                  AND student_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             ownershipSql
                     )) {

            statement.setInt(
                    1,
                    assignmentId
            );

            statement.setInt(
                    2,
                    user.getUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {

                    throw new IllegalArgumentException(
                            "You cannot edit this assignment."
                    );
                }

                acadtracker.model.AssignmentStatus current =
                        acadtracker.model.AssignmentStatus
                                .fromDatabase(
                                        result.getString("status")
                                );

                acadtracker.model.AssignmentStatus next =
                        current.next();

                if (next == null || requested != next) {
                    String allowed = next == null
                            ? "This assignment is already completed."
                            : "Move this assignment from "
                                    + current.label()
                                    + " to "
                                    + next.label()
                                    + ".";

                    throw new IllegalArgumentException(allowed);
                }
            }
        }

        return studentAssignmentDAO.updateStatus(
                assignmentId,
                user.getUserId(),
                status
        );
    }

    // =========================================================
    // ASSIGNMENT ID FOR TABLE ROW
    // =========================================================

    public int getAssignmentIdForRow(
            int row)
            throws SQLException {

        List<String[]> assignments =
                getAssignments();

        if (row < 0 ||
                row >= assignments.size()) {

            throw new IllegalArgumentException(
                    "Invalid assignment selection."
            );
        }

        return Integer.parseInt(
                assignments.get(row)[0]
        );
    }

    // =========================================================
    // ATTENDANCE SUMMARY
    // =========================================================

    public List<String[]> getAttendanceSummary()
            throws SQLException {

        List<String[]> summary =
                new ArrayList<>();

        String sql = """
                SELECT
                    s.subject_name,

                    SUM(
                        CASE
                            WHEN a.status = 'PRESENT'
                            THEN 1
                            ELSE 0
                        END
                    ) AS present_count,

                    SUM(
                        CASE
                            WHEN a.status = 'ABSENT'
                            THEN 1
                            ELSE 0
                        END
                    ) AS absent_count,

                    SUM(
                        CASE
                            WHEN a.status = 'LEAVE'
                            THEN 1
                            ELSE 0
                        END
                    ) AS leave_count

                FROM enrollments e

                JOIN subjects s
                    ON e.subject_id =
                       s.subject_id

                LEFT JOIN attendance a
                    ON e.enrollment_id =
                       a.enrollment_id

                WHERE e.student_id = ?

                GROUP BY
                    e.enrollment_id,
                    s.subject_name

                ORDER BY
                    s.subject_name
                """;

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    user.getUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    int present =
                            result.getInt(
                                    "present_count"
                            );

                    int absent =
                            result.getInt(
                                    "absent_count"
                            );

                    int leave =
                            result.getInt(
                                    "leave_count"
                            );

                    int total =
                            present + absent;

                    double percentage =
                            total == 0
                                    ? 0.0
                                    : present * 100.0 / total;

                    summary.add(
                            new String[]{
                                    result.getString(
                                            "subject_name"
                                    ),
                                    String.valueOf(
                                            present
                                    ),
                                    String.valueOf(
                                            absent
                                    ),
                                    String.valueOf(
                                            leave
                                    ),
                                    String.format(
                                            "%.2f%%",
                                            percentage
                                    )
                            }
                    );
                }
            }
        }

        return summary;
    }

    public AuthenticatedUser getUser() {
        return user;
    }
}