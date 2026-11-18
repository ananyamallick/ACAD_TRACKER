
package acadtracker.dao;

import acadtracker.database.DBConnection;
import acadtracker.model.Attendance;
import acadtracker.model.AttendanceStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public boolean enrollmentExists(int enrollmentId)
            throws SQLException {

        String sql =
            "SELECT 1 FROM enrollments WHERE enrollment_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean markAttendance(Attendance attendance)
            throws SQLException {

        String sql =
            "INSERT INTO attendance " +
            "(enrollment_id, attendance_date, status) " +
            "VALUES (?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, attendance.getEnrollmentId());

            ps.setDate(
                2,
                java.sql.Date.valueOf(attendance.getAttendanceDate())
            );

            ps.setString(3, attendance.getStatus().name());

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<Attendance> findByEnrollmentId(int enrollmentId)
            throws SQLException {

        List<Attendance> records = new ArrayList<>();

        String sql =
            "SELECT attendance_id, enrollment_id, " +
            "attendance_date, status " +
            "FROM attendance " +
            "WHERE enrollment_id = ? " +
            "ORDER BY attendance_date";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Attendance attendance = new Attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("enrollment_id"),
                        rs.getDate("attendance_date").toLocalDate(),
                        AttendanceStatus.valueOf(
                            rs.getString("status")
                        )
                    );

                    records.add(attendance);
                }
            }
        }

        return records;
    }

    @Override
    public double calculateAttendancePercentage(int enrollmentId)
            throws SQLException {

        String sql =
            "SELECT " +
            "SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) " +
            "AS present_count, " +
            "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) " +
            "AS absent_count " +
            "FROM attendance WHERE enrollment_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int present = rs.getInt("present_count");
                    int absent = rs.getInt("absent_count");

                    int total = present + absent;

                    if (total == 0) {
                        return 0.0;
                    }

                    return (present * 100.0) / total;
                }
            }
        }

        return 0.0;
    }

    @Override
public boolean updateAttendance(Attendance attendance)
        throws SQLException {

    if (attendance == null) {
        throw new IllegalArgumentException(
            "Attendance cannot be null."
        );
    }

    if (attendance.getAttendanceId() <= 0) {
        throw new IllegalArgumentException(
            "Invalid attendance ID."
        );
    }

    if (attendance.getStatus() == null) {
        throw new IllegalArgumentException(
            "Attendance status cannot be null."
        );
    }

    String sql =
        "UPDATE attendance SET status = ? " +
        "WHERE attendance_id = ? AND enrollment_id = ?";

    try (
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, attendance.getStatus().name());
        ps.setInt(2, attendance.getAttendanceId());
        ps.setInt(3, attendance.getEnrollmentId());

        return ps.executeUpdate() == 1;
    }
}
}
