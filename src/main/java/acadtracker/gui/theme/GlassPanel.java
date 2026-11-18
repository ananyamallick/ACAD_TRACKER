
package acadtracker.gui.theme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GlassPanel extends JPanel {

    private final int radius;
    private final Color glassColor;

    public GlassPanel(int radius, Color glassColor) {

        this.radius = radius;
        this.glassColor = glassColor;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(glassColor);

        g2.fillRoundRect(
                0, 0,
                getWidth(), getHeight(),
                radius, radius
        );

        g2.setColor(
                new Color(255, 255, 255, 65)
        );

        g2.drawRoundRect(
                0, 0,
                getWidth() - 1,
                getHeight() - 1,
                radius, radius
        );

        g2.dispose();

        super.paintComponent(g);
    }
}
