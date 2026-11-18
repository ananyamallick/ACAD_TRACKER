
package acadtracker.model;

import java.time.LocalDate;

public class Attendance {

    private int attendanceId;
    private int enrollmentId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;

    public Attendance(
            int attendanceId,
            int enrollmentId,
            LocalDate attendanceDate,
            AttendanceStatus status) {

        this.attendanceId = attendanceId;
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}
