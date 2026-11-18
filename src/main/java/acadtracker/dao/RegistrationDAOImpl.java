package acadtracker.dao;

import acadtracker.database.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAOImpl implements RegistrationDAO {

    @Override
    public List<String[]> findSubjects() throws SQLException {

        List<String[]> subjects = new ArrayList<>();

        String sql = """
                SELECT
                    s.subject_id,
                    s.subject_name,
                    s.subject_code,
                    u.name AS faculty_name
                FROM subjects s
                JOIN users u
                    ON s.faculty_id = u.user_id
                ORDER BY s.subject_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                subjects.add(new String[]{
                        String.valueOf(result.getInt("subject_id")),
                        result.getString("subject_name"),
                        result.getString("subject_code"),
                        result.getString("faculty_name")
                });
            }
        }

        return subjects;
    }

    @Override
    public int register(
            String name,
            String email,
            String passwordHash,
            String role,
            List<Integer> subjectIds,
            List<String[]> newSubjects
    ) throws SQLException {

        String insertUser = """
                INSERT INTO users (name, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int userId = insertUser(
                        connection,
                        insertUser,
                        name,
                        email,
                        passwordHash,
                        role
                );

                if ("FACULTY".equals(role)) {
                    linkFacultySubjects(connection, userId, subjectIds);
                    createFacultySubjects(connection, userId, newSubjects);
                } else {
                    enrollStudent(connection, userId, subjectIds);
                }

                connection.commit();
                return userId;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private int insertUser(
            Connection connection,
            String sql,
            String name,
            String email,
            String passwordHash,
            String role
    ) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setString(4, role);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Could not create the account.");
                }
                return keys.getInt(1);
            }
        }
    }

    /**
     * Links the faculty member to existing courses without replacing
     * the concerned faculty already stored on the subject.
     */
    private void linkFacultySubjects(
            Connection connection,
            int facultyId,
            List<Integer> subjectIds
    ) throws SQLException {

        String sql = """
                INSERT IGNORE INTO faculty_subjects (faculty_id, subject_id)
                SELECT ?, subject_id
                FROM subjects
                WHERE subject_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int subjectId : subjectIds) {
                statement.setInt(1, facultyId);
                statement.setInt(2, subjectId);
                if (statement.executeUpdate() != 1) {
                    throw new SQLException(
                            "One of the selected subjects is no longer available."
                    );
                }
            }
        }
    }

    /**
     * Creates a course this faculty member is responsible for,
     * then links them to it.
     */
    private void createFacultySubjects(
            Connection connection,
            int facultyId,
            List<String[]> newSubjects
    ) throws SQLException {

        String insertSubject = """
                INSERT INTO subjects (subject_name, subject_code, faculty_id)
                VALUES (?, ?, ?)
                """;

        String linkSubject = """
                INSERT INTO faculty_subjects (faculty_id, subject_id)
                VALUES (?, ?)
                """;

        for (String[] subject : newSubjects) {
            int subjectId;

            try (PreparedStatement statement = connection.prepareStatement(
                    insertSubject,
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, subject[0]);
                statement.setString(2, subject[1]);
                statement.setInt(3, facultyId);
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Could not create the subject.");
                    }
                    subjectId = keys.getInt(1);
                }
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(linkSubject)) {
                statement.setInt(1, facultyId);
                statement.setInt(2, subjectId);
                statement.executeUpdate();
            }
        }
    }

    /**
     * Enrolls the student and opens each existing assignment
     * at the Assigned status. The concerned faculty stays on the subject.
     */
    private void enrollStudent(
            Connection connection,
            int studentId,
            List<Integer> subjectIds
    ) throws SQLException {

        String enroll = """
                INSERT INTO enrollments (student_id, subject_id, enrollment_date)
                VALUES (?, ?, ?)
                """;

        String openAssignments = """
                INSERT INTO assignment_status (assignment_id, student_id, status)
                SELECT assignment_id, ?, 'ASSIGNED'
                FROM assignments
                WHERE subject_id = ?
                """;

        Date today = Date.valueOf(LocalDate.now());

        try (PreparedStatement enrollment = connection.prepareStatement(enroll);
             PreparedStatement assignments =
                     connection.prepareStatement(openAssignments)) {

            for (int subjectId : subjectIds) {
                if (!subjectExists(connection, subjectId)) {
                    throw new SQLException(
                            "One of the selected subjects is no longer available."
                    );
                }

                enrollment.setInt(1, studentId);
                enrollment.setInt(2, subjectId);
                enrollment.setDate(3, today);
                enrollment.executeUpdate();

                assignments.setInt(1, studentId);
                assignments.setInt(2, subjectId);
                assignments.executeUpdate();
            }
        }
    }

    private boolean subjectExists(Connection connection, int subjectId)
            throws SQLException {

        String sql = "SELECT 1 FROM subjects WHERE subject_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, subjectId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
