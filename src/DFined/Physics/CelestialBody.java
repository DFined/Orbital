package DFined.Physics;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PShape;

public class CelestialBody {
    private double mass; //*10^18 kg
    private double radius; //*10^18 kg
    private PShape shape;
    private PShape marker;
    private String name;
    private String registryName;
    private String texture;
    private Orbit orbit = new Orbit();
    private boolean central;
    private Vector3D position;
    private Vector3D velocity;
    private Vector3D acceleration = new Vector3D(0, 0, 0);
    private double maximumA = 0;
    CelestialBody anchor;

    public CelestialBody(BodyParameters params) {
        this.mass = params.getMass();
        this.radius = params.getRadius();
        this.name = params.getDisplayName();
        this.registryName = params.getRegistryName();
        this.texture = params.getTexture();
    }

    public Orbit getOrbit() {
        return orbit;
    }

    //Method for updating orbital parameters each tick.
    public void update() {
        if (!this.isCentral()) {
            this.orbit.update(this.position, anchor.position, this.velocity, anchor.velocity, this.mass, anchor.mass);
        }
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    //Create a parameter-wise copy of this body. Used by the copy button in gui.
    public CelestialBody clone(Vector3D pos) {
        CelestialBody clone = new CelestialBody(BodyParameters.getPreset(this.registryName));
        int rand = (int) (Math.random() * 10000);
        clone.setName("Planet " + rand);
        clone.setRegistryName("planet_" + rand);
        return clone.setKinetics(this.isCentral(), pos, this.getVelocity(), Vector3D.ZERO);
    }

    protected void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setMass(double mass) {
        this.mass = mass;
    }

    protected void setRadius(double radius) {
        this.radius = radius;
    }

    //Set body kinetics from apoapsis and velocity at apoapsis. Central argument only for orbit calculation
    public CelestialBody setKinetics(boolean central, double apoapsis, double minimumV) {
        this.central = central;
        this.position = new Vector3D(apoapsis / Physics.DISTANCE_SCALE, 0, 0);
        this.velocity = new Vector3D(0, 0, minimumV / Physics.DISTANCE_SCALE);
        this.acceleration = new Vector3D(0, 0, 0);
        return this;
    }

    //Set body kinetics directly. Central argument only for orbit calculation
    public CelestialBody setKinetics(
            boolean central,
            Vector3D position,
            Vector3D velocity,
            Vector3D acceleration
    ) {
        this.central = central;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        return this;
    }

    //Add acceleration to this body. Also calculate influence factor from source for orbit calculations.
    public void addAcceleration(Vector3D accel, CelestialBody source, double dist) {

        if (accel.getNorm() > maximumA && !isCentral()) {
            maximumA = accel.getNorm();
            anchor = source;
        }

        this.acceleration = this.acceleration.add(accel);
    }

    public boolean isCentral() {
        return central;
    }

    public Vector3D getPosition() {
        return position;
    }

    public Vector3D getVelocity() {
        return velocity;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getZ() {
        return position.getZ();
    }

    //Clear acceleration at the end of every physics tick
    public void clearAcceleration() {
        this.acceleration = new Vector3D(0, 0, 0);
    }

    //Perform Physics tick and apply acceleration and velocity
    public void tick(double deltaT) {
        velocity = this.velocity.add(deltaT, this.acceleration);
        position = this.position.add(deltaT, this.velocity);
    }

    //Main method for calculating gravitational attraction
    public CelestialBody calculateInfluence(CelestialBody other) {
        Vector3D radius = position.subtract(other.position);
        double collDist = other.getRadius() / Physics.DISTANCE_SCALE + this.getRadius() / Physics.DISTANCE_SCALE;
        double semiForce = Physics.BIGG / (radius.getNormSq() * Physics.DISTANCE_SCALE);
        double acc = this.getMass() * Physics.MASS_UPSCALE * semiForce / (Physics.DISTANCE_SCALE * Physics.DISTANCE_SCALE);
        Vector3D accel = radius.scalarMultiply(acc / radius.getNorm());
        influenceOther(accel, other, radius.getNorm());

        acc = other.getMass() * Physics.MASS_UPSCALE * semiForce / (Physics.DISTANCE_SCALE * Physics.DISTANCE_SCALE);
        accel = radius.scalarMultiply(-acc / radius.getNorm());
        addAcceleration(accel, other, radius.getNorm());
        if (radius.getNorm() <= collDist) {
            return this.collide(this, other);
        }
        return null;
    }

    /*Handle collisions between bodies. Cant add or remove bodies here, because of concurrent modifiation,
  so they are queued*/
    public CelestialBody collide(CelestialBody CelestialBody, CelestialBody other) {
        CelestialBody larger = CelestialBody;
        CelestialBody smaller = other;
        if (other.getMass() > larger.getMass()) {
            larger = other;
            smaller = CelestialBody;
        }
        larger.setMass(larger.getMass() + smaller.getMass());
        larger.setRadius(
                Math.cbrt(Math.pow(larger.getRadius(), 3) + Math.pow(smaller.getRadius(), 3))
        );
        larger.setVelocity(
                larger.getVelocity()
                        .scalarMultiply(larger.getMass() / 2)
                        .add(smaller.getMass() / 2, smaller.getVelocity())
                        .scalarMultiply(1 / (larger.getMass() + smaller.getMass()))
        );
        return smaller;
    }


    //Exert gravitational influence over other body. Could be overridden for smaller bodies to negate influence.
    public void influenceOther(Vector3D accel, CelestialBody other, double rad) {
        other.addAcceleration(accel, this, rad);
    }

    public void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }

    public PShape getShape() {
        return shape;
    }

    public void setShape(PShape shape) {
        this.shape = shape;
    }

    public String getTexture() {
        return texture;
    }

    public void setMarker(PShape marker) {
        this.marker = marker;
    }

    public PShape getMarker() {
        return marker;
    }

}
