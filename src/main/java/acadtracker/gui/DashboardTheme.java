package acadtracker.gui;

import acadtracker.gui.theme.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.*;
import java.awt.*;

public final class DashboardTheme {

    public static final Color PAGE = Theme.PAGE;

    public static final Color ACCENT = Theme.ACCENT;

    public static final Color BORDER = Theme.BORDER;

    /** Table headers. */
    public static final Color NAVY = Theme.NAVY;

    /** Primary actions. */
    public static final Color PURPLE = Theme.ACCENT;

    public static final Color BLUE =
            new Color(0x2E, 0x6F, 0xBE);

    public static final Color CYAN = Theme.BLUE;

    public static final Color WHITE =
            new Color(255, 255, 255);

    public static final Color MUTED = Theme.SECONDARY_TEXT;

    public static final Color TEXT = Theme.PRIMARY_TEXT;

    public static final Color LIGHT = PAGE;

    public static final Color SUCCESS =
            new Color(0x1B, 0x9A, 0x62);

    public static final Color WARNING =
            new Color(0xB4, 0x53, 0x09);

    public static final Color DANGER =
            new Color(0xBE, 0x3A, 0x4A);

    private static final Color ROW_ALT =
            new Color(0xF4, 0xF5, 0xFA);

    private static final Color ROW_SELECTED =
            new Color(0xE8, 0xE6, 0xF6);

    private static final Font UI_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    private static final Font UI_FONT_BOLD =
            new Font("Segoe UI", Font.BOLD, 13);

    private DashboardTheme() {
    }

    public static JPanel createBackground() {
        return new GradientBackground();
    }

    public static JPanel createGlassPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    public static JPanel createContentCard() {
        return createContentCard(12);
    }

    public static JPanel createContentCard(int cornerRadius) {
        JPanel panel = new ContentCard(cornerRadius);
        panel.setLayout(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        return panel;
    }

    public static JPanel createHeader(
            String name,
            String role,
            String detailLine,
            JButton logout
    ) {
        JPanel header = createGlassPanel();
        header.setLayout(new BorderLayout());

        JLabel heading = new JLabel(
                "Welcome back, " + name + "    ·    " + role
        );
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(WHITE);

        JLabel details = new JLabel(detailLine);
        details.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        details.setForeground(Theme.ON_DARK_MUTED);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setOpaque(false);
        text.add(heading);
        text.add(details);

        styleSecondaryButton(logout);
        header.add(text, BorderLayout.CENTER);
        header.add(logout, BorderLayout.EAST);
        return header;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UI_FONT_BOLD);
        label.setForeground(TEXT);
        return label;
    }

