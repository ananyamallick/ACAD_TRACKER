package acadtracker.gui;

import acadtracker.exceptions.AuthenticationException;
import acadtracker.gui.theme.GradientButton;
import acadtracker.gui.theme.GradientPanel;
import acadtracker.gui.theme.ModernPasswordField;
import acadtracker.gui.theme.ModernTextField;
import acadtracker.gui.theme.RoundedPanel;
import acadtracker.gui.theme.Theme;
import acadtracker.model.AuthenticatedUser;
import acadtracker.service.AuthService;

import javax.swing.*;
import java.awt.*;
// import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private JButton loginButton;
    private CardLayout authCardLayout;
    private JPanel authCardPanel;
    private final AuthService authService;

    public LoginFrame() {

        authService = new AuthService();

        initializeFrame();
        createUI();
    }

    private void initializeFrame() {

        setTitle("ACAD TRACKER");

        setSize(1050, 650);

        setMinimumSize(
                new Dimension(900, 580)
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(true);
    }

    private void createUI() {

        GradientPanel background =
                new GradientPanel(
                        Theme.PAGE,
                        Theme.WASH
                );

        background.setLayout(
                new GridBagLayout()
        );

        JPanel content =
                new JPanel(
                        new GridBagLayout()
                );

        content.setOpaque(false);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(20, 30, 20, 30);

        gbc.fill =
                GridBagConstraints.BOTH;

        gbc.weightx = 1;
        gbc.weighty = 1;

        /*
         * LEFT BRANDING PANEL
         */

        JPanel brandingPanel =
                createBrandingPanel();

        gbc.gridx = 0;
        gbc.gridy = 0;

        content.add(
                brandingPanel,
                gbc
        );

        /*
         * RIGHT LOGIN CARD
         */

        authCardLayout = new CardLayout();
        authCardPanel = new JPanel(authCardLayout);
        authCardPanel.setOpaque(false);
        authCardPanel.add(createLoginCard(), "LOGIN");
        authCardPanel.add(new SignUpFrame(this::showLogin), "SIGNUP");

        gbc.gridx = 1;
        content.add(authCardPanel, gbc);

        background.add(content);

        setContentPane(background);

        setupKeyboardShortcuts();
    }

    private JPanel createBrandingPanel() {

        JPanel panel =
                new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel logo =
                new JLabel("A");

        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        logo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        logo.setVerticalAlignment(
                SwingConstants.CENTER
        );

        logo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        48
                )
        );

        logo.setForeground(Color.WHITE);

        logo.setPreferredSize(
                new Dimension(85, 85)
        );

        logo.setMaximumSize(
                new Dimension(85, 85)
        );

        logo.setOpaque(true);

        logo.setBackground(Theme.LOGO_TILE);

        panel.add(logo);

        panel.add(
                Box.createVerticalStrut(30)
        );

        JLabel title =
                new JLabel("ACAD TRACKER");

        title.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        38
                )
        );

        title.setForeground(Theme.ON_DARK);

        panel.add(title);

        panel.add(
                Box.createVerticalStrut(12)
        );

        JLabel subtitle =
                new JLabel(
                        "<html>"
                        + "Attendance &amp; Assignment<br>"
                        + "Management System"
                        + "</html>"
                );

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        18
                )
        );

        subtitle.setForeground(Theme.ON_DARK_MUTED);

        panel.add(subtitle);

        panel.add(
                Box.createVerticalStrut(35)
        );

        JLabel description =
                new JLabel(
                        "<html>"
                        + "<div style='width:330px;'>"
                        + "A centralized academic platform "
                        + "for managing student attendance, "
                        + "assignments and academic progress."
                        + "</div>"
                        + "</html>"
                );

        description.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        description.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        description.setForeground(Theme.ON_DARK_MUTED);

        panel.add(description);

        panel.add(
                Box.createVerticalGlue()
        );

        JLabel footer =
                new JLabel(
                        "SMART • SIMPLE • ACADEMIC"
                );

        footer.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        footer.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        footer.setForeground(Theme.ON_DARK_MUTED);

        panel.add(footer);

        return panel;
    }

    private JPanel createLoginCard() {

        RoundedPanel card =
                new RoundedPanel(12, Color.WHITE);

        card.setLayout(
                new GridBagLayout()
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        35,
                        40,
                        35,
                        40
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(
                        7,
                        0,
                        7,
                        0
                );

        /*
         * LOGIN TITLE
         */

        JLabel title =
                new JLabel("Welcome Back");

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(
                Theme.PRIMARY_TEXT
        );

        gbc.gridy = 0;

        card.add(
                title,
                gbc
        );

        /*
         * LOGIN SUBTITLE
         */

        JLabel subtitle =
                new JLabel(
                        "Sign in to continue to your dashboard"
                );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        subtitle.setForeground(
                Theme.SECONDARY_TEXT
        );

        gbc.gridy = 1;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        25,
                        0
                );

        card.add(
                subtitle,
                gbc
        );

        /*
         * EMAIL LABEL
         */

        JLabel emailLabel =
                createFieldLabel(
                        "EMAIL ADDRESS"
                );

        gbc.gridy = 2;

        gbc.insets =
                new Insets(
                        5,
                        0,
                        6,
                        0
                );

        card.add(
                emailLabel,
                gbc
        );

        /*
         * EMAIL FIELD
         */

        emailField =
                new ModernTextField();

        emailField.setPreferredSize(
                new Dimension(
                        320,
                        45
                )
        );

        emailField.setToolTipText(
                "Enter your registered email address"
        );

        gbc.gridy = 3;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        15,
                        0
                );

        card.add(
                emailField,
                gbc
        );

        /*
         * PASSWORD LABEL
         */

        JLabel passwordLabel =
                createFieldLabel(
                        "PASSWORD"
                );

        gbc.gridy = 4;

        gbc.insets =
                new Insets(
                        5,
                        0,
                        6,
                        0
                );

        card.add(
                passwordLabel,
                gbc
        );

        /*
         * PASSWORD FIELD
         */

        passwordField =
                new ModernPasswordField();

        passwordField.setPreferredSize(
                new Dimension(
                        320,
                        45
                )
        );

        passwordField.setToolTipText(
                "Enter your password"
        );

        gbc.gridy = 5;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        22,
                        0
                );

        card.add(
                passwordField,
                gbc
        );

        /*
         * LOGIN BUTTON
         */

        loginButton =
        new GradientButton(
                "SIGN IN",
                Theme.BLUE,
                Theme.ROYAL_PURPLE
        );

        loginButton.setPreferredSize(
                new Dimension(
                        320,
                        48
                )
        );

        loginButton.addActionListener(
                e -> login()
        );

        gbc.gridy = 6;

        gbc.insets =
                new Insets(
                        5,
                        0,
                        18,
                        0
                );

        card.add(
                loginButton,
                gbc
        );

        /*
         * ROLE INFORMATION
         */

        JLabel roleLabel =
                new JLabel(
                        "Student & Faculty access"
                );

        roleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        roleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        roleLabel.setForeground(
                Theme.SECONDARY_TEXT
        );

        gbc.gridy = 7;

        gbc.insets =
                new Insets(
                        5,
                        0,
                        0,
                        0
                );

        card.add(
                roleLabel,
                gbc
        );

        JButton signUpButton =
                new JButton("Create an account");

        signUpButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        signUpButton.setForeground(Theme.ROYAL_PURPLE);
        signUpButton.setContentAreaFilled(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        signUpButton.addActionListener(e -> showSignUp());

        gbc.gridy = 8;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        0,
                        0
                );

        card.add(
                signUpButton,
                gbc
        );

        return card;
    }


    private void showSignUp() {
        authCardLayout.show(authCardPanel, "SIGNUP");
        getRootPane().setDefaultButton(null);
        authCardPanel.revalidate();
        authCardPanel.repaint();
    }

    private void showLogin() {
        authCardLayout.show(authCardPanel, "LOGIN");
        getRootPane().setDefaultButton(loginButton);
        authCardPanel.revalidate();
        authCardPanel.repaint();
    }

    private JLabel createFieldLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        label.setForeground(
                Theme.SECONDARY_TEXT
        );

        return label;
    }

    private void setupKeyboardShortcuts() {

        getRootPane().setDefaultButton(
                loginButton
        );
    }

    // private JButton findLoginButton() {

    //     JButton button =
    //             new GradientButton(
    //                     "SIGN IN",
    //                     Theme.BLUE,
    //                     Theme.ROYAL_PURPLE
    //             );

    //     /*
    //      * This button is only used as the
    //      * root-pane default button.
    //      *
    //      * The visible login button already
    //      * performs the actual login action.
    //      */

    //     button.addActionListener(
    //             e -> login()
    //     );

    //     return button;
    // }

    private void login() {

        String email =
                emailField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        if (email.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your email address.",
                    "Missing Email",
                    JOptionPane.WARNING_MESSAGE
            );

            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your password.",
                    "Missing Password",
                    JOptionPane.WARNING_MESSAGE
            );

            passwordField.requestFocus();
            return;
        }

        try {

            AuthenticatedUser user =
                    authService.login(
                            email,
                            password
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, "
                            + user.getName()
                            + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if ("STUDENT".equals(
                    user.getRole()
            )) {

                new StudentDashboardFrame(
                        user
                ).setVisible(true);

            } else if ("FACULTY".equals(
                    user.getRole()
            )) {

                new FacultyDashboardFrame(
                        user
                ).setVisible(true);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Your account has an unsupported role.",
                        "Access Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            dispose();

        } catch (AuthenticationException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Login Failed",
                    JOptionPane.WARNING_MESSAGE
            );

            passwordField.setText("");
            passwordField.requestFocus();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to the database. "
                            + "Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }
}

