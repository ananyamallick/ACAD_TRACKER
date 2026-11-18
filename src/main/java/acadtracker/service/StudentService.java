
package acadtracker.service;

import acadtracker.dao.StudentDAO;
import acadtracker.dao.StudentDAOImpl;
import acadtracker.exceptions.StudentNotFoundException;
import acadtracker.exceptions.DuplicateEmailException;
import acadtracker.model.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public Student getStudentById(int id)
            throws SQLException, StudentNotFoundException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Student ID must be positive."
            );
        }

        Student student = studentDAO.findById(id);

        if (student == null) {
            throw new StudentNotFoundException(
                "Student with ID " + id + " was not found."
            );
        }

        return student;
    }

    public boolean addStudent(Student student)
            throws SQLException, DuplicateEmailException {

        validateStudent(student);

        if (studentDAO.emailExists(student.getEmail())) {
            throw new DuplicateEmailException(
                "Email address already exists."
            );
        }

        return studentDAO.insert(student);
    }

    public boolean updateStudent(Student student)
            throws SQLException, StudentNotFoundException,
                   DuplicateEmailException {

        validateStudent(student);

        Student existing = studentDAO.findById(student.getUserId());

        if (existing == null) {
            throw new StudentNotFoundException(
                "Student does not exist."
            );
        }

        if (!existing.getEmail().equalsIgnoreCase(student.getEmail())
                && studentDAO.emailExists(student.getEmail())) {

            throw new DuplicateEmailException(
                "Email address already belongs to another user."
            );
        }

        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id)
            throws SQLException, StudentNotFoundException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Student ID must be positive."
            );
        }

        Student existing = studentDAO.findById(id);

        if (existing == null) {
            throw new StudentNotFoundException(
                "Student does not exist."
            );
        }

        return studentDAO.delete(id);
    }

    private void validateStudent(Student student) {

        if (student == null) {
            throw new IllegalArgumentException(
                "Student cannot be null."
            );
        }

        if (student.getName() == null ||
                student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Student name is required."
            );
        }

        if (student.getEmail() == null ||
                !student.getEmail().matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                "Enter a valid email address."
            );
        }
    }
}
