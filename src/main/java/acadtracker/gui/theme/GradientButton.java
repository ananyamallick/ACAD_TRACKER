
package acadtracker.gui.theme;

import javax.swing.JButton;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GradientButton extends JButton {

    private final Color startColor;
    private final Color endColor;

    private boolean hovered = false;

    public GradientButton(
            String text,
            Color startColor,
            Color endColor
    ) {

        super(text);

        this.startColor = startColor;
        this.endColor = endColor;

        setFont(Theme.BUTTON_FONT);
        setForeground(Color.WHITE);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setOpaque(false);
        setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        setBorder(
                javax.swing.BorderFactory.createEmptyBorder(
                        8, 16, 8, 16
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

        Color first = hovered
                ? endColor.brighter()
                : startColor;

        Color second = hovered
                ? startColor.brighter()
                : endColor;

        GradientPaint gradient = new GradientPaint(
                0, 0, first,
                getWidth(), getHeight(), second
        );

        g2.setPaint(gradient);

        g2.fillRoundRect(
                0, 0,
                getWidth(), getHeight(),
                12, 12
        );

        g2.dispose();

        super.paintComponent(g);
    }
}
