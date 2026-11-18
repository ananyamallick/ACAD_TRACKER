
package acadtracker.model;

public class Faculty extends User {

    public Faculty(int userId, String name, String email) {
        super(userId, name, email, "FACULTY");
    }

    @Override
    public void displayDashboard() {
        System.out.println("================================");
        System.out.println("        FACULTY DASHBOARD");
        System.out.println("================================");
        System.out.println("Faculty ID : " + getUserId());
        System.out.println("Name       : " + getName());
        System.out.println("Email      : " + getEmail());
        System.out.println("Role       : " + getRole());
        System.out.println("Features:");
        System.out.println("1. Manage Subjects");
        System.out.println("2. Mark Attendance");
        System.out.println("3. Create Assignments");
        System.out.println("4. View Student Reports");
        System.out.println("================================");
    }
}
