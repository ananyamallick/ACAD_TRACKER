package acadtracker.gui.theme;

import java.awt.Color;
import java.awt.Font;

public class Theme {

    /** Top-left of the navy-to-violet page gradient. */
    public static final Color PAGE =
            new Color(0x12, 0x1A, 0x48);

    /** Bottom-right of the navy-to-violet page gradient. */
    public static final Color WASH =
            new Color(0x4E, 0x45, 0xC4);

    public static final Color ACCENT =
            new Color(0x40, 0x35, 0x9C);

    public static final Color BORDER =
            new Color(0xE2, 0xE8, 0xF0);

    public static final Color INPUT_BG =
            new Color(0xF8, 0xFA, 0xFC);

    public static final Color NAVY =
            new Color(0x0E, 0x16, 0x36);

    public static final Color DEEP_PURPLE = PAGE;

    public static final Color ROYAL_PURPLE =
            new Color(0x4E, 0x4A, 0xC4);

    public static final Color BLUE =
            new Color(0x26, 0x82, 0xD8);

    public static final Color CYAN = BLUE;

    public static final Color LIGHT_BACKGROUND = PAGE;

    public static final Color CARD_BACKGROUND =
            Color.WHITE;

    public static final Color PRIMARY_TEXT =
            new Color(0x1A, 0x21, 0x40);

    public static final Color SECONDARY_TEXT =
            new Color(0x6B, 0x72, 0x88);

    public static final Color ON_DARK = Color.WHITE;

    public static final Color ON_DARK_MUTED =
            new Color(214, 218, 240);

    public static final Color LOGO_TILE =
            new Color(54, 62, 108);

    public static final Color WHITE =
            Color.WHITE;

    public static final Color SUCCESS =
            new Color(0x1B, 0x9A, 0x62);

    public static final Color WARNING =
            new Color(0xB4, 0x53, 0x09);

    public static final Color DANGER =
            new Color(0xBE, 0x3A, 0x4A);

    public static final Font HEADING_FONT =
            new Font("Segoe UI", Font.BOLD, 22);

    public static final Font SUBHEADING_FONT =
            new Font("Segoe UI", Font.BOLD, 16);

    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 13);

    public static final Color GLASS_WHITE =
            new Color(255, 255, 255, 230);

    public static final Color GLASS_BORDER = BORDER;

    public static final Color GLASS_HOVER =
            new Color(0xF4, 0xF6, 0xF8);

    public static final Color MUTED_WHITE = SECONDARY_TEXT;

    public static final Color SOFT_CYAN = CYAN;

    public static final Color SOFT_PURPLE = ACCENT;

    private Theme() {
    }
}
