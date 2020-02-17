package DFined.core;

public class Parameters {
    public static final int REJECTION_BUFFER_SIZE = 200;
    public static final int PROJECTION_BUFFER_SIZE = 200;
    public static final int PROJECTION_MULTIPLIER = 2000;
    private static int timeStep = 365*12;

    public static int getTimeStep() {
        return timeStep;
    }
}
