package acadtracker.gui;

import acadtracker.model.ReportRow;
import acadtracker.service.ReportService;
import acadtracker.utils.CsvExporter;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import acadtracker.model.Assignment;
import acadtracker.model.Attendance;
import acadtracker.model.AttendanceStatus;
import acadtracker.model.AuthenticatedUser;
import acadtracker.service.FacultyDashboardService;
import acadtracker.service.FacultyAttendanceService;
import acadtracker.service.FacultyAssignmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FacultyDashboardFrame extends JFrame {

    private final AuthenticatedUser faculty;
    private final ReportService reportService;

    private JComboBox<SubjectOption> reportSubjectCombo;
    private JTable reportTable;
    private DefaultTableModel reportModel;
    private final FacultyDashboardService dashboardService;
    private final FacultyAttendanceService attendanceService;
    private final FacultyAssignmentService assignmentService;

    private JComboBox<SubjectOption> attendanceSubjectCombo;
    private JComboBox<SubjectOption> assignmentSubjectCombo;
    private JComboBox<StudentOption> studentCombo;
    private JComboBox<AttendanceStatus> statusCombo;

    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JTable attendanceHistoryTable;
    private DefaultTableModel attendanceHistoryModel;

    private JTable assignmentTable;
    private DefaultTableModel assignmentModel;

    public FacultyDashboardFrame(AuthenticatedUser faculty) {

        this.faculty = faculty;

        dashboardService =
                new FacultyDashboardService(faculty);

        attendanceService =
                new FacultyAttendanceService(faculty);

        assignmentService =
                new FacultyAssignmentService(faculty);

        reportService = new ReportService(faculty);

        setTitle("ACAD TRACKER - Faculty Dashboard");
        setSize(1050, 650);
        setMinimumSize(new Dimension(960, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createDashboard();
    }

    private void createDashboard() {

        JPanel background = DashboardTheme.createBackground();
        background.setLayout(new BorderLayout(18, 18));
        background.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 20, 25)
        );

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel header = DashboardTheme.createHeader(
                faculty.getName(),
                "Faculty",
                "Faculty ID: " + faculty.getUserId()
                        + "    ·    " + faculty.getEmail(),
                logoutButton
        );

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("  My Subjects  ", createSubjectsPanel());
        tabs.addTab("  Students & Attendance  ", createAttendancePanel());
        tabs.addTab("  Assignments  ", createAssignmentsPanel());
        tabs.addTab("  Student Records  ", new FacultyRecordsPanel(faculty));
        tabs.addTab("  Reports  ", createReportsPanel());

        DashboardTheme.styleTabs(tabs);

        JPanel content = DashboardTheme.createGlassPanel();
        content.add(tabs, BorderLayout.CENTER);

        background.add(header, BorderLayout.NORTH);
        background.add(content, BorderLayout.CENTER);

        setContentPane(background);
    }

    private JPanel createSubjectsPanel() {

        String[] columns = {
                "Subject ID",
                "Subject Name",
                "Subject Code"
        };

        DefaultTableModel model =
                createReadOnlyModel(columns);

        JTable table = new JTable(model);

        try {

            List<String[]> subjects =
                    dashboardService.getSubjects();

            for (String[] subject : subjects) {
                model.addRow(subject);
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }

        return createTablePanel(table, "No subjects yet");
    }

    private JPanel createAttendancePanel() {

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

        JPanel fields = new JPanel(new GridLayout(3, 2, 12, 10));
        fields.setOpaque(false);

        attendanceSubjectCombo = new JComboBox<>();
        studentCombo = new JComboBox<>();

        statusCombo = new JComboBox<>(
                AttendanceStatus.values()
        );

        DashboardTheme.styleCombo(attendanceSubjectCombo);
        DashboardTheme.styleCombo(studentCombo);
        DashboardTheme.styleCombo(statusCombo);

        JButton loadStudentsButton = new JButton("Load Students");
        JButton markButton = new JButton("Mark Attendance");
        JButton loadHistoryButton = new JButton("Load Attendance History");
        JButton editAttendanceButton = new JButton("Edit Selected Attendance");

        DashboardTheme.styleSecondaryButton(loadStudentsButton);
        DashboardTheme.styleButton(markButton);
        DashboardTheme.styleSecondaryButton(loadHistoryButton);
        DashboardTheme.styleSecondaryButton(editAttendanceButton);

        fields.add(DashboardTheme.formLabel("Select Subject"));
        fields.add(attendanceSubjectCombo);
        fields.add(DashboardTheme.formLabel("Select Student"));
        fields.add(studentCombo);
        fields.add(DashboardTheme.formLabel("Attendance Status"));
        fields.add(statusCombo);

        JPanel actions = DashboardTheme.buttonBar();
        actions.add(loadStudentsButton);
        actions.add(markButton);
        actions.add(loadHistoryButton);
        actions.add(editAttendanceButton);

        JPanel form = DashboardTheme.createContentCard(28);
        form.add(
                DashboardTheme.sectionTitle("Mark Student Attendance"),
                BorderLayout.NORTH
        );
        form.add(fields, BorderLayout.CENTER);
        form.add(actions, BorderLayout.SOUTH);

        studentModel = createReadOnlyModel(
                new String[]{
                        "Student ID",
                        "Student Name",
                        "Email",
                        "Enrollment ID"
                }
        );

        studentTable = new JTable(studentModel);
        DashboardTheme.styleTable(studentTable);
        loadSubjectsIntoCombo(attendanceSubjectCombo);

        attendanceSubjectCombo.addActionListener(e -> {
            studentCombo.removeAllItems();
            studentModel.setRowCount(0);
            if (attendanceHistoryModel != null) attendanceHistoryModel.setRowCount(0);
        });

        loadStudentsButton.addActionListener(e -> loadStudents());
        markButton.addActionListener(e -> markAttendance());
        loadHistoryButton.addActionListener(e -> loadAttendanceHistory());
        editAttendanceButton.addActionListener(e -> editSelectedAttendance());

        JPanel students = DashboardTheme.createContentCard(28);
        students.add(
                DashboardTheme.sectionTitle("Enrolled Students"),
                BorderLayout.NORTH
        );
        students.add(
                DashboardTheme.tableView(studentTable, "No students loaded"),
                BorderLayout.CENTER
        );

        attendanceHistoryModel = createReadOnlyModel(
                new String[]{"Attendance ID", "Date", "Status"}
        );
        attendanceHistoryTable = new JTable(attendanceHistoryModel);
        DashboardTheme.styleTable(attendanceHistoryTable);

        JPanel history = DashboardTheme.createContentCard(28);
        history.add(DashboardTheme.sectionTitle("Attendance History"), BorderLayout.NORTH);
        history.add(DashboardTheme.tableView(attendanceHistoryTable, "No attendance history loaded"), BorderLayout.CENTER);

        JPanel columns = new JPanel(new GridLayout(1, 2, 16, 0));
        columns.setOpaque(false);
        columns.add(students);
        columns.add(history);

        panel.add(form, BorderLayout.NORTH);
        panel.add(columns, BorderLayout.CENTER);
        return panel;
    }

    private void loadSubjectsIntoCombo(
            JComboBox<SubjectOption> combo
    ) {

        combo.removeAllItems();

        try {

            List<String[]> subjects =
                    dashboardService.getSubjects();

            for (String[] subject : subjects) {

                combo.addItem(
                        new SubjectOption(
                                Integer.parseInt(subject[0]),
                                subject[1],
                                subject[2]
                        )
                );
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void loadStudents() {

        SubjectOption selected =
                (SubjectOption) attendanceSubjectCombo.getSelectedItem();

        if (selected == null) {
            showMessage("Please select a subject.");
            return;
        }

        studentCombo.removeAllItems();
        studentModel.setRowCount(0);

        try {

            List<String[]> students =
                    dashboardService.getStudents(selected.id);

            for (String[] student : students) {

                studentModel.addRow(student);

                studentCombo.addItem(
                        new StudentOption(
                                Integer.parseInt(student[3]),
                                student[1],
                                Integer.parseInt(student[0])
                        )
                );
            }

            if (students.isEmpty()) {
                showMessage("No students are enrolled in this subject.");
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void loadAttendanceHistory() {
        StudentOption student = (StudentOption) studentCombo.getSelectedItem();
        if (student == null) {
            showMessage("Please load students and select a student first.");
            return;
        }
        attendanceHistoryModel.setRowCount(0);
        try {
            List<Attendance> records = attendanceService.getAttendanceHistory(student.enrollmentId);
            for (Attendance record : records) {
                attendanceHistoryModel.addRow(new Object[]{
                        record.getAttendanceId(),
                        record.getAttendanceDate().toString(),
                        record.getStatus().name()
                });
            }
            if (records.isEmpty()) showMessage("No attendance records exist for this student yet.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attendance History Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedAttendance() {
        int selectedRow = attendanceHistoryTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Select an attendance record from the history table.");
            return;
        }
        StudentOption student = (StudentOption) studentCombo.getSelectedItem();
        if (student == null) {
            showMessage("Select a student first.");
            return;
        }
        int row = attendanceHistoryTable.convertRowIndexToModel(selectedRow);
        int attendanceId = Integer.parseInt(attendanceHistoryModel.getValueAt(row, 0).toString());
        String oldStatus = attendanceHistoryModel.getValueAt(row, 2).toString();
        JComboBox<AttendanceStatus> statusPicker = new JComboBox<>(AttendanceStatus.values());
        statusPicker.setSelectedItem(AttendanceStatus.valueOf(oldStatus));
        JPanel editor = new JPanel(new GridLayout(2, 2, 8, 8));
        editor.add(new JLabel("Attendance date:"));
        editor.add(new JLabel(attendanceHistoryModel.getValueAt(row, 1).toString()));
        editor.add(new JLabel("Correct status:"));
        editor.add(statusPicker);
        int choice = JOptionPane.showConfirmDialog(this, editor, "Correct Attendance", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        try {
            LocalDate date = LocalDate.parse(attendanceHistoryModel.getValueAt(row, 1).toString());
            Attendance updated = new Attendance(attendanceId, student.enrollmentId, date,
                    (AttendanceStatus) statusPicker.getSelectedItem());
            if (attendanceService.updateAttendance(updated)) {
                JOptionPane.showMessageDialog(this, "Attendance corrected successfully.");
                loadAttendanceHistory();
            } else {
                showMessage("Attendance could not be updated. Refresh and try again.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Attendance Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAttendance() {

        StudentOption student =
                (StudentOption) studentCombo.getSelectedItem();

        AttendanceStatus status =
                (AttendanceStatus) statusCombo.getSelectedItem();

        if (student == null || status == null) {
            showMessage("Please select a student and attendance status.");
            return;
        }

        Attendance attendance = new Attendance(
                0,
                student.enrollmentId,
                LocalDate.now(),
                status
        );

        try {

            boolean saved =
                    attendanceService.markAttendance(attendance);

            if (saved) {
                JOptionPane.showMessageDialog(
                        this,
                        "Attendance marked successfully."
                );
            } else {
                showMessage(
                        "Attendance was not saved. Check whether a record "
                        + "already exists for today."
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Attendance Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
   

private JPanel createAssignmentsPanel() {

    JPanel panel = new JPanel(new BorderLayout(12, 12));
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

    JPanel fields = new JPanel(new GridLayout(5, 2, 12, 10));
    fields.setOpaque(false);

    assignmentSubjectCombo = new JComboBox<>();
    DashboardTheme.styleCombo(assignmentSubjectCombo);

    JTextField titleField = new JTextField();
    JTextField descriptionField = new JTextField();
    JTextField assignedDateField =
            new JTextField(LocalDate.now().toString());
    JTextField dueDateField =
            new JTextField(LocalDate.now().plusDays(7).toString());

    DashboardTheme.styleTextField(titleField);
    DashboardTheme.styleTextField(descriptionField);
    DashboardTheme.styleTextField(assignedDateField);
    DashboardTheme.styleTextField(dueDateField);

    JButton createButton = new JButton("Create Assignment");
    JButton refreshButton = new JButton("Refresh Assignments");
    JButton editButton = new JButton("Edit Assignment");

    DashboardTheme.styleButton(createButton);
    DashboardTheme.styleSecondaryButton(refreshButton);
    DashboardTheme.styleSecondaryButton(editButton);

    fields.add(DashboardTheme.formLabel("Select Subject"));
    fields.add(assignmentSubjectCombo);
    fields.add(DashboardTheme.formLabel("Assignment Title"));
    fields.add(titleField);
    fields.add(DashboardTheme.formLabel("Description"));
    fields.add(descriptionField);
    fields.add(DashboardTheme.formLabel("Assigned Date (YYYY-MM-DD)"));
    fields.add(assignedDateField);
    fields.add(DashboardTheme.formLabel("Due Date (YYYY-MM-DD)"));
    fields.add(dueDateField);

    loadSubjectsIntoCombo(assignmentSubjectCombo);

    String[] columns = {
    "Assignment ID",
    "Title",
    "Description",
    "Assigned Date",
    "Due Date"
};

    assignmentModel = createReadOnlyModel(columns);

    assignmentTable = new JTable(assignmentModel);
    DashboardTheme.styleTable(assignmentTable);

    // CREATE ASSIGNMENT
    createButton.addActionListener(e -> {

        try {

            SubjectOption subject =
                    (SubjectOption) assignmentSubjectCombo.getSelectedItem();

            if (subject == null) {
                showMessage("Please select a subject.");
                return;
            }

            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();

            if (title.isEmpty()) {
                showMessage("Please enter an assignment title.");
                return;
            }

            LocalDate assignedDate =
                    LocalDate.parse(assignedDateField.getText().trim());

            LocalDate dueDate =
                    LocalDate.parse(dueDateField.getText().trim());

            if (dueDate.isBefore(assignedDate)) {
                showMessage("Due date cannot be before assigned date.");
                return;
            }

            Assignment assignment = new Assignment(
                    0,
                    subject.id,
                    title,
                    description,
                    assignedDate,
                    dueDate
            );

            boolean created =
                    assignmentService.createAssignment(assignment);

            if (created) {

                JOptionPane.showMessageDialog(
                        this,
                        "Assignment created successfully."
                );

                titleField.setText("");
                descriptionField.setText("");

                loadAssignments(subject.id);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to create assignment: " + ex.getMessage(),
                    "Assignment Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    });

    // REFRESH ASSIGNMENTS
    refreshButton.addActionListener(e -> {

        SubjectOption subject =
                (SubjectOption) assignmentSubjectCombo.getSelectedItem();

        if (subject != null) {
            loadAssignments(subject.id);
        }
    });

    // EDIT ASSIGNMENT
    editButton.addActionListener(e -> editSelectedAssignment());

    JPanel actions = DashboardTheme.buttonBar();
    actions.add(refreshButton);
    actions.add(editButton);
    actions.add(createButton);

    JPanel form = DashboardTheme.createContentCard();
    form.add(
            DashboardTheme.sectionTitle("Create Assignment"),
            BorderLayout.NORTH
    );
    form.add(fields, BorderLayout.CENTER);
    form.add(actions, BorderLayout.SOUTH);

    JPanel list = DashboardTheme.createContentCard();
    list.add(
            DashboardTheme.sectionTitle("Assignments"),
            BorderLayout.NORTH
    );
    list.add(
            DashboardTheme.tableView(assignmentTable, "No assignments yet"),
            BorderLayout.CENTER
    );

    panel.add(form, BorderLayout.NORTH);
    panel.add(list, BorderLayout.CENTER);
    return panel;
}


private void editSelectedAssignment() {

    int selectedRow = assignmentTable.getSelectedRow();

    if (selectedRow == -1) {
        showMessage("Please select an assignment to edit.");
        return;
    }

    int modelRow =
            assignmentTable.convertRowIndexToModel(selectedRow);

    try {

        int assignmentId = Integer.parseInt(
                assignmentModel.getValueAt(modelRow, 0).toString()
        );

        String oldTitle =
        assignmentModel.getValueAt(modelRow, 1).toString();

String oldDescription =
        assignmentModel.getValueAt(modelRow, 2) == null
                ? ""
                : assignmentModel.getValueAt(modelRow, 2).toString();

String oldAssignedDate =
        assignmentModel.getValueAt(modelRow, 3).toString();

String oldDueDate =
        assignmentModel.getValueAt(modelRow, 4).toString();
        SubjectOption subject =
                (SubjectOption) assignmentSubjectCombo.getSelectedItem();

        if (subject == null) {
            showMessage("Please select a subject.");
            return;
        }

        JTextField titleField = new JTextField(oldTitle);
        JTextField descriptionField = new JTextField(oldDescription);
        JTextField assignedDateField =
                new JTextField(oldAssignedDate);
        JTextField dueDateField =
                new JTextField(oldDueDate);

        DashboardTheme.styleTextField(titleField);
        DashboardTheme.styleTextField(descriptionField);
        DashboardTheme.styleTextField(assignedDateField);
        DashboardTheme.styleTextField(dueDateField);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));

        form.add(new JLabel("Assignment Title:"));
        form.add(titleField);

        form.add(new JLabel("Description:"));
        form.add(descriptionField);

        form.add(new JLabel("Assigned Date (YYYY-MM-DD):"));
        form.add(assignedDateField);

        form.add(new JLabel("Due Date (YYYY-MM-DD):"));
        form.add(dueDateField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Assignment",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String newTitle = titleField.getText().trim();

        if (newTitle.isEmpty()) {
            showMessage("Assignment title cannot be empty.");
            return;
        }

        LocalDate assignedDate =
                LocalDate.parse(assignedDateField.getText().trim());

        LocalDate dueDate =
                LocalDate.parse(dueDateField.getText().trim());

        if (dueDate.isBefore(assignedDate)) {
            showMessage("Due date cannot be before assigned date.");
            return;
        }

        Assignment updatedAssignment = new Assignment(
                assignmentId,
                subject.id,
                newTitle,
                descriptionField.getText().trim(),
                assignedDate,
                dueDate
        );

        boolean updated =
                assignmentService.updateAssignment(updatedAssignment);

        if (updated) {

            JOptionPane.showMessageDialog(
                    this,
                    "Assignment updated successfully!"
            );

            loadAssignments(subject.id);

        } else {

            showMessage("Assignment could not be updated.");
        }

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to update assignment: " + ex.getMessage(),
                "Edit Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


private void loadAssignments(int subjectId) {
assignmentModel.setRowCount(0);
try {
List<String[]> assignments =
dashboardService.getAssignments(subjectId);
for (String[] assignment : assignments) {
assignmentModel.addRow(assignment);
}
} catch (SQLException e) {
showDatabaseError(e);
}
}
private DefaultTableModel createReadOnlyModel(
String[] columns
) {
return new DefaultTableModel(columns, 0) {
@Override
public boolean isCellEditable(int row, int column) {
return false;
}
};
}
private JPanel createTablePanel(JTable table, String emptyMessage) {
    JPanel panel = DashboardTheme.createContentCard();
    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    panel.add(
            DashboardTheme.tableView(table, emptyMessage),
            BorderLayout.CENTER
    );
    return panel;
}
private void showMessage(String message) {
JOptionPane.showMessageDialog(this, message);
}
private void showDatabaseError(SQLException e) {
JOptionPane.showMessageDialog(
this,
"Unable to load information from the database.",
"Database Error",
JOptionPane.ERROR_MESSAGE
);
e.printStackTrace();
}
private static class SubjectOption {
private final int id;
private final String name;
private final String code;
SubjectOption(int id, String name, String code) {
this.id = id;
this.name = name;
this.code = code;
}
@Override
public String toString() {
return name + " (" + code + ")";
}
}
private static class StudentOption {
private final int enrollmentId;
private final String name;
private final int studentId;
StudentOption(int enrollmentId, String name, int studentId) {
this.enrollmentId = enrollmentId;
this.name = name;
this.studentId = studentId;
}
@Override
public String toString() {
return name + " (ID: " + studentId + ")";
}
}
private JPanel createReportsPanel() {
JPanel panel = new JPanel(new BorderLayout(12, 12));
panel.setOpaque(false);
panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
controls.setOpaque(false);

reportSubjectCombo = new JComboBox<>();
DashboardTheme.styleCombo(reportSubjectCombo);
JButton attendanceButton = new JButton("All Attendance");
JButton subjectButton = new JButton("Subject Attendance");
JButton assignmentButton = new JButton("Assignment Report");
JButton exportButton = new JButton("Export CSV");
DashboardTheme.styleSecondaryButton(attendanceButton);
DashboardTheme.styleSecondaryButton(subjectButton);
DashboardTheme.styleSecondaryButton(assignmentButton);
DashboardTheme.styleButton(exportButton);
controls.add(DashboardTheme.formLabel("Subject"));
controls.add(reportSubjectCombo);
controls.add(attendanceButton);
controls.add(subjectButton);
controls.add(assignmentButton);
controls.add(exportButton);

JPanel toolbar = DashboardTheme.createContentCard();
toolbar.add(controls, BorderLayout.CENTER);
reportModel = createReadOnlyModel(
new String[]{
"Student",
"Subject",
"Present",
"Absent",
"Leave",
"Attendance %"
}
);
reportTable = new JTable(reportModel);
loadReportSubjects();
attendanceButton.addActionListener(e ->
loadAllAttendanceReport()
);
subjectButton.addActionListener(e ->
loadSubjectAttendanceReport()
);
assignmentButton.addActionListener(e ->
loadAssignmentReport()
);
exportButton.addActionListener(e ->
exportCurrentReport()
);
JPanel reportCard = DashboardTheme.createContentCard();
reportCard.add(
        DashboardTheme.sectionTitle("Report"),
        BorderLayout.NORTH
);
reportCard.add(
        DashboardTheme.tableView(reportTable, "No records in this report"),
        BorderLayout.CENTER
);

panel.add(toolbar, BorderLayout.NORTH);
panel.add(reportCard, BorderLayout.CENTER);
return panel;
}
private void loadReportSubjects() {
reportSubjectCombo.removeAllItems();
try {
List<String[]> subjects =
dashboardService.getSubjects();
for (String[] subject : subjects) {
reportSubjectCombo.addItem(
new SubjectOption(
Integer.parseInt(subject[0]),
subject[1],
subject[2]
)
);
}
} catch (SQLException e) {
showDatabaseError(e);
}
}
private void loadAllAttendanceReport() {
setReportColumns(new String[]{
"Student",
"Subject",
"Present",
"Absent",
"Leave",
"Attendance %"
});
try {
List<ReportRow> reports = reportService.getAttendanceReport();
for (ReportRow report : reports) {
reportModel.addRow(new Object[]{
report.getStudentName(),
report.getSubjectName(),
report.getPresentCount(),
report.getAbsentCount(),
report.getLeaveCount(),
String.format(
"%.2f%%",
report.getAttendancePercentage()
)
});
}
} catch (SQLException e) {
showDatabaseError(e);
}
}
private void loadSubjectAttendanceReport() {
SubjectOption subject =
(SubjectOption) reportSubjectCombo.getSelectedItem();
if (subject == null) {
showMessage("Please select a subject.");
return;
}
setReportColumns(new String[]{
"Student",
"Subject",
"Present",
"Absent",
"Leave",
"Attendance %"
});
try {
List<ReportRow> reports = reportService.getSubjectAttendanceReport(
subject.id
);
for (ReportRow report : reports) {
reportModel.addRow(new Object[]{
report.getStudentName(),
report.getSubjectName(),
report.getPresentCount(),
report.getAbsentCount(),
report.getLeaveCount(),
String.format(
"%.2f%%",
report.getAttendancePercentage()
)
});
}
} catch (SQLException e) {
showDatabaseError(e);
}
}
private void loadAssignmentReport() {
setReportColumns(new String[]{
"Subject",
"Assignment",
"Total Students",
"Assigned",
"Pending",
"Completed"
});
try {
List<String[]> reports =
reportService.getAssignmentReport();
for (String[] report : reports) {
reportModel.addRow(report);
}
} catch (SQLException e) {
showDatabaseError(e);
}
}
private void setReportColumns(String[] columns) {
reportModel.setDataVector(
new Object[0][columns.length],
columns
);
}
private void exportCurrentReport() {
if (reportTable.getColumnCount() == 0) {
showMessage("There is no report to export.");
return;
}
if (reportTable.getRowCount() == 0) {
showMessage("The selected report has no records.");
return;
}
JFileChooser chooser = new JFileChooser();
chooser.setDialogTitle("Save Report as CSV");
chooser.setFileFilter(
new FileNameExtensionFilter(
"CSV Files (*.csv)",
"csv"
)
);
chooser.setSelectedFile(
new File("acad_tracker_report.csv")
);
int choice = chooser.showSaveDialog(this);
if (choice != JFileChooser.APPROVE_OPTION) {
return;
}
File selectedFile = chooser.getSelectedFile();
String fileName = selectedFile.getName();
if (!fileName.toLowerCase().endsWith(".csv")) {
selectedFile = new File(
selectedFile.getParentFile(),
fileName + ".csv"
);
}
try {
CsvExporter.exportTable(
reportTable,
selectedFile.toPath()
);
JOptionPane.showMessageDialog(
this,
"Report exported successfully:\n"
+ selectedFile.getAbsolutePath()
);
} catch (Exception e) {
JOptionPane.showMessageDialog(
this,
"Unable to export report: " + e.getMessage(),
"Export Error",
JOptionPane.ERROR_MESSAGE
);
}
}
}