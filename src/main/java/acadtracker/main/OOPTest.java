
package acadtracker.main;

import acadtracker.model.Student;
import acadtracker.model.Faculty;
import acadtracker.model.User;
import acadtracker.model.Subject;
import acadtracker.interfaces.Trackable;

public class OOPTest {

    public static void main(String[] args) {

        // Create Student object
        Student student = new Student(
            1,
            "Arun Kumar",
            "arun@gmail.com"
        );

        // Create Faculty object
        Faculty faculty = new Faculty(
            2,
            "Priya Sharma",
            "priya@gmail.com"
        );

        // Polymorphism
        User user1 = student;
        User user2 = faculty;

        user1.displayDashboard();
        user2.displayDashboard();

        // Interface
        Trackable tracker = student;

        tracker.trackAttendance();
        tracker.trackAssignments();

        // Subject object
        Subject subject = new Subject(
            1,
            "Java Programming",
            "JAVA101",
            2
        );

        System.out.println("\nSUBJECT DETAILS");
        subject.displaySubject();
    }
}
