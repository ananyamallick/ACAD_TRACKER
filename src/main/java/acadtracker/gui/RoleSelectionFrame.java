package acadtracker.gui;

import javax.swing.*;
import java.awt.*;

public class RoleSelectionFrame extends JFrame {

    public RoleSelectionFrame() {

        setTitle("ACAD TRACKER - Select Dashboard");
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JLabel title = new JLabel(
                "Select Your Dashboard",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton studentButton = new JButton("Student Dashboard");
        JButton facultyButton = new JButton("Faculty Dashboard");
        DashboardTheme.styleButton(studentButton);
        DashboardTheme.styleSecondaryButton(facultyButton);

        panel.add(title);
        panel.add(studentButton);
        panel.add(facultyButton);

        JPanel background = DashboardTheme.createBackground();
        background.setLayout(new BorderLayout());
        background.add(panel, BorderLayout.CENTER);
        setContentPane(background);
    }
}
