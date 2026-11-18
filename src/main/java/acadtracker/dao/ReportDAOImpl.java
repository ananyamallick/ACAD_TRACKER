
package acadtracker.dao;

import acadtracker.database.DBConnection;
import acadtracker.model.ReportRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ReportDAOImpl implements ReportDAO {

    @Override
    public List<ReportRow> getAttendanceReport(int facultyId)
            throws SQLException {

        List<ReportRow> reports = new ArrayList<>();

        String sql = """
                SELECT
                    u.name AS student_name,
                    s.subject_name,
                    SUM(CASE WHEN a.status = 'PRESENT'
                        THEN 1 ELSE 0 END) AS present_count,
                    SUM(CASE WHEN a.status = 'ABSENT'
                        THEN 1 ELSE 0 END) AS absent_count,
                    SUM(CASE WHEN a.status = 'LEAVE'
                        THEN 1 ELSE 0 END) AS leave_count
                FROM enrollments e
                JOIN users u
                    ON e.student_id = u.user_id
                JOIN subjects s
                    ON e.subject_id = s.subject_id
                LEFT JOIN attendance a
                    ON e.enrollment_id = a.enrollment_id
                WHERE (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                )
                GROUP BY e.enrollment_id, u.name, s.subject_name
                ORDER BY s.subject_name, u.name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, facultyId);
            statement.setInt(2, facultyId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int present = result.getInt("present_count");
                    int absent = result.getInt("absent_count");
                    int leave = result.getInt("leave_count");

                    int total = present + absent;

                    double percentage = total == 0
                            ? 0.0
                            : present * 100.0 / total;

                    reports.add(new ReportRow(
                            result.getString("student_name"),
                            result.getString("subject_name"),
                            present,
                            absent,
                            leave,
                            percentage
                    ));
                }
            }
        }

        return reports;
    }

    @Override
    public List<ReportRow> getSubjectAttendanceReport(
            int facultyId,
            int subjectId
    ) throws SQLException {

        List<ReportRow> reports = new ArrayList<>();

        String sql = """
                SELECT
                    u.name AS student_name,
                    s.subject_name,
                    SUM(CASE WHEN a.status = 'PRESENT'
                        THEN 1 ELSE 0 END) AS present_count,
                    SUM(CASE WHEN a.status = 'ABSENT'
                        THEN 1 ELSE 0 END) AS absent_count,
                    SUM(CASE WHEN a.status = 'LEAVE'
                        THEN 1 ELSE 0 END) AS leave_count
                FROM enrollments e
                JOIN users u
                    ON e.student_id = u.user_id
                JOIN subjects s
                    ON e.subject_id = s.subject_id
                LEFT JOIN attendance a
                    ON e.enrollment_id = a.enrollment_id
                WHERE (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                )
                  AND s.subject_id = ?
                GROUP BY e.enrollment_id, u.name, s.subject_name
                ORDER BY u.name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, facultyId);
            statement.setInt(2, facultyId);
            statement.setInt(3, subjectId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int present = result.getInt("present_count");
                    int absent = result.getInt("absent_count");
                    int leave = result.getInt("leave_count");

                    int total = present + absent;

                    double percentage = total == 0
                            ? 0.0
                            : present * 100.0 / total;

                    reports.add(new ReportRow(
                            result.getString("student_name"),
                            result.getString("subject_name"),
                            present,
                            absent,
                            leave,
                            percentage
                    ));
                }
            }
        }

        return reports;
    }

    @Override
    public List<String[]> getAssignmentReport(int facultyId)
            throws SQLException {

        List<String[]> reports = new ArrayList<>();

        String sql = """
                SELECT
                    s.subject_name,
                    a.title,
                    COUNT(ast.status_id) AS total_students,
                    SUM(CASE WHEN ast.status = 'ASSIGNED'
                        THEN 1 ELSE 0 END) AS assigned_count,
                    SUM(CASE WHEN ast.status = 'PENDING'
                        THEN 1 ELSE 0 END) AS pending_count,
                    SUM(CASE WHEN ast.status = 'COMPLETED'
                        THEN 1 ELSE 0 END) AS completed_count
                FROM assignments a
                JOIN subjects s
                    ON a.subject_id = s.subject_id
                LEFT JOIN assignment_status ast
                    ON a.assignment_id = ast.assignment_id
                WHERE (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                )
                GROUP BY a.assignment_id, s.subject_name, a.title, a.due_date
                ORDER BY s.subject_name, a.due_date
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, facultyId);
            statement.setInt(2, facultyId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    reports.add(new String[]{
                            result.getString("subject_name"),
                            result.getString("title"),
                            String.valueOf(
                                    result.getInt("total_students")
                            ),
                            String.valueOf(
                                    result.getInt("assigned_count")
                            ),
                            String.valueOf(
                                    result.getInt("pending_count")
                            ),
                            String.valueOf(
                                    result.getInt("completed_count")
                            )
                    });
                }
            }
        }

        return reports;
    }
}
