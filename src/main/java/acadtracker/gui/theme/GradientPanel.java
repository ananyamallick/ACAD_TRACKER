
package acadtracker.gui.theme;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

public class GradientPanel extends JPanel {

    private final Color startColor;
    private final Color middleColor;
    private final Color endColor;

    public GradientPanel(Color startColor, Color endColor) {

        this(
                startColor,
                blend(startColor, endColor),
                endColor
        );
    }

    public GradientPanel(
            Color startColor,
            Color middleColor,
            Color endColor
    ) {

        this.startColor = startColor;
        this.middleColor = middleColor;
        this.endColor = endColor;

        setOpaque(false);
    }

    private static Color blend(Color start, Color end) {
        return new Color(
                (start.getRed() + end.getRed()) / 2,
                (start.getGreen() + end.getGreen()) / 2,
                (start.getBlue() + end.getBlue()) / 2
        );
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        GradientPaint first = new GradientPaint(
                0, 0,
                startColor,
                width / 2, height,
                middleColor
        );

        g2.setPaint(first);

        g2.fillRect(
                0, 0,
                width / 2,
                height
        );

        GradientPaint second = new GradientPaint(
                width / 2, height,
                middleColor,
                width, 0,
                endColor
        );

        g2.setPaint(second);

        g2.fillRect(
                width / 2, 0,
                width / 2,
                height
        );

        g2.dispose();

        super.paintComponent(g);
    }
}
