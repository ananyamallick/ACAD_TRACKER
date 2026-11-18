
package acadtracker.dao;

import acadtracker.database.DBConnection;
import acadtracker.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public List<Student> findAll() throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT user_id, name, email FROM users " +
                     "WHERE role = 'STUDENT' ORDER BY user_id";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        }

        return students;
    }

    @Override
    public Student findById(int studentId) throws SQLException {

        String sql = "SELECT user_id, name, email FROM users " +
                     "WHERE user_id = ? AND role = 'STUDENT'";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public boolean insert(Student student) throws SQLException {

        String sql = "INSERT INTO users " +
                     "(name, email, password_hash, role) " +
                     "VALUES (?, ?, 'PENDING_SETUP', 'STUDENT')";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean update(Student student) throws SQLException {

        String sql = "UPDATE users SET name = ?, email = ? " +
                     "WHERE user_id = ? AND role = 'STUDENT'";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getUserId());

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int studentId) throws SQLException {

        String sql = "DELETE FROM users " +
                     "WHERE user_id = ? AND role = 'STUDENT'";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean emailExists(String email) throws SQLException {

        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
