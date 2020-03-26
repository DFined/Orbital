package DFined.Physics;

import static DFined.core.Parameters.MAX_TIME_STEP;
import static DFined.core.Parameters.TARGET_TPS;

public class Physics {
    public static final long MASS_SCALE = 1000000000000000000L;
    public static final double BIGG = 66.7408;//*10^-12
    public static final long GRAV_SCALE = 1000000000000L;
    public static final long MASS_UPSCALE = MASS_SCALE / GRAV_SCALE;
    public static final long DISTANCE_SCALE = 1000000;

    private static double time = 0;
    private static int timeSpeed = 0;
    private static int physicsStepsPerGraphicsStep = 0;

    //Calculate required delta-T and step count and perform physics ticks.
    public static void tick(double dt, SolarSystemState system) {
        int timeStep = 1;
        if (timeSpeed < TARGET_TPS) {
            physicsStepsPerGraphicsStep = timeSpeed;
        } else if (timeSpeed < MAX_TIME_STEP * TARGET_TPS) {
            timeStep = timeSpeed / TARGET_TPS;
            physicsStepsPerGraphicsStep = TARGET_TPS;
        } else {
            timeStep = MAX_TIME_STEP;
            physicsStepsPerGraphicsStep = timeSpeed / MAX_TIME_STEP;
        }
        double deltaT = dt * timeStep;
        try {
            for (int it = 0; it < Math.max(1, physicsStepsPerGraphicsStep); it++) {
                system.calculateInfluence();
                if (physicsStepsPerGraphicsStep > 0) {
                    system.step(deltaT);
                    time += deltaT;
                }
                system.clearAcceleration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (CelestialBody state : system) {
            state.update();
        }
    }

    public static double getTime() {
        return time;
    }

    public static int getTimeSpeed() {
        return timeSpeed;
    }

    public static void setTimeSpeed(int timeSpeed) {
        Physics.timeSpeed = timeSpeed;
    }
}
