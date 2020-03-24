package DFined.core;

public class Parameters {
    private static int timeStep = 365*12;
    private static int maxPhysicsStepsPerTick = 1000;

    public static int getMaxPhysicsStepsPerTick() {
        return maxPhysicsStepsPerTick;
    }

    public static int getTimeStep() {
        return timeStep;
    }
}
