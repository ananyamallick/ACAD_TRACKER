package acadtracker.gui;

import acadtracker.exceptions.InvalidAttendanceException;
import acadtracker.model.AssignmentStatus;
import acadtracker.model.AttendanceStatus;
import acadtracker.model.AuthenticatedUser;
import acadtracker.service.FacultyDashboardService;
import acadtracker.service.FacultyRecordService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

/**
 * Faculty view of student attendance and assignment rows
 * for a subject this faculty member teaches.
 * Updates go through {@link FacultyRecordService}, which
 * rejects records outside those subjects.
 */
public class FacultyRecordsPanel extends JPanel {

    private final FacultyDashboardService dashboardService;
    private final FacultyRecordService recordService;

    private JComboBox<SubjectOption> subjectCombo;
    private boolean fillingSubjects;
    private DefaultTableModel attendanceModel;
    private JTable attendanceTable;
    private DefaultTableModel assignmentModel;
    private JTable assignmentTable;

    public FacultyRecordsPanel(AuthenticatedUser faculty) {
        dashboardService = new FacultyDashboardService(faculty);
        recordService = new FacultyRecordService(faculty);

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(createToolbar(), BorderLayout.NORTH);
        add(createTables(), BorderLayout.CENTER);

        loadSubjects();
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JLabel label = new JLabel("Subject:");
        label.setForeground(DashboardTheme.WHITE);

        subjectCombo = new JComboBox<>();
        DashboardTheme.styleCombo(subjectCombo);
        subjectCombo.addActionListener(e -> loadRecords());

        JButton refreshButton = new JButton("Refresh");
        DashboardTheme.styleButton(refreshButton);
        refreshButton.addActionListener(e -> loadRecords());

        toolbar.add(label);
        toolbar.add(subjectCombo);
        toolbar.add(refreshButton);
        return toolbar;
    }

