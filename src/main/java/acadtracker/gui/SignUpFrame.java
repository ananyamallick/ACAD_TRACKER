package acadtracker.gui;

import acadtracker.exceptions.DuplicateEmailException;
import acadtracker.gui.theme.GradientButton;
import acadtracker.gui.theme.ModernPasswordField;
import acadtracker.gui.theme.ModernTextField;
import acadtracker.gui.theme.RoundedPanel;
import acadtracker.gui.theme.Theme;
import acadtracker.service.RegistrationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Registration view embedded inside LoginFrame's authentication card. */
public class SignUpFrame extends JPanel {
    private final RegistrationService registrationService = new RegistrationService();
    private final Runnable onSignIn;

    private ModernTextField nameField, emailField, facultySubjectField, facultyCodeField;
    private ModernPasswordField passwordField, confirmField;
    private JButton showPasswordButton, createButton;
    private JLabel strengthLabel, validationLabel;
    private JProgressBar strengthBar;
    private JToggleButton studentRole, facultyRole;
    private JPanel studentSection, facultySection, subjectPanel;
    private final List<JCheckBox> subjectBoxes = new ArrayList<>();

    public SignUpFrame(Runnable onSignIn) {
        this.onSignIn = onSignIn;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(createCard(), BorderLayout.CENTER);
        loadSubjects();
    }

