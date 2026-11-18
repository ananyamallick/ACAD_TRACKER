
package acadtracker.main;

import acadtracker.model.Student;
import acadtracker.service.StudentService;

public class StudentServiceTest {

    public static void main(String[] args) {

        StudentService service = new StudentService();

        String testEmail = "phase7.test@example.com";
        int testStudentId = -1;

        try {
            // 1. CREATE
            System.out.println("1. ADDING STUDENT");

            Student newStudent = new Student(
                0,
                "Phase Seven Test",
                testEmail
            );

            boolean inserted = service.addStudent(newStudent);

            if (!inserted) {
                throw new IllegalStateException(
                    "Student was not inserted."
                );
            }

            System.out.println("Student inserted successfully.");

            // 2. READ
            System.out.println("\n2. RETRIEVING STUDENT");

            for (Student student : service.getAllStudents()) {
                if (student.getEmail().equals(testEmail)) {
                    testStudentId = student.getUserId();

                    System.out.println(
                        "Found: " + student.getName()
                    );
                }
            }

            if (testStudentId == -1) {
                throw new IllegalStateException(
                    "Inserted student could not be found."
                );
            }

            // 3. UPDATE
            System.out.println("\n3. UPDATING STUDENT");

            Student updatedStudent = new Student(
                testStudentId,
                "Updated Phase Seven Student",
                testEmail
            );

            boolean updated = service.updateStudent(updatedStudent);

            System.out.println(
                updated ? "Student updated successfully."
                        : "Student update failed."
            );

            // 4. DELETE
            System.out.println("\n4. DELETING STUDENT");

            boolean deleted = service.deleteStudent(testStudentId);

            System.out.println(
                deleted ? "Student deleted successfully."
                        : "Student deletion failed."
            );

        } catch (Exception e) {

            System.out.println("Test failed: " + e.getMessage());

            // Best-effort cleanup if insertion succeeded.
            if (testStudentId != -1) {
                try {
                    service.deleteStudent(testStudentId);
                    System.out.println("Cleanup attempted.");
                } catch (Exception cleanupError) {
                    System.out.println(
                        "Cleanup failed: " + cleanupError.getMessage()
                    );
                }
            }
        }
    }
}
