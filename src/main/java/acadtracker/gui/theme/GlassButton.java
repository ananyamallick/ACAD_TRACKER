
package acadtracker.gui.theme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GlassButton extends JButton {

    private boolean hovered = false;

    public GlassButton(String text) {

        super(text);

        setForeground(Color.WHITE);
        setFont(Theme.NORMAL_FONT);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setHorizontalAlignment(LEFT);

        setBorder(
                javax.swing.BorderFactory.createEmptyBorder(
                        12, 18, 12, 18
                )
        );

        setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (hovered || getModel().isSelected()) {

            g2.setColor(
                    new Color(255, 255, 255, 55)
            );

            g2.fillRoundRect(
                    0, 0,
                    getWidth(),
                    getHeight(),
                    16, 16
            );
        }

        g2.dispose();

        super.paintComponent(g);
    }
}
