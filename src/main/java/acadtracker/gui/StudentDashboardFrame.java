package acadtracker.gui;

import acadtracker.model.AssignmentStatus;
import acadtracker.model.AuthenticatedUser;
import acadtracker.service.StudentDashboardService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentDashboardFrame extends JFrame {

    private final AuthenticatedUser user;
    private final StudentDashboardService dashboardService;

    public StudentDashboardFrame(AuthenticatedUser user) {

        this.user = user;
        this.dashboardService =
                new StudentDashboardService(user);

        setTitle("ACAD TRACKER - Student Dashboard");
        setSize(950, 600);
        setMinimumSize(new Dimension(860, 560));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createDashboard();
    }

    private void createDashboard() {

        JPanel background = DashboardTheme.createBackground();

        background.setLayout(
                new BorderLayout(18, 18)
        );

        background.setBorder(
                BorderFactory.createEmptyBorder(
                        22, 25, 20, 25
                )
        );

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel header = DashboardTheme.createHeader(
                user.getName(),
                "Student",
                "Student ID: " + user.getUserId()
                        + "    ·    " + user.getEmail(),
                logoutButton
        );

        JTabbedPane tabs =
                new JTabbedPane();

        tabs.addTab(
                "  My Subjects  ",
                createSubjectsPanel()
        );

        tabs.addTab(
                "  Attendance  ",
                createAttendancePanel()
        );

        tabs.addTab(
                "  Assignments  ",
                createAssignmentsPanel()
        );

        DashboardTheme.styleTabs(tabs);

        JPanel content =
                DashboardTheme.createGlassPanel();

        content.add(
                tabs,
                BorderLayout.CENTER
        );

        background.add(
                header,
                BorderLayout.NORTH
        );

        background.add(
                content,
                BorderLayout.CENTER
        );

        setContentPane(background);
    }

    // =========================================================
    // SUBJECTS
    // =========================================================

    private JPanel createSubjectsPanel() {

        String[] columns = {
                "Subject Name",
                "Subject Code",
                "Faculty"
        };

        DefaultTableModel model =
                createReadOnlyModel(columns);

        JTable table = new JTable(model);

        try {

            List<String[]> subjects =
                    dashboardService.getSubjects();

            for (String[] subject : subjects) {
                model.addRow(new String[]{
                        subject[1],
                        subject[2],
                        subject[3]
                });
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }

        return createTablePanel(table, "No subjects yet");
    }

    // =========================================================
    // ATTENDANCE
    // =========================================================

    private JPanel createAttendancePanel() {

        JPanel panel = new JPanel(new GridLayout(2, 1, 12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

        String[] summaryColumns = {
                "Subject",
                "Present",
                "Absent",
                "Leave",
                "Attendance Percentage"
        };

        DefaultTableModel summaryModel =
                createReadOnlyModel(
                        summaryColumns
                );

        JTable summaryTable =
                new JTable(summaryModel);

        try {

            List<String[]> summaries =
                    dashboardService
                            .getAttendanceSummary();

            for (String[] summary : summaries) {
                summaryModel.addRow(summary);
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }

        String[] detailColumns = {
                "Subject",
                "Date",
                "Status"
        };

        DefaultTableModel detailModel =
                createReadOnlyModel(
                        detailColumns
                );

        JTable detailTable =
                new JTable(detailModel);

        try {

            List<String[]> records =
                    dashboardService.getAttendance();

            for (String[] record : records) {
                detailModel.addRow(record);
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }

        JPanel summaryPanel = createSection(
                "Attendance Summary",
                summaryTable,
                "No attendance records"
        );
        DashboardTheme.colorCountColumn(
                summaryTable, 1, DashboardTheme.SUCCESS
        );
        DashboardTheme.colorCountColumn(
                summaryTable, 2, DashboardTheme.DANGER
        );
        DashboardTheme.colorCountColumn(
                summaryTable, 3, DashboardTheme.WARNING
        );

        JPanel detailPanel = createSection(
                "Attendance History",
                detailTable,
                "No attendance records"
        );
        DashboardTheme.colorStatusColumn(detailTable, 2);

        panel.add(summaryPanel);
        panel.add(detailPanel);
        return panel;
    }

    // =========================================================
    // ASSIGNMENTS
    // =========================================================

    private JPanel createAssignmentsPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

        String[] columns = {
                "Assignment",
                "Subject",
                "Due Date",
                "Status",
                "Submission Date"
        };

        DefaultTableModel model =
                createReadOnlyModel(columns);

        JTable table =
                new JTable(model);

        loadAssignments(model);

        JPanel card = DashboardTheme.createContentCard();
        card.add(
                DashboardTheme.sectionTitle("Assignments"),
                BorderLayout.NORTH
        );
        card.add(
                DashboardTheme.tableView(table, "No assignments yet"),
                BorderLayout.CENTER
        );
        DashboardTheme.colorStatusColumn(table, 3);

        JButton editButton = new JButton("Edit Status");
        DashboardTheme.styleButton(editButton);

        editButton.addActionListener(e -> {

            int selectedRow =
                    table.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select an assignment first.",
                        "No Assignment Selected",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            editAssignmentStatus(
                    table,
                    model,
                    selectedRow
            );
        });

        JPanel buttonPanel = DashboardTheme.buttonBar();
        buttonPanel.add(editButton);
        card.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // LOAD ASSIGNMENTS
    // =========================================================

    private void loadAssignments(
            DefaultTableModel model) {

        model.setRowCount(0);

        try {

            List<String[]> assignments =
                    dashboardService.getAssignments();

            for (String[] assignment : assignments) {
                // The service row starts with the hidden assignment id.
                // The status column shows Assigned, Pending, or Completed.
                model.addRow(new String[]{
                        assignment[1],
                        assignment[2],
                        assignment[3],
                        AssignmentStatus.fromDatabase(assignment[4]).label(),
                        assignment[5]
                });
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // EDIT ASSIGNMENT STATUS
    // =========================================================

    private void editAssignmentStatus(
            JTable table,
            DefaultTableModel model,
            int selectedRow) {

        try {

            /*
             * Current table:
             *
             * 0 = Assignment
             * 1 = Subject
             * 2 = Due Date
             * 3 = Status
             * 4 = Submission Date
             *
             * We need the assignment ID to update the
             * database. The visible table does not contain
             * the ID, so we retrieve the assignments again
             * and match the selected row.
             */

            List<String[]> assignments =
                    dashboardService.getAssignments();

            if (selectedRow >= assignments.size()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to identify the selected assignment.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            /*
             * The StudentDashboardService returns:
             *
             * title
             * subject
             * due date
             * status
             * submission date
             *
             * We therefore need another method that provides
             * the assignment ID.
             */

            int assignmentId =
                    dashboardService.getAssignmentIdForRow(
                            selectedRow
                    );

            String currentStatus =
                    table.getValueAt(
                            selectedRow,
                            3
                    ).toString();

            AssignmentStatus current =
                    AssignmentStatus.fromDatabase(currentStatus);

            AssignmentStatus nextStatus = current.next();

            if (nextStatus == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "This assignment is already completed.",
                        "No Further Update",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            JComboBox<AssignmentStatus> statusCombo =
                    new JComboBox<>();

            statusCombo.addItem(nextStatus);
            statusCombo.setRenderer(
                    new javax.swing.DefaultListCellRenderer() {
                        @Override
                        public java.awt.Component getListCellRendererComponent(
                                JList<?> list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {

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
                    }
            );

            JPanel panel =
                    new JPanel(
                            new GridLayout(
                                    4,
                                    1,
                                    5,
                                    5
                            )
                    );

            panel.add(
                    new JLabel(
                            "Assignment: "
                                    + table.getValueAt(selectedRow, 0)
                    )
            );

            panel.add(
                    new JLabel(
                            "Current status: " + current.label()
                    )
            );

            panel.add(
                    new JLabel("Move status to:")
            );

            panel.add(statusCombo);

            int result =
                    JOptionPane.showConfirmDialog(
                            this,
                            panel,
                            "Edit Assignment Status",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (result !=
                    JOptionPane.OK_OPTION) {

                return;
            }

            AssignmentStatus selectedStatus =
                    (AssignmentStatus) statusCombo
                            .getSelectedItem();

            String newStatus = selectedStatus == null
                    ? null
                    : selectedStatus.name();

            if (newStatus == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a status.",
                        "Invalid Status",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            boolean updated =
                    dashboardService.updateAssignmentStatus(
                            assignmentId,
                            newStatus
                    );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Assignment status updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadAssignments(model);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "The assignment status could not be updated.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update assignment status.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =========================================================
    // TABLE CREATION
    // =========================================================

    private DefaultTableModel createReadOnlyModel(
            String[] columns) {

        return new DefaultTableModel(
                columns,
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };
    }

    private JPanel createTablePanel(JTable table, String emptyMessage) {
        JPanel panel = DashboardTheme.createContentCard();
        panel.add(
                DashboardTheme.tableView(table, emptyMessage),
                BorderLayout.CENTER
        );
        return panel;
    }

    private JPanel createSection(
            String title,
            JTable table,
            String emptyMessage
    ) {
        JPanel panel = DashboardTheme.createContentCard();
        panel.add(DashboardTheme.sectionTitle(title), BorderLayout.NORTH);
        panel.add(
                DashboardTheme.tableView(table, emptyMessage),
                BorderLayout.CENTER
        );
        return panel;
    }

    // =========================================================
    // DATABASE ERROR
    // =========================================================

    private void showDatabaseError(
            SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to load student information. "
                        + "Please check the database connection.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }
}