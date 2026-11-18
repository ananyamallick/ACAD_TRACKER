
package acadtracker.main;

import acadtracker.exceptions.StudentNotFoundException;
import acadtracker.exceptions.DuplicateEmailException;
import acadtracker.model.Student;
import acadtracker.service.StudentService;
import acadtracker.utils.AppLogger;
import acadtracker.utils.GlobalExceptionHandler;

public class ExceptionTest {

    public static void main(String[] args) {

        StudentService service = new StudentService();

        AppLogger.getLogger().info(
            "Exception handling test started."
        );

        // TEST 1: Student not found
        try {

            System.out.println("\nTEST 1: STUDENT NOT FOUND");

            service.getStudentById(99999);

        } catch (StudentNotFoundException e) {

            GlobalExceptionHandler.handle(e);

        } catch (Exception e) {

            GlobalExceptionHandler.handle(e);
        }

        // TEST 2: Invalid student ID
        try {

            System.out.println("\nTEST 2: INVALID STUDENT ID");

            service.getStudentById(-1);

        } catch (Exception e) {

            GlobalExceptionHandler.handle(e);
        }

        // TEST 3: Duplicate email
        try {

            System.out.println("\nTEST 3: DUPLICATE EMAIL");

            Student student = new Student(
                0,
                "Duplicate Test",
                "arun@gmail.com"
            );

            service.addStudent(student);

        } catch (DuplicateEmailException e) {

            GlobalExceptionHandler.handle(e);

        } catch (Exception e) {

            GlobalExceptionHandler.handle(e);
        }

        AppLogger.getLogger().info(
            "Exception handling test completed."
        );
    }
}
