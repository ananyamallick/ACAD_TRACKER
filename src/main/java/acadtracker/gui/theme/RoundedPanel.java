package acadtracker.gui.theme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedPanel extends JPanel {

    private final int radius;
    private final Color backgroundColor;

    public RoundedPanel(int radius, Color backgroundColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int arc = Math.min(radius, 12);
        int w = getWidth() - 2;
        int h = getHeight() - 3;

        g2.setColor(new Color(30, 41, 59, 18));
        g2.fillRoundRect(2, 3, w, h, arc, arc);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        g2.setColor(Theme.BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w, h, arc, arc);

        g2.dispose();

        super.paintComponent(g);
    }
}