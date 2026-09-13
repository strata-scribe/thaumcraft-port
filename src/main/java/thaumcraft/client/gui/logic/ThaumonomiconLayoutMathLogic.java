package thaumcraft.client.gui.logic;

public class ThaumonomiconLayoutMathLogic {
    // Decoupled math logic for calculating layout offsets and tab hitboxes.

    public static final int START_Y = 30;
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 28;
    public static final int TAB_SPACING = 4;
    public static final int MIN_X = 5;
    public static final int MAX_X = 33; // 5 + 28 = 33

    public static int getTabY(int tabIndex) {
        return START_Y + tabIndex * (TAB_HEIGHT + TAB_SPACING);
    }

    public static boolean isTabHovered(double mouseX, double mouseY, int tabIndex) {
        int tabY = getTabY(tabIndex);
        return mouseX >= MIN_X && mouseX <= MAX_X && mouseY >= tabY && mouseY <= tabY + TAB_HEIGHT;
    }

    public static int getIconX() {
        return 11;
    }

    public static int getIconY(int tabIndex) {
        return getTabY(tabIndex) + 6;
    }

    public static int getSelectedIndicatorY(int tabIndex) {
        return getTabY(tabIndex) + 4;
    }

    public static int getSelectedIndicatorHeight() {
        return TAB_HEIGHT - 8; // 28 - 8 = 20
    }
}
