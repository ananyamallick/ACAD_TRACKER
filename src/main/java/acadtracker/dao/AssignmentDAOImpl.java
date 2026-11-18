package acadtracker.dao;

import acadtracker.database.DBConnection;
import acadtracker.model.Assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAOImpl implements AssignmentDAO {

    @Override
    public boolean createAssignment(Assignment assignment)
            throws SQLException {

        String insertAssignment =
                "INSERT INTO assignments " +
                "(subject_id, title, description, assigned_date, due_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        String createStudentStatuses =
                "INSERT INTO assignment_status " +
                "(assignment_id, student_id, status) " +
                "SELECT ?, student_id, 'ASSIGNED' " +
                "FROM enrollments WHERE subject_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int generatedAssignmentId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertAssignment,
                        Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, assignment.getSubjectId());
                    ps.setString(2, assignment.getTitle());
                    ps.setString(3, assignment.getDescription());

                    ps.setDate(4,
                            java.sql.Date.valueOf(
                                    assignment.getAssignedDate()));

                    ps.setDate(5,
                            java.sql.Date.valueOf(
                                    assignment.getDueDate()));

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not obtain assignment ID.");
                        }

                        generatedAssignmentId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(createStudentStatuses)) {

                    ps.setInt(1, generatedAssignmentId);
                    ps.setInt(2, assignment.getSubjectId());

                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Assignment> findAll() throws SQLException {

        List<Assignment> assignments = new ArrayList<>();

        String sql =
                "SELECT assignment_id, subject_id, title, description, " +
                "assigned_date, due_date " +
                "FROM assignments ORDER BY due_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                assignments.add(new Assignment(
                        rs.getInt("assignment_id"),
                        rs.getInt("subject_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("assigned_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate()
                ));
            }
        }

        return assignments;
    }

    @Override
    public List<Assignment> findBySubject(int subjectId)
            throws SQLException {

        List<Assignment> assignments = new ArrayList<>();

        String sql =
                "SELECT assignment_id, subject_id, title, description, " +
                "assigned_date, due_date " +
                "FROM assignments WHERE subject_id = ? " +
                "ORDER BY due_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, subjectId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    assignments.add(new Assignment(
                            rs.getInt("assignment_id"),
                            rs.getInt("subject_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDate("assigned_date").toLocalDate(),
                            rs.getDate("due_date").toLocalDate()
                    ));
                }
            }
        }

        return assignments;
    }

    @Override
    public boolean assignmentExists(int assignmentId)
            throws SQLException {

        String sql =
                "SELECT 1 FROM assignments WHERE assignment_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // NEW: UPDATE ASSIGNMENT

    @Override
    public boolean updateAssignment(Assignment assignment)
            throws SQLException {

        if (assignment == null) {
            throw new IllegalArgumentException(
                    "Assignment cannot be null.");
        }

        if (assignment.getAssignmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid assignment ID.");
        }

        if (assignment.getTitle() == null ||
                assignment.getTitle().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Assignment title cannot be empty.");
        }

        if (assignment.getAssignedDate() == null ||
                assignment.getDueDate() == null) {

            throw new IllegalArgumentException(
                    "Assignment dates cannot be empty.");
        }

        if (assignment.getDueDate().isBefore(
                assignment.getAssignedDate())) {

            throw new IllegalArgumentException(
                    "Due date cannot be before assigned date.");
        }

        String sql =
                "UPDATE assignments SET " +
                "title = ?, description = ?, " +
                "assigned_date = ?, due_date = ? " +
                "WHERE assignment_id = ? AND subject_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, assignment.getTitle().trim());

            ps.setString(2, assignment.getDescription());

            ps.setDate(3,
                    java.sql.Date.valueOf(
                            assignment.getAssignedDate()));

            ps.setDate(4,
                    java.sql.Date.valueOf(
                            assignment.getDueDate()));

            ps.setInt(5, assignment.getAssignmentId());
            ps.setInt(6, assignment.getSubjectId());

            return ps.executeUpdate() > 0;
        }
    }
}