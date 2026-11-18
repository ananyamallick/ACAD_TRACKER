package acadtracker.gui.theme;

import javax.swing.JPasswordField;
import javax.swing.BorderFactory;

import java.awt.Insets;

public class ModernPasswordField extends JPasswordField {

    public ModernPasswordField() {

        setFont(Theme.NORMAL_FONT);

        setForeground(Theme.PRIMARY_TEXT);

        setBackground(Theme.INPUT_BG);

        setCaretColor(Theme.ACCENT);

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                )
        );

        setMargin(new Insets(0, 0, 0, 0));
    }
}