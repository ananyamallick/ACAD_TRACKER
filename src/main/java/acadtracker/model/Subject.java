
package acadtracker.model;

public class Subject {

    private int subjectId;
    private String subjectName;
    private String subjectCode;
    private int facultyId;

    public Subject(int subjectId, String subjectName,
                   String subjectCode, int facultyId) {

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.facultyId = facultyId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void displaySubject() {
        System.out.println("Subject ID   : " + subjectId);
        System.out.println("Subject Name : " + subjectName);
        System.out.println("Subject Code : " + subjectCode);
        System.out.println("Faculty ID   : " + facultyId);
    }
}
