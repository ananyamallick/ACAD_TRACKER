
package acadtracker.service;

import acadtracker.dao.AttendanceDAO;
import acadtracker.dao.AttendanceDAOImpl;
import acadtracker.model.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAOImpl();
    }

    public boolean markAttendance(Attendance attendance)
            throws SQLException {

        if (attendance == null) {
            throw new IllegalArgumentException(
                "Attendance record cannot be null."
            );
        }

        if (attendance.getEnrollmentId() <= 0) {
            throw new IllegalArgumentException(
                "Invalid enrollment ID."
            );
        }

        if (attendance.getAttendanceDate() == null) {
            throw new IllegalArgumentException(
                "Attendance date is required."
            );
        }

        if (attendance.getAttendanceDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Attendance date cannot be in the future."
            );
        }

        if (attendance.getStatus() == null) {
            throw new IllegalArgumentException(
                "Attendance status is required."
            );
        }

        if (!attendanceDAO.enrollmentExists(
                attendance.getEnrollmentId())) {

            throw new IllegalArgumentException(
                "Enrollment does not exist."
            );
        }

        return attendanceDAO.markAttendance(attendance);
    }

    public List<Attendance> getAttendance(int enrollmentId)
            throws SQLException {

        if (enrollmentId <= 0) {
            throw new IllegalArgumentException(
                "Invalid enrollment ID."
            );
        }

        return attendanceDAO.findByEnrollmentId(enrollmentId);
    }

    public double getAttendancePercentage(int enrollmentId)
            throws SQLException {

        if (enrollmentId <= 0) {
            throw new IllegalArgumentException(
                "Invalid enrollment ID."
            );
        }

        return attendanceDAO.calculateAttendancePercentage(
            enrollmentId
        );
    }

    public boolean updateAttendance(Attendance attendance)
            throws SQLException {

        if (attendance == null) {
            throw new IllegalArgumentException(
                "Attendance record cannot be null."
            );
        }

        if (attendance.getAttendanceId() <= 0) {
            throw new IllegalArgumentException(
                "Invalid attendance ID."
            );
        }

        if (attendance.getEnrollmentId() <= 0) {
            throw new IllegalArgumentException(
                "Invalid enrollment ID."
            );
        }

        if (attendance.getAttendanceDate() == null) {
            throw new IllegalArgumentException(
                "Attendance date is required."
            );
        }

        if (attendance.getAttendanceDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Attendance date cannot be in the future."
            );
        }

        if (attendance.getStatus() == null) {
            throw new IllegalArgumentException(
                "Attendance status is required."
            );
        }

        if (!attendanceDAO.enrollmentExists(
                attendance.getEnrollmentId())) {

            throw new IllegalArgumentException(
                "Enrollment does not exist."
            );
        }

        return attendanceDAO.updateAttendance(attendance);
    }
}
