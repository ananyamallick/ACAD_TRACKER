
package acadtracker.model;

import acadtracker.interfaces.Trackable;

public class Student extends User implements Trackable {

    public Student(int userId, String name, String email) {
        super(userId, name, email, "STUDENT");
    }

    @Override
    public void displayDashboard() {
        System.out.println("STUDENT DASHBOARD");
        System.out.println("Welcome, " + getName());
    }

    @Override
    public void trackAttendance() {
        System.out.println("Displaying student's attendance...");
    }

    @Override
    public void trackAssignments() {
        System.out.println("Displaying student's assignments...");
    }
}
