package acadtracker.service;

import acadtracker.dao.AttendanceDAO;
import acadtracker.dao.AttendanceDAOImpl;
import acadtracker.dao.StudentAssignmentDAO;
import acadtracker.dao.StudentAssignmentDAOImpl;
import acadtracker.database.DBConnection;
import acadtracker.exceptions.InvalidAttendanceException;
import acadtracker.model.AssignmentStatus;
import acadtracker.model.Attendance;
import acadtracker.model.AttendanceStatus;
import acadtracker.model.AuthenticatedUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Faculty edits for attendance and assignment records.
 * Every change is limited to a subject this faculty member teaches.
 */
public class FacultyRecordService {

    private final AuthenticatedUser faculty;
    private final FacultyDashboardService dashboardService;
    private final AttendanceDAO attendanceDAO;
    private final StudentAssignmentDAO studentAssignmentDAO;

    public FacultyRecordService(AuthenticatedUser faculty) {
        this.faculty = faculty;
        this.dashboardService = new FacultyDashboardService(faculty);
        this.attendanceDAO = new AttendanceDAOImpl();
        this.studentAssignmentDAO = new StudentAssignmentDAOImpl();
    }

    /**
     * Rows: attendance id, student, date, status.
     */
    public List<String[]> getAttendanceRecords(int subjectId)
            throws SQLException {

        requireOwnedSubject(subjectId);

        List<String[]> records = new ArrayList<>();

        String sql = """
                SELECT
                    a.attendance_id,
                    u.name,
                    a.attendance_date,
                    a.status
                FROM attendance a
                JOIN enrollments e
                    ON a.enrollment_id = e.enrollment_id
                JOIN users u
                    ON e.student_id = u.user_id
                JOIN subjects s
                    ON e.subject_id = s.subject_id
                WHERE s.subject_id = ?
                  AND (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                  )
                ORDER BY a.attendance_date DESC, u.name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    records.add(new String[]{
                            String.valueOf(result.getInt("attendance_id")),
                            result.getString("name"),
                            result.getDate("attendance_date").toString(),
                            result.getString("status")
                    });
                }
            }
        }

        return records;
    }

    /**
     * Rows: student id, student, assignment id, title, status, grade.
     */
    public List<String[]> getAssignmentRecords(int subjectId)
            throws SQLException {

        requireOwnedSubject(subjectId);

        List<String[]> records = new ArrayList<>();

        String sql = """
                SELECT
                    u.user_id,
                    u.name,
                    a.assignment_id,
                    a.title,
                    ast.status,
                    ast.grade
                FROM assignment_status ast
                JOIN assignments a
                    ON ast.assignment_id = a.assignment_id
                JOIN users u
                    ON ast.student_id = u.user_id
                JOIN subjects s
                    ON a.subject_id = s.subject_id
                WHERE s.subject_id = ?
                  AND (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                  )
                ORDER BY u.name, a.due_date
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String grade = result.getString("grade");
                    records.add(new String[]{
                            String.valueOf(result.getInt("user_id")),
                            result.getString("name"),
                            String.valueOf(result.getInt("assignment_id")),
                            result.getString("title"),
                            result.getString("status"),
                            grade == null ? "" : grade
                    });
                }
            }
        }

        return records;
    }

    public boolean updateAttendance(int attendanceId, AttendanceStatus status)
            throws SQLException, InvalidAttendanceException {

        if (attendanceId <= 0 || status == null) {
            throw new InvalidAttendanceException(
                    "Choose an attendance record and a status."
            );
        }

        if (!attendanceBelongsToFaculty(attendanceId)) {
            throw new InvalidAttendanceException(
                    "You can edit attendance only for a subject you teach."
            );
        }

        return attendanceDAO.updateAttendance(new Attendance(
                attendanceId,
                0,
                java.time.LocalDate.now(),
                status
        ));
    }

    /**
     * Faculty may set any assignment status and an optional grade.
     * Students cannot use this path.
     */
    public boolean updateAssignmentRecord(
            int assignmentId,
            int studentId,
            AssignmentStatus status,
            String grade
    ) throws SQLException {

        if (assignmentId <= 0 || studentId <= 0 || status == null) {
            throw new IllegalArgumentException(
                    "Choose a student assignment and a status."
            );
        }

        String cleanGrade = cleanGrade(grade);

        if (!assignmentBelongsToFaculty(assignmentId, studentId)) {
            throw new IllegalArgumentException(
                    "You can edit assignments only for a subject you teach."
            );
        }

        return studentAssignmentDAO.updateRecord(
                assignmentId,
                studentId,
                status.name(),
                cleanGrade
        );
    }

    private void requireOwnedSubject(int subjectId) throws SQLException {
        if (!dashboardService.ownsSubject(subjectId)) {
            throw new IllegalArgumentException(
                    "You can view records only for a subject you teach."
            );
        }
    }

    private boolean attendanceBelongsToFaculty(int attendanceId)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM attendance a
                JOIN enrollments e
                    ON a.enrollment_id = e.enrollment_id
                JOIN subjects s
                    ON e.subject_id = s.subject_id
                WHERE a.attendance_id = ?
                  AND (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                  )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, attendanceId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private boolean assignmentBelongsToFaculty(
            int assignmentId,
            int studentId
    ) throws SQLException {

        String sql = """
                SELECT 1
                FROM assignment_status ast
                JOIN assignments a
                    ON ast.assignment_id = a.assignment_id
                JOIN subjects s
                    ON a.subject_id = s.subject_id
                WHERE ast.assignment_id = ?
                  AND ast.student_id = ?
                  AND (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                  )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, assignmentId);
            statement.setInt(2, studentId);
            statement.setInt(3, faculty.getUserId());
            statement.setInt(4, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private String cleanGrade(String grade) {
        if (grade == null || grade.isBlank()) {
            return null;
        }

        String clean = grade.trim();

        if (!clean.matches("[A-Za-z0-9+\\-.]{1,8}")) {
            throw new IllegalArgumentException(
                    "Enter a grade such as A, B+, or 85."
            );
        }

        return clean;
    }
}
