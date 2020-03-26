package DFined.Physics;

import DFined.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Orbit {
    private double semiMinor;
    private double semiMajor;
    private double apoapsis;
    private double periapsis;
    private double apoV;
    private double periV;
    private double eccentricity;
    private long period;
    private CelestialBody anchor;
    private CelestialBody orbiter;
    private double focus;
    private double yawOffset;
    private double periapsisPhase;
    private double phase;

    //Calculate all orbital parameters from immediate body state
    public static Orbit fromPoint(CelestialBody orbiter, CelestialBody anchor) {
        Orbit result = new Orbit(orbiter);
        result.update(anchor);
        // orbital energy equation: u/r - v^2/2 = u/2a
        // therefore a = 1/2(1/r - v^2/2u)
        return result;
    }

    //The anchor is the body that this one orbits
    public void setAnchor(CelestialBody anchor) {
        this.anchor = anchor;
    }

    public void update(CelestialBody anchor) {
        this.anchor = anchor;
        this.update();
    }

    //Update orbital parameters from immediate body state
    public void update() {
        Vector3D rVel = orbiter.getVelocity().subtract(anchor.getVelocity()).scalarMultiply(Physics.DISTANCE_SCALE);
        double v = rVel.getNorm();
        double orbitalParameter = Physics.BIGG * Physics.MASS_UPSCALE * (orbiter.getMass() + anchor.getMass());
        Vector3D radius = anchor.getPosition().subtract(orbiter.getPosition()).scalarMultiply(Physics.DISTANCE_SCALE);
        double r = radius.getNorm();
        radius = radius.normalize();
        this.semiMajor = 1.f / (2.f * ((1.f / r) - v * v / (2 * orbitalParameter)));
        /*
            v1 * r1* sin(a1) = v * r
            u/r1 - v1^2/2 = u/r - v^2/2

            r = v1 * r1* sin(a1) / v

            v^2 - 2uv/(v1 * r1* sin(a1)) + 2u/r1 - v1^2 = 0;

            v = ((2u/(v1 * r1* sin(a1))) +- sqrt(4u^2/(v1 * r1* sin(a1))^2 - 8u/r1 + 4*v1^2))/2
        */
        if (rVel.getNorm() > 0) {
            double sa = rVel.normalize().crossProduct(radius).getNorm();
            double d = v * r * sa;
            double vc = (orbitalParameter / d);
            double vv = Math.sqrt(4 * Math.pow(orbitalParameter / d, 2) - 8 * orbitalParameter / r + 4 * v * v) / 2;

            this.apoV = vc - vv;
            this.periV = vc + vv;
            this.periapsis = d / this.periV;
            this.apoapsis = d / this.apoV;
            this.period = (long) Math.sqrt(Math.pow(semiMajor, 3) * 4 * Math.pow(Math.PI, 2) / (orbitalParameter));
            this.eccentricity = apoapsis / semiMajor - 1;
            this.semiMinor = semiMajor * Math.sqrt(1 - Math.pow(eccentricity, 2));
            this.focus = Math.sqrt(Math.pow(this.semiMajor, 2) - Math.pow(this.semiMinor, 2));
            this.phase = Math.acos((d * d / orbitalParameter - r) / (this.eccentricity * r));
            if (rVel.dotProduct(radius) > 0) {
                this.phase = 2 * Math.PI - this.phase;
            }
            double o2h = Math.acos(radius.dotProduct(new Vector3D(-1, 0, 0)));
            if (radius.dotProduct(new Vector3D(0, 0, 1)) > 0) {
                o2h = 2 * Math.PI - o2h;
            }
            this.yawOffset = this.phase - o2h;
        }
    }

    //Correct way to calculate orbital angle from trig.
    public double twoPiAngle(double asin, double acos) {
        if (asin > 0) {
            return acos;
        } else {
            if (acos < Math.PI / 2) {
                return asin;
            } else {
                return -Math.PI - asin;
            }
        }
    }

    public Orbit(CelestialBody orbiter) {
        this.orbiter = orbiter;
    }

    //For displaying orbital parameters calculated in update. Not fully working yet, therefor disabled.
    @Override
    public String toString() {
        return String.format("Orbit of %s around %s:{\n\tapoapsis: %.3f;\t\nperiapsis: %.3f;\t\n maxSpeed: %.3f;\n\t minSpeed: %.3f;\n\t period: %s;\n\t semiMajor: %.3f;\n\t semiMinor: %.3f;\n\t eccentricity: %.3f;\n\t phase: %.3f\n}",
                orbiter.getName(),
                anchor.getName(),
                apoapsis,
                periapsis,
                periV,
                apoV,
                Util.formatSeconds(period),
                semiMajor,
                semiMinor,
                eccentricity,
                phase
        );
    }

    public double getSemiMinor() {
        return semiMinor;
    }

    public double getSemiMajor() {
        return semiMajor;
    }

    public double getApoapsis() {
        return apoapsis;
    }

    public double getPeriapsis() {
        return periapsis;
    }

    public double getApoV() {
        return apoV;
    }

    public double getPeriV() {
        return periV;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public long getPeriod() {
        return period;
    }

    public CelestialBody getAnchor() {
        return anchor;
    }

    public CelestialBody getOrbiter() {
        return orbiter;
    }

    public double getFocus() {
        return focus;
    }
}
