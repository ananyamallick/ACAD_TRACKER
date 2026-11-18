
package acadtracker.main;

import acadtracker.model.Assignment;
import acadtracker.model.AssignmentStatus;
import acadtracker.model.StudentAssignment;
import acadtracker.service.AssignmentService;

import java.time.LocalDate;
import java.util.List;

public class AssignmentTest {

    public static void main(String[] args) {

        AssignmentService service = new AssignmentService();

        int subjectId = 1;
        int studentId = 1;

        String testTitle =
            "Phase 8B Test " + System.currentTimeMillis();

        int assignmentId = -1;

        try {

            // 1. CREATE ASSIGNMENT
            System.out.println("======= CREATE ASSIGNMENT =======");

            Assignment assignment = new Assignment(
                0,
                subjectId,
                testTitle,
                "Testing assignment management using Java",
                LocalDate.now(),
                LocalDate.now().plusDays(7)
            );

            boolean created =
                service.createAssignment(assignment);

            System.out.println(
                created
                    ? "Assignment created successfully."
                    : "Assignment creation failed."
            );

            // 2. FIND THE NEW ASSIGNMENT
            System.out.println("\n======= RETRIEVE ASSIGNMENT =======");

            List<Assignment> assignments =
                service.getAssignmentsBySubject(subjectId);

            for (Assignment item : assignments) {

                if (item.getTitle().equals(testTitle)) {

                    assignmentId = item.getAssignmentId();

                    item.displayAssignment();
                    break;
                }
            }

            if (assignmentId == -1) {
                throw new IllegalStateException(
                    "Created assignment could not be found."
                );
            }

            // 3. UPDATE STUDENT STATUS
            System.out.println("\n======= UPDATE STATUS =======");

            boolean updated = service.updateStudentStatus(
                assignmentId,
                studentId,
                AssignmentStatus.COMPLETED
            );

            System.out.println(
                updated
                    ? "Assignment marked as COMPLETED."
                    : "Student assignment record not found."
            );

            // 4. DISPLAY STUDENT REPORT
            System.out.println("\n======= ASSIGNMENT REPORT =======");

            List<StudentAssignment> report =
                service.getStudentAssignmentReport(studentId);

            for (StudentAssignment item : report) {

                System.out.println(
                    "Assignment ID: " + item.getAssignmentId()
                );

                System.out.println(
                    "Status: " + item.getStatus()
                );

                System.out.println(
                    "Submission Date: " + item.getSubmissionDate()
                );

                System.out.println("---------------------------");
            }

        } catch (Exception e) {

            System.out.println(
                "Assignment operation failed: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
