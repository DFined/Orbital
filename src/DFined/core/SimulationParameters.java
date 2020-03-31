package DFined.core;

public class SimulationParameters {
    public static final int MAX_TIME_STEP = 365 * 12;
    public static final int TARGET_TPS = 100;
    public static final int MAX_TIME_SPEED = 86400000;
    private static boolean drawLabels = false;

    public static boolean isDrawLabels() {
        return drawLabels;
    }

    public static void setDrawLabels(boolean drawLabels) {
        SimulationParameters.drawLabels = drawLabels;
    }

}
