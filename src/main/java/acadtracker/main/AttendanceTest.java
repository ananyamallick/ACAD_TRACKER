
package acadtracker.main;

import acadtracker.model.Attendance;
import acadtracker.model.AttendanceStatus;
import acadtracker.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;

public class AttendanceTest {

    public static void main(String[] args) {

        AttendanceService service = new AttendanceService();

        // Use enrollment ID 1 from the sample database.
        int enrollmentId = 1;

        try {

            // Retrieve existing attendance
            List<Attendance> records =
                service.getAttendance(enrollmentId);

            System.out.println("======= EXISTING ATTENDANCE =======");

            for (Attendance record : records) {
                System.out.println(
                    record.getAttendanceDate() + " | " +
                    record.getStatus()
                );
            }

            // Find a date without an existing attendance record.
            LocalDate testDate = LocalDate.now();

            while (true) {

                boolean dateExists = false;

                for (Attendance record : records) {
                    if (record.getAttendanceDate().equals(testDate)) {
                        dateExists = true;
                        break;
                    }
                }

                if (!dateExists) {
                    break;
                }

                testDate = testDate.minusDays(1);
            }

            // Mark attendance
            System.out.println("\n======= MARK ATTENDANCE =======");

            Attendance attendance = new Attendance(
                0,
                enrollmentId,
                testDate,
                AttendanceStatus.PRESENT
            );

            boolean marked = service.markAttendance(attendance);

            System.out.println(
                marked ? "Attendance marked successfully."
                       : "Attendance marking failed."
            );

            // Calculate percentage
            System.out.println("\n======= ATTENDANCE REPORT =======");

            double percentage =
                service.getAttendancePercentage(enrollmentId);

            System.out.printf(
                "Attendance Percentage: %.2f%%%n",
                percentage
            );

        } catch (Exception e) {

            System.out.println(
                "Attendance operation failed: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
