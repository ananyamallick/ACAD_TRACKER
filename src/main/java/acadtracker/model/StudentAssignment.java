
package acadtracker.model;

import java.time.LocalDate;

public class StudentAssignment {

    private int statusId;
    private int assignmentId;
    private int studentId;
    private AssignmentStatus status;
    private LocalDate submissionDate;

    public StudentAssignment(
            int statusId,
            int assignmentId,
            int studentId,
            AssignmentStatus status,
            LocalDate submissionDate) {

        this.statusId = statusId;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.status = status;
        this.submissionDate = submissionDate;
    }

    public int getStatusId() {
        return statusId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }
}
