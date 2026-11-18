package acadtracker.service;

import acadtracker.database.DBConnection;
import acadtracker.model.AuthenticatedUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class FacultyDashboardService {

    private final AuthenticatedUser faculty;

    public FacultyDashboardService(AuthenticatedUser faculty) {

        if (faculty == null ||
                !"FACULTY".equals(faculty.getRole())) {

            throw new IllegalArgumentException(
                    "A valid faculty account is required.");
        }

        this.faculty = faculty;
    }

    // GET FACULTY SUBJECTS

    public List<String[]> getSubjects() throws SQLException {

        List<String[]> subjects = new ArrayList<>();

        String sql = """
                SELECT DISTINCT
                    s.subject_id,
                    s.subject_name,
                    s.subject_code
                FROM subjects s
                WHERE s.faculty_id = ?
                   OR EXISTS (
                        SELECT 1
                        FROM faculty_subjects fs
                        WHERE fs.subject_id = s.subject_id
                          AND fs.faculty_id = ?
                   )
                ORDER BY s.subject_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, faculty.getUserId());
            statement.setInt(2, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    subjects.add(new String[]{
                            String.valueOf(
                                    result.getInt("subject_id")),
                            result.getString("subject_name"),
                            result.getString("subject_code")
                    });
                }
            }
        }

        return subjects;
    }

    // GET STUDENTS

    public List<String[]> getStudents(int subjectId)
            throws SQLException {

        List<String[]> students = new ArrayList<>();

        String sql = """
                SELECT
                    u.user_id,
                    u.name,
                    u.email,
                    e.enrollment_id
                FROM enrollments e
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
                ORDER BY u.name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    students.add(new String[]{
                            String.valueOf(
                                    result.getInt("user_id")),
                            result.getString("name"),
                            result.getString("email"),
                            String.valueOf(
                                    result.getInt("enrollment_id"))
                    });
                }
            }
        }

        return students;
    }

    // GET ASSIGNMENTS INCLUDING DESCRIPTION

    public List<String[]> getAssignments(int subjectId)
            throws SQLException {

        List<String[]> assignments = new ArrayList<>();

        String sql = """
                SELECT
                    a.assignment_id,
                    a.title,
                    a.description,
                    a.assigned_date,
                    a.due_date
                FROM assignments a
                JOIN subjects s
                    ON a.subject_id = s.subject_id
                WHERE a.subject_id = ?
                  AND (
                        s.faculty_id = ?
                        OR EXISTS (
                            SELECT 1
                            FROM faculty_subjects fs
                            WHERE fs.subject_id = s.subject_id
                              AND fs.faculty_id = ?
                        )
                  )
                ORDER BY a.due_date
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    assignments.add(new String[]{
                            String.valueOf(
                                    result.getInt("assignment_id")),
                            result.getString("title"),
                            result.getString("description"),
                            result.getDate("assigned_date").toString(),
                            result.getDate("due_date").toString()
                    });
                }
            }
        }

        return assignments;
    }

    // CHECK SUBJECT OWNERSHIP

    public boolean ownsSubject(int subjectId)
            throws SQLException {

        String sql = """
                SELECT s.subject_id
                FROM subjects s
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
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setInt(2, faculty.getUserId());
            statement.setInt(3, faculty.getUserId());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}