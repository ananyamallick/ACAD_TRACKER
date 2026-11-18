
package acadtracker.dao;

import acadtracker.database.DBConnection;
import acadtracker.model.AssignmentStatus;
import acadtracker.model.StudentAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentDAOImpl
        implements StudentAssignmentDAO {

    @Override
    public boolean updateStatus(
            int assignmentId,
            int studentId,
            String status) throws SQLException {

        String sql =
            "UPDATE assignment_status " +
            "SET status = ?, submission_date = ? " +
            "WHERE assignment_id = ? AND student_id = ?";

        AssignmentStatus assignmentStatus =
            AssignmentStatus.fromDatabase(status);

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, assignmentStatus.name());

            if (assignmentStatus == AssignmentStatus.COMPLETED) {
                ps.setDate(
                    2,
                    java.sql.Date.valueOf(
                        java.time.LocalDate.now()
                    )
                );
            } else {
                ps.setNull(
                    2,
                    java.sql.Types.DATE
                );
            }

            ps.setInt(3, assignmentId);
            ps.setInt(4, studentId);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updateRecord(
            int assignmentId,
            int studentId,
            String status,
            String grade) throws SQLException {

        AssignmentStatus assignmentStatus =
            AssignmentStatus.fromDatabase(status);

        String sql =
            "UPDATE assignment_status " +
            "SET status = ?, grade = ?, " +
            "submission_date = IF(? = 'COMPLETED', " +
            "COALESCE(submission_date, CURRENT_DATE), submission_date) " +
            "WHERE assignment_id = ? AND student_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, assignmentStatus.name());

            if (grade == null || grade.isBlank()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, grade);
            }

            ps.setString(3, assignmentStatus.name());
            ps.setInt(4, assignmentId);
            ps.setInt(5, studentId);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<StudentAssignment> findByStudent(int studentId)
            throws SQLException {

        List<StudentAssignment> records = new ArrayList<>();

        String sql =
            "SELECT status_id, assignment_id, student_id, " +
            "status, submission_date " +
            "FROM assignment_status " +
            "WHERE student_id = ? ORDER BY assignment_id";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    java.sql.Date sqlDate =
                        rs.getDate("submission_date");

                    records.add(new StudentAssignment(
                        rs.getInt("status_id"),
                        rs.getInt("assignment_id"),
                        rs.getInt("student_id"),
                        AssignmentStatus.fromDatabase(
                            rs.getString("status")
                        ),
                        sqlDate == null
                            ? null
                            : sqlDate.toLocalDate()
                    ));
                }
            }
        }

        return records;
    }
}
