
package acadtracker.model;

public class ReportRow {

    private final String studentName;
    private final String subjectName;
    private final int presentCount;
    private final int absentCount;
    private final int leaveCount;
    private final double attendancePercentage;

    public ReportRow(
            String studentName,
            String subjectName,
            int presentCount,
            int absentCount,
            int leaveCount,
            double attendancePercentage
    ) {
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.leaveCount = leaveCount;
        this.attendancePercentage = attendancePercentage;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public int getLeaveCount() {
        return leaveCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }
}
