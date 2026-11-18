
package acadtracker.service;

import acadtracker.dao.AttendanceDAO;
import acadtracker.dao.AttendanceDAOImpl;
import acadtracker.exceptions.InvalidAttendanceException;
import acadtracker.model.Attendance;
import acadtracker.model.AuthenticatedUser;

import java.sql.SQLException;

public class FacultyAttendanceService {

    private final AuthenticatedUser faculty;
    private final FacultyDashboardService dashboardService;
    private final AttendanceDAO attendanceDAO;

    public FacultyAttendanceService(AuthenticatedUser faculty) {

        this.faculty = faculty;
        this.dashboardService = new FacultyDashboardService(faculty);

        this.attendanceDAO = new AttendanceDAOImpl();
    }

    public boolean markAttendance(Attendance attendance)
            throws SQLException, InvalidAttendanceException {

        if (attendance == null) {
            throw new InvalidAttendanceException(
                    "Attendance information is required."
            );
        }

        if (attendance.getEnrollmentId() <= 0) {
            throw new InvalidAttendanceException(
                    "Invalid enrollment ID."
            );
        }

        if (attendance.getAttendanceDate() == null ||
                attendance.getAttendanceDate().isAfter(
                        java.time.LocalDate.now()
                )) {

            throw new InvalidAttendanceException(
                    "Attendance date cannot be in the future."
            );
        }

        if (attendance.getStatus() == null) {
            throw new InvalidAttendanceException(
                    "Attendance status is required."
            );
        }

        String sql = """
                SELECT s.subject_id
                FROM enrollments e
                JOIN subjects s
                    ON e.subject_id = s.subject_id
                WHERE e.enrollment_id = ?
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

        try (java.sql.Connection connection =
                     acadtracker.database.DBConnection.getConnection();
             java.sql.PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, attendance.getEnrollmentId());
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (java.sql.ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {
                    throw new InvalidAttendanceException(
                            "You cannot mark attendance for this enrollment."
                    );
                }
            }
        }

        return attendanceDAO.markAttendance(attendance);
    }


    /** Returns attendance history only when this faculty member owns the enrollment's subject. */
    public java.util.List<Attendance> getAttendanceHistory(int enrollmentId)
            throws SQLException, InvalidAttendanceException {
        ensureFacultyOwnsEnrollment(enrollmentId);
        return attendanceDAO.findByEnrollmentId(enrollmentId);
    }

    /** Updates a record after checking that its enrollment belongs to this faculty member. */
    public boolean updateAttendance(Attendance attendance)
            throws SQLException, InvalidAttendanceException {
        if (attendance == null || attendance.getAttendanceId() <= 0
                || attendance.getEnrollmentId() <= 0
                || attendance.getStatus() == null
                || attendance.getAttendanceDate() == null) {
            throw new InvalidAttendanceException("Valid attendance details are required.");
        }
        if (attendance.getAttendanceDate().isAfter(java.time.LocalDate.now())) {
            throw new InvalidAttendanceException("Attendance date cannot be in the future.");
        }
        ensureFacultyOwnsEnrollment(attendance.getEnrollmentId());
        return attendanceDAO.updateAttendance(attendance);
    }

    private void ensureFacultyOwnsEnrollment(int enrollmentId)
            throws SQLException, InvalidAttendanceException {
        if (enrollmentId <= 0) {
            throw new InvalidAttendanceException("Invalid enrollment ID.");
        }
        String sql = """
                SELECT 1
                FROM enrollments e
                JOIN subjects s ON s.subject_id = e.subject_id
                WHERE e.enrollment_id = ?
                  AND (s.faculty_id = ? OR EXISTS (
                      SELECT 1 FROM faculty_subjects fs
                      WHERE fs.subject_id = s.subject_id AND fs.faculty_id = ?
                  ))
                """;
        try (java.sql.Connection connection = acadtracker.database.DBConnection.getConnection();
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());
            try (java.sql.ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new InvalidAttendanceException(
                            "You are not authorized to edit attendance for this enrollment.");
                }
            }
        }
    }
}
