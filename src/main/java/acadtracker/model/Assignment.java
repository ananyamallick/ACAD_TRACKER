package acadtracker.model;

import java.time.LocalDate;

public class Assignment {

    private int assignmentId;
    private int subjectId;
    private String title;
    private String description;
    private LocalDate assignedDate;
    private LocalDate dueDate;

    public Assignment(
            int assignmentId,
            int subjectId,
            String title,
            String description,
            LocalDate assignedDate,
            LocalDate dueDate) {

        this.assignmentId = assignmentId;
        this.subjectId = subjectId;
        this.title = title;
        this.description = description;
        this.assignedDate = assignedDate;
        this.dueDate = dueDate;
    }

    // GETTERS

    public int getAssignmentId() {
        return assignmentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // SETTERS

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // DISPLAY ASSIGNMENT

    public void displayAssignment() {

        System.out.println("Assignment ID : " + assignmentId);
        System.out.println("Subject ID    : " + subjectId);
        System.out.println("Title         : " + title);
        System.out.println("Description   : " + description);
        System.out.println("Assigned Date : " + assignedDate);
        System.out.println("Due Date      : " + dueDate);
    }
}