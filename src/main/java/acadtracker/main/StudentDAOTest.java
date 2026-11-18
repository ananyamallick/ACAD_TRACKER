
package acadtracker.main;

import acadtracker.dao.StudentDAO;
import acadtracker.dao.StudentDAOImpl;
import acadtracker.model.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentDAOTest {

    public static void main(String[] args) {

        StudentDAO studentDAO = new StudentDAOImpl();

        try {

            // Retrieve all students
            System.out.println("======= ALL STUDENTS =======");

            List<Student> students = studentDAO.findAll();

            for (Student student : students) {
                System.out.println(
                    student.getUserId() + " | " +
                    student.getName() + " | " +
                    student.getEmail()
                );
            }

            // Retrieve one student by ID
            System.out.println("\n======= STUDENT BY ID =======");

            Student student = studentDAO.findById(1);

            if (student != null) {
                System.out.println("ID: " + student.getUserId());
                System.out.println("Name: " + student.getName());
                System.out.println("Email: " + student.getEmail());
            } else {
                System.out.println("Student not found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to access student records.");
            e.printStackTrace();
        }
    }
}
