
package acadtracker.dao;

import acadtracker.model.Student;
import java.sql.SQLException;
import java.util.List;

public interface StudentDAO {

    // Read all students
    List<Student> findAll() throws SQLException;

    // Read one student
    Student findById(int studentId) throws SQLException;

    // Create student
    boolean insert(Student student) throws SQLException;

    // Update student
    boolean update(Student student) throws SQLException;

    // Delete student
    boolean delete(int studentId) throws SQLException;

    // Check whether email already exists
    boolean emailExists(String email) throws SQLException;
}
