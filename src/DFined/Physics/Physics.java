package DFined.Physics;

import DFined.core.Parameters;

import java.util.List;

public class Physics {
    public static final long MASS_SCALE = 1000000000000000000L;
    public static final double BIGG = 66.7408;//*10^-12
    public static final long GRAV_SCALE = 1000000000000L;
    public static final long MASS_UPSCALE = MASS_SCALE/GRAV_SCALE;
    public static final long DISTANCE_SCALE = 1000000;

    private static double time = 0;
    private static int physicsTicksPerDraw = 0;

    public static void tick(double dt, SolarSystemState system){
        double deltaT = dt* Parameters.getTimeStep();
        try {
            for(int it = 0; it < Math.max(1,physicsTicksPerDraw); it++) {
                system.calculateInfluence();
                if(physicsTicksPerDraw > 0) {
                    system.step(deltaT);
                    time+=deltaT;
                }
                system.clearAcceleration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        system.project(deltaT*Parameters.PROJECTION_MULTIPLIER);
        for (BodyState state : system) {
            state.getBody().update();
        }
    }

    public static double getTime() {
        return time;
    }

    public static int getPhysicsTicksPerDraw() {
        return physicsTicksPerDraw;
    }

    public static void incrementTPD(){
        physicsTicksPerDraw+=20;
    }

    public static void decrementTPD(){
        physicsTicksPerDraw-=20;
    }
}