    public static JPanel buttonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bar.setOpaque(false);
        return bar;
    }

    public static void styleTabs(JTabbedPane tabs) {
        tabs.setFont(UI_FONT_BOLD);
        tabs.setForeground(TEXT);
        tabs.setBackground(PAGE);
        tabs.setOpaque(false);
        tabs.setBorder(new EmptyBorder(4, 0, 0, 0));
        tabs.setUI(new DashboardTabUI());
    }

    public static void styleTable(JTable table) {
        table.setFont(UI_FONT);
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(TEXT);

        JTableHeader header = table.getTableHeader();
        header.setFont(UI_FONT_BOLD);
        header.setBackground(NAVY);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(null);
        header.setDefaultRenderer(new HeaderRenderer());

        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
    }

    public static JPanel tableView(JTable table, String emptyMessage) {
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);

        JLabel empty = new JLabel(emptyMessage, SwingConstants.CENTER);
        empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        empty.setForeground(MUTED);

        JPanel emptyWrap = new JPanel(new GridBagLayout());
        emptyWrap.setBackground(WHITE);
        emptyWrap.add(empty);

        CardLayout cards = new CardLayout();
        JPanel stack = new JPanel(cards);
        stack.setOpaque(false);
        stack.add(scroll, "table");
        stack.add(emptyWrap, "empty");

        TableModelListener refresh = event -> showTableOrEmpty(
                cards,
                stack,
                table
        );
        table.getModel().addTableModelListener(refresh);
        showTableOrEmpty(cards, stack, table);
        return stack;
    }

    private static void showTableOrEmpty(
            CardLayout cards,
            JPanel stack,
            JTable table
    ) {
        cards.show(stack, table.getRowCount() == 0 ? "empty" : "table");
    }

    public static void colorStatusColumn(JTable table, int column) {
        table.getColumnModel()
                .getColumn(column)
                .setCellRenderer(new StatusToneRenderer());
    }

    public static void colorCountColumn(
            JTable table,
            int column,
            Color color
    ) {
        table.getColumnModel()
                .getColumn(column)
                .setCellRenderer(new ToneRenderer(color));
    }

    public static Color statusColor(String value) {
        if (value == null || value.isBlank()) {
            return TEXT;
        }

        return switch (value.trim().toUpperCase()) {
            case "PRESENT", "COMPLETED" -> SUCCESS;
            case "ABSENT" -> DANGER;
            case "LEAVE", "PENDING", "IN_PROGRESS" -> WARNING;
            case "ASSIGNED" -> BLUE;
            default -> TEXT;
        };
    }

    public static void styleButton(JButton button) {
        button.setFont(UI_FONT_BOLD);
        button.setForeground(WHITE);
        button.setBackground(PURPLE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(ACCENT, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(UI_FONT_BOLD);
        button.setForeground(TEXT);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(UI_FONT);
        combo.setBackground(WHITE);
        combo.setForeground(TEXT);
        combo.setPreferredSize(new Dimension(180, 36));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(UI_FONT);
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(new LineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setOpaque(false);
    }

    private static void paintRow(
            JComponent component,
            boolean selected,
            int row,
            Color foreground
    ) {
        if (selected) {
            component.setBackground(ROW_SELECTED);
            component.setForeground(TEXT);
        } else {
            component.setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
            component.setForeground(foreground);
        }
        component.setOpaque(true);
    }

    public static class GradientBackground extends JPanel {
        public GradientBackground() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );
            int width = getWidth();
            int height = getHeight();
            Color middle = new Color(
                    (Theme.PAGE.getRed() + Theme.WASH.getRed()) / 2,
                    (Theme.PAGE.getGreen() + Theme.WASH.getGreen()) / 2,
                    (Theme.PAGE.getBlue() + Theme.WASH.getBlue()) / 2
            );
            g2.setPaint(new GradientPaint(
                    0, 0, Theme.PAGE,
                    width / 2f, height, middle
            ));
            g2.fillRect(0, 0, width / 2, height);
            g2.setPaint(new GradientPaint(
                    width / 2f, height, middle,
                    width, 0, Theme.WASH
            ));
            g2.fillRect(width / 2, 0, width - width / 2, height);
            g2.dispose();
        }
    }

    public static class GlassPanel extends JPanel {
        public GlassPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            int arc = 18;
            int w = getWidth() - 2;
            int h = getHeight() - 3;
            g2.setColor(new Color(255, 255, 255, 36));
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w, h, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class ContentCard extends JPanel {
        private final int arc;

        private ContentCard(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            int w = getWidth() - 2;
            int h = getHeight() - 3;
            g2.setColor(new Color(30, 41, 59, 14));
            g2.fillRoundRect(2, 3, w, h, arc, arc);
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, w, h, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class DashboardTabUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(8, 16, 8, 16);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            tabAreaInsets = new Insets(6, 4, 0, 4);
            contentBorderInsets = new Insets(8, 0, 0, 0);
        }

        @Override
        protected void paintTabBackground(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x,
                int y,
                int w,
                int h,
                boolean isSelected
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(isSelected ? WHITE : new Color(0xE4, 0xE2, 0xF0));
            g2.fillRoundRect(x + 3, y + 4, w - 6, h - 2, 12, 12);
            if (isSelected) {
                g2.setColor(BORDER);
                g2.drawRoundRect(x + 3, y + 4, w - 6, h - 2, 12, 12);
            }
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x,
                int y,
                int w,
                int h,
                boolean isSelected
        ) {
        }

        @Override
        protected void paintContentBorder(
                Graphics g,
                int tabPlacement,
                int selectedIndex
        ) {
        }

        @Override
        protected void paintFocusIndicator(
                Graphics g,
                int tabPlacement,
                Rectangle[] rects,
                int tabIndex,
                Rectangle iconRect,
                Rectangle textRect,
                boolean isSelected
        ) {
        }

        @Override
        protected void paintText(
                Graphics g,
                int tabPlacement,
                Font font,
                FontMetrics metrics,
                int tabIndex,
                String title,
                Rectangle textRect,
                boolean isSelected
        ) {
            g.setFont(font);
            g.setColor(isSelected ? TEXT : new Color(0x2A, 0x31, 0x55));
            g.drawString(
                    title,
                    textRect.x,
                    textRect.y + metrics.getAscent()
            );
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column
        ) {
            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
            );
            setBackground(NAVY);
            setForeground(WHITE);
            setFont(UI_FONT_BOLD);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            setOpaque(true);
            return this;
        }
    }

    private static class AlternatingRowRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
            );
            if (component instanceof JComponent rendered) {
                paintRow(rendered, selected, row, TEXT);
                rendered.setBorder(new EmptyBorder(5, 10, 5, 10));
                rendered.setFont(UI_FONT);
            }
            return component;
        }
    }

    private static final class StatusToneRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
            );
            if (component instanceof JComponent rendered) {
                String text = value == null ? "" : value.toString();
                paintRow(rendered, selected, row, statusColor(text));
                rendered.setBorder(new EmptyBorder(5, 10, 5, 10));
                rendered.setFont(UI_FONT_BOLD);
            }
            return component;
        }
    }

    private static final class ToneRenderer extends DefaultTableCellRenderer {
        private final Color color;

        private ToneRenderer(Color color) {
            this.color = color;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
            );
            if (component instanceof JComponent rendered) {
                paintRow(rendered, selected, row, color);
                rendered.setBorder(new EmptyBorder(5, 10, 5, 10));
                rendered.setFont(UI_FONT_BOLD);
            }
            return component;
        }
    }
}