    private JSplitPane createTables() {
        attendanceModel = readOnlyModel(new String[]{
                "Attendance ID",
                "Student",
                "Date",
                "Status"
        });
        attendanceTable = new JTable(attendanceModel);
        DashboardTheme.styleTable(attendanceTable);
        column(attendanceTable, 3).setCellRenderer(new StatusLabelRenderer(true));
        hideColumn(attendanceTable, 0);

        assignmentModel = readOnlyModel(new String[]{
                "Student ID",
                "Student",
                "Assignment ID",
                "Assignment",
                "Status",
                "Grade"
        });
        assignmentTable = new JTable(assignmentModel);
        DashboardTheme.styleTable(assignmentTable);
        column(assignmentTable, 4).setCellRenderer(new StatusLabelRenderer(false));
        hideColumn(assignmentTable, 0);
        hideColumn(assignmentTable, 2);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                section(
                        "Attendance",
                        attendanceTable,
                        "Edit Attendance",
                        this::editAttendance
                ),
                section(
                        "Assignments",
                        assignmentTable,
                        "Edit Assignment",
                        this::editAssignment
                )
        );
        split.setResizeWeight(0.45);
        split.setOpaque(false);
        split.setBorder(BorderFactory.createEmptyBorder());
        return split;
    }

    private JPanel section(
            String title,
            JTable table,
            String buttonText,
            Runnable editAction
    ) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        javax.swing.border.TitledBorder border =
                BorderFactory.createTitledBorder(title);
        border.setTitleColor(DashboardTheme.WHITE);
        panel.setBorder(border);

        JScrollPane scroll = new JScrollPane(table);
        DashboardTheme.styleScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        JButton editButton = new JButton(buttonText);
        DashboardTheme.styleButton(editButton);
        editButton.addActionListener(e -> editAction.run());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(editButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void loadSubjects() {
        fillingSubjects = true;
        subjectCombo.removeAllItems();

        try {
            List<String[]> subjects = dashboardService.getSubjects();

            for (String[] subject : subjects) {
                subjectCombo.addItem(new SubjectOption(
                        Integer.parseInt(subject[0]),
                        subject[1],
                        subject[2]
                ));
            }
        } catch (SQLException exception) {
            showError("Subjects could not be loaded.\n" + exception.getMessage());
        } finally {
            fillingSubjects = false;
        }

        loadRecords();
    }

    private void loadRecords() {
        if (fillingSubjects) {
            return;
        }

        attendanceModel.setRowCount(0);
        assignmentModel.setRowCount(0);

        SubjectOption subject = (SubjectOption) subjectCombo.getSelectedItem();
        if (subject == null) {
            return;
        }

        try {
            for (String[] row : recordService.getAttendanceRecords(subject.id)) {
                attendanceModel.addRow(row);
            }

            for (String[] row : recordService.getAssignmentRecords(subject.id)) {
                assignmentModel.addRow(row);
            }
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Records could not be loaded.\n" + exception.getMessage());
        }
    }

    private void editAttendance() {
        int modelRow = selectedModelRow(attendanceTable);
        if (modelRow < 0) {
            showWarning("Select an attendance record to edit.");
            return;
        }

        int attendanceId = Integer.parseInt(
                attendanceModel.getValueAt(modelRow, 0).toString()
        );
        String student = attendanceModel.getValueAt(modelRow, 1).toString();
        AttendanceStatus current = AttendanceStatus.valueOf(
                attendanceModel.getValueAt(modelRow, 3).toString()
        );

        JComboBox<AttendanceStatus> statusCombo =
                new JComboBox<>(AttendanceStatus.values());
        statusCombo.setSelectedItem(current);

        JPanel form = new JPanel(new GridLayout(3, 1, 6, 6));
        form.add(new JLabel("Student: " + student));
        form.add(new JLabel("Attendance status:"));
        form.add(statusCombo);

        if (!confirm(form, "Edit Attendance")) {
            return;
        }

        AttendanceStatus selected =
                (AttendanceStatus) statusCombo.getSelectedItem();

        try {
            boolean updated = recordService.updateAttendance(
                    attendanceId,
                    selected
            );

            if (updated) {
                showInfo("Attendance updated.");
                loadRecords();
            } else {
                showError("That attendance record could not be updated.");
            }
        } catch (InvalidAttendanceException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Attendance could not be updated.\n" + exception.getMessage());
        }
    }

    private void editAssignment() {
        int modelRow = selectedModelRow(assignmentTable);
        if (modelRow < 0) {
            showWarning("Select an assignment record to edit.");
            return;
        }

        int studentId = Integer.parseInt(
                assignmentModel.getValueAt(modelRow, 0).toString()
        );
        String student = assignmentModel.getValueAt(modelRow, 1).toString();
        int assignmentId = Integer.parseInt(
                assignmentModel.getValueAt(modelRow, 2).toString()
        );
        String title = assignmentModel.getValueAt(modelRow, 3).toString();
        AssignmentStatus current = AssignmentStatus.fromDatabase(
                assignmentModel.getValueAt(modelRow, 4).toString()
        );
        String grade = assignmentModel.getValueAt(modelRow, 5) == null
                ? ""
                : assignmentModel.getValueAt(modelRow, 5).toString();

        JComboBox<AssignmentStatus> statusCombo =
                new JComboBox<>(AssignmentStatus.values());
        statusCombo.setSelectedItem(current);
        statusCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );
                if (value instanceof AssignmentStatus status) {
                    setText(status.label());
                }
                return this;
            }
        });

        JTextField gradeField = new JTextField(grade);
        DashboardTheme.styleTextField(gradeField);

        JPanel form = new JPanel(new GridLayout(5, 1, 6, 6));
        form.add(new JLabel(student + " — " + title));
        form.add(new JLabel("Assignment status:"));
        form.add(statusCombo);
        form.add(new JLabel("Grade (optional):"));
        form.add(gradeField);

        if (!confirm(form, "Edit Assignment Record")) {
            return;
        }

        AssignmentStatus selected =
                (AssignmentStatus) statusCombo.getSelectedItem();

        try {
            boolean updated = recordService.updateAssignmentRecord(
                    assignmentId,
                    studentId,
                    selected,
                    gradeField.getText()
            );

            if (updated) {
                showInfo("Assignment record updated.");
                loadRecords();
            } else {
                showError("That assignment record could not be updated.");
            }
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError(
                    "The assignment record could not be updated.\n"
                            + exception.getMessage()
            );
        }
    }

    /**
     * After a column is hidden, view indexes no longer match the model.
     * The status renderer is attached to the view column that remains.
     */
    private javax.swing.table.TableColumn column(JTable table, int modelIndex) {
        return table.getColumnModel().getColumn(
                table.convertColumnIndexToView(modelIndex)
        );
    }

    private void hideColumn(JTable table, int modelIndex) {
        javax.swing.table.TableColumn column =
                table.getColumnModel().getColumn(
                        table.convertColumnIndexToView(modelIndex)
                );
        table.getColumnModel().removeColumn(column);
    }

    private int selectedModelRow(JTable table) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return -1;
        }
        return table.convertRowIndexToModel(viewRow);
    }

    private DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private boolean confirm(JPanel form, String title) {
        return JOptionPane.showConfirmDialog(
                this,
                form,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) == JOptionPane.OK_OPTION;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Updated",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Select a record",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Student Records",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Shows Present / Absent / Leave, or Assigned / Pending / Completed,
     * while the model keeps the database token.
     */
    private static final class StatusLabelRenderer extends DefaultTableCellRenderer {

        private final boolean attendance;

        private StatusLabelRenderer(boolean attendance) {
            this.attendance = attendance;
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            String text = value == null ? "" : value.toString();

            if (attendance) {
                text = labelAttendance(text);
            } else if (!text.isBlank()) {
                text = AssignmentStatus.fromDatabase(text).label();
            }

            return super.getTableCellRendererComponent(
                    table,
                    text,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );
        }

        private String labelAttendance(String status) {
            return switch (status) {
                case "PRESENT" -> "Present";
                case "ABSENT" -> "Absent";
                case "LEAVE" -> "Leave";
                default -> status;
            };
        }
    }

    private static final class SubjectOption {
        private final int id;
        private final String name;
        private final String code;

        private SubjectOption(int id, String name, String code) {
            this.id = id;
            this.name = name;
            this.code = code;
        }

        @Override
        public String toString() {
            return name + " (" + code + ")";
        }
    }
}