    private JPanel createCard() {
        RoundedPanel card = new RoundedPanel(30, new Color(255, 255, 255, 245));
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(25, 38, 22, 38));
        card.add(createHeading(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(createForm());
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        card.add(scroll, BorderLayout.CENTER);
        card.add(createActions(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel createHeading() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.PRIMARY_TEXT);
        JLabel subtitle = new JLabel("Register to continue to your academic dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Theme.SECONDARY_TEXT);
        p.add(title);
        p.add(Box.createVerticalStrut(5));
        p.add(subtitle);
        return p;
    }

    private JPanel createForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(5, 2, 10, 8));

        nameField = new ModernTextField();
        emailField = new ModernTextField();
        passwordField = new ModernPasswordField();
        confirmField = new ModernPasswordField();

        form.add(fieldLabel("REGISTER AS"));
        form.add(Box.createVerticalStrut(7));
        form.add(createRoleSelector());
        form.add(Box.createVerticalStrut(13));
        addField(form, "FULL NAME", nameField);
        addField(form, "EMAIL ADDRESS", emailField);

        form.add(fieldLabel("PASSWORD"));
        form.add(Box.createVerticalStrut(5));
        form.add(createPasswordRow());
        form.add(Box.createVerticalStrut(5));
        form.add(createStrengthPanel());
        form.add(Box.createVerticalStrut(10));
        addField(form, "CONFIRM PASSWORD", confirmField);

        studentSection = createStudentSection();
        facultySection = createFacultySection();
        form.add(studentSection);
        form.add(facultySection);

        validationLabel = new JLabel(" ");
        validationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        validationLabel.setForeground(Theme.DANGER);
        validationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(7));
        form.add(validationLabel);

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
        });
        return form;
    }

    private JPanel createRoleSelector() {
        JPanel p = new JPanel(new GridLayout(1, 2, 7, 0));
        p.setOpaque(true);
        p.setBackground(new Color(244, 245, 249));
        p.setBorder(new EmptyBorder(4, 4, 4, 4));
        studentRole = new JToggleButton("I'm a Student");
        facultyRole = new JToggleButton("I'm Faculty");
        ButtonGroup group = new ButtonGroup();
        group.add(studentRole); group.add(facultyRole);
        studentRole.setSelected(true);
        styleRole(studentRole); styleRole(facultyRole);
        studentRole.addActionListener(e -> updateRoleSections());
        facultyRole.addActionListener(e -> updateRoleSections());
        p.add(studentRole); p.add(facultyRole);
        return p;
    }

    private void styleRole(JToggleButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 8, 10, 8));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addChangeListener(e -> {
            b.setOpaque(true);
            b.setBackground(b.isSelected() ? Color.WHITE : new Color(244,245,249));
            b.setForeground(b.isSelected() ? Theme.ROYAL_PURPLE : Theme.SECONDARY_TEXT);
        });
    }

    private JPanel createPasswordRow() {
        JPanel row = new JPanel(new BorderLayout(7, 0));
        row.setOpaque(false);
        showPasswordButton = new JButton("SHOW");
        showPasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
        showPasswordButton.setForeground(Theme.ROYAL_PURPLE);
        showPasswordButton.setBackground(new Color(247,248,253));
        showPasswordButton.setBorder(BorderFactory.createLineBorder(new Color(220,224,238)));
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.addActionListener(e -> togglePasswordVisibility());
        row.add(passwordField, BorderLayout.CENTER);
        row.add(showPasswordButton, BorderLayout.EAST);
        return row;
    }

    private JPanel createStrengthPanel() {
        JPanel p = new JPanel(new BorderLayout(8,0));
        p.setOpaque(false);
        strengthBar = new JProgressBar(0,4);
        strengthBar.setPreferredSize(new Dimension(100,6));
        strengthBar.setBorderPainted(false);
        strengthLabel = new JLabel("Password strength: —");
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        strengthLabel.setForeground(Theme.SECONDARY_TEXT);
        p.add(strengthBar, BorderLayout.CENTER);
        p.add(strengthLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel createStudentSection() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fieldLabel("SELECT YOUR SUBJECTS"));
        JLabel hint = new JLabel("Choose one or more subjects to enroll in.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hint.setForeground(Theme.SECONDARY_TEXT);
        p.add(Box.createVerticalStrut(4)); p.add(hint); p.add(Box.createVerticalStrut(6));
        subjectPanel = new JPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));
        subjectPanel.setBackground(new Color(247,248,253));
        JScrollPane scroll = new JScrollPane(subjectPanel);
        scroll.setPreferredSize(new Dimension(350, 100));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,224,238)));
        p.add(scroll);
        return p;
    }

    private JPanel createFacultySection() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fieldLabel("SUBJECT DETAILS"));
        p.add(Box.createVerticalStrut(6));
        JPanel pair = new JPanel(new GridLayout(1,2,9,0));
        pair.setOpaque(false);
        facultySubjectField = new ModernTextField();
        facultyCodeField = new ModernTextField();
        facultyCodeField.setToolTipText("Example: CS-101");
        pair.add(fieldGroup("SUBJECT TAUGHT", facultySubjectField));
        pair.add(fieldGroup("SUBJECT CODE", facultyCodeField));
        p.add(pair);
        p.setVisible(false);
        return p;
    }

    private JPanel fieldGroup(String label, JComponent field) {
        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fieldLabel(label)); p.add(Box.createVerticalStrut(5));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43)); p.add(field);
        return p;
    }

    private JPanel createActions() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        createButton = new GradientButton("CREATE ACCOUNT", Theme.BLUE, Theme.ROYAL_PURPLE);
        createButton.setPreferredSize(new Dimension(320,47));
        createButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,47));
        createButton.addActionListener(e -> createAccount());
        JButton signIn = new JButton("Already have an account? Sign in");
        signIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signIn.setForeground(Theme.ROYAL_PURPLE);
        signIn.setContentAreaFilled(false); signIn.setBorderPainted(false); signIn.setFocusPainted(false);
        signIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signIn.addActionListener(e -> onSignIn.run());
        p.add(createButton); p.add(Box.createVerticalStrut(7)); p.add(signIn);
        return p;
    }

    private void addField(JPanel parent, String label, JComponent field) {
        parent.add(fieldLabel(label)); parent.add(Box.createVerticalStrut(5));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,43)); parent.add(field);
        parent.add(Box.createVerticalStrut(10));
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(Theme.PRIMARY_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void togglePasswordVisibility() {
        char echo = passwordField.getEchoChar();
        if (echo == 0) { passwordField.setEchoChar('•'); showPasswordButton.setText("SHOW"); }
        else { passwordField.setEchoChar((char)0); showPasswordButton.setText("HIDE"); }
    }

    private void updatePasswordStrength() {
        String value = new String(passwordField.getPassword());
        int score = 0;
        if (value.length() >= 8) score++;
        if (value.matches(".*[A-Z].*")) score++;
        if (value.matches(".*[a-z].*")) score++;
        if (value.matches(".*[0-9].*") && value.matches(".*[^A-Za-z0-9].*")) score++;
        strengthBar.setValue(score);
        String[] names = {"Very weak", "Weak", "Fair", "Good", "Strong"};
        strengthLabel.setText("Password strength: " + (value.isEmpty() ? "—" : names[score]));
        strengthBar.setForeground(score <= 1 ? Theme.DANGER : score == 2 ? Theme.WARNING : Theme.SUCCESS);
    }

    private void updateRoleSections() {
        boolean isStudent = studentRole.isSelected();
        studentSection.setVisible(isStudent);
        facultySection.setVisible(!isStudent);
        validationLabel.setText(" ");
        revalidate(); repaint();
    }

    private void loadSubjects() {
        subjectPanel.removeAll(); subjectBoxes.clear();
        try {
            List<String[]> subjects = registrationService.getSubjects();
            if (subjects.isEmpty()) {
                JLabel empty = new JLabel("No subjects are currently available.");
                empty.setBorder(new EmptyBorder(7,8,7,8));
                subjectPanel.add(empty);
            }
            for (String[] subject : subjects) {
                JCheckBox box = new JCheckBox(subject[1] + " (" + subject[2] + ")");
                box.putClientProperty("subjectId", Integer.parseInt(subject[0]));
                box.setOpaque(false); box.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                box.setForeground(Theme.PRIMARY_TEXT); box.setBorder(new EmptyBorder(4,8,4,8));
                subjectBoxes.add(box); subjectPanel.add(box);
            }
        } catch (SQLException ex) { showInlineError("Unable to load subjects: " + ex.getMessage()); }
        subjectPanel.revalidate(); subjectPanel.repaint();
    }

    private void createAccount() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String role = studentRole.isSelected() ? "STUDENT" : "FACULTY";
        if (name.length() < 2) { showInlineError("Please enter your full name."); nameField.requestFocus(); return; }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) { showInlineError("Please enter a valid email address."); emailField.requestFocus(); return; }
        if (password.length() < 8) { showInlineError("Password must contain at least 8 characters."); passwordField.requestFocus(); return; }
        if (!password.equals(confirm)) { showInlineError("Passwords do not match."); confirmField.requestFocus(); return; }

        List<Integer> selectedIds = new ArrayList<>();
        List<String[]> newSubjects = new ArrayList<>();
        if ("STUDENT".equals(role)) {
            for (JCheckBox box : subjectBoxes) if (box.isSelected()) selectedIds.add((Integer)box.getClientProperty("subjectId"));
            if (selectedIds.isEmpty()) { showInlineError("Please select at least one subject."); return; }
        } else {
            String subjectName = facultySubjectField.getText().trim();
            String subjectCode = facultyCodeField.getText().trim().toUpperCase();
            if (subjectName.length() < 2) { showInlineError("Enter the subject you teach."); facultySubjectField.requestFocus(); return; }
            if (!subjectCode.matches("[A-Z0-9-]{2,20}")) { showInlineError("Enter a valid subject code, e.g. CS-101."); facultyCodeField.requestFocus(); return; }
            newSubjects.add(new String[]{subjectName, subjectCode});
        }

        createButton.setEnabled(false); createButton.setText("CREATING ACCOUNT...");
        try {
            String result = registrationService.register(name,email,password,role,selectedIds,newSubjects);
            JOptionPane.showMessageDialog(this,result,"Account created",JOptionPane.INFORMATION_MESSAGE);
            onSignIn.run();
        } catch (DuplicateEmailException | IllegalArgumentException ex) {
            showInlineError(ex.getMessage());
        } catch (SQLException ex) {
            showInlineError("Could not create account: " + ex.getMessage());
        } finally {
            createButton.setEnabled(true); createButton.setText("CREATE ACCOUNT");
        }
    }

    private void showInlineError(String message) { validationLabel.setText("<html>" + message + "</html>"); }
}
