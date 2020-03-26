package DFined.Physics;

import DFined.core.Parameters;
import DFined.core.Renderer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.*;
import processing.opengl.PGraphics3D;

import java.util.List;

public class CelestialBody {
    private double mass; //*10^18 kg
    private double radius; //*10^18 kg
    private PShape shape;
    private PShape marker;
    private String name;
    private String registryName;
    private String texture;
    private PImage textureImage;
    private Orbit orbit = new Orbit(this);
    private PApplet applet;
    private boolean central;
    private Vector3D position;
    private Vector3D velocity;
    private Vector3D acceleration = new Vector3D(0, 0, 0);
    private double maximumA = 0;

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
            this.orbit.update();
        }
    }

    //Self-render method. Draws the body. All necessary transforms are handled by the renderer.
    public void draw(Renderer renderer, PGraphics graphics) {
        graphics.pushMatrix();
        graphics.shape(shape);
        graphics.scale(1.f / renderer.getScale());
        graphics.shape(marker);
        if(Parameters.isDrawLabels()) {
            graphics.rotateX(PConstants.PI / 2);
            graphics.text(name, -name.length() * 15, -80, 0);
        }
        graphics.popMatrix();
    }

    //Initialize required PGraphics elements for rendering.
    public CelestialBody initGraphics(PApplet pApplet) {
        shape = pApplet.createShape(pApplet.SPHERE, (float) (radius / Physics.DISTANCE_SCALE));
        shape.setStroke(false);
        if (textureImage == null) {
            textureImage = pApplet.loadImage(texture);
        }
        shape.setTexture(textureImage);
        marker = pApplet.createShape(pApplet.SPHERE, 20);
        marker.setStroke(false);
        marker.setTexture(textureImage);
        this.applet = pApplet;
        return this;
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
    public CelestialBody clone(Vector3D pos, PApplet applet) {
        CelestialBody clone = new CelestialBody(BodyParameters.getPreset(this.registryName)).initGraphics(applet);
        int rand = (int) applet.random(0, 10000);
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

    public PApplet getApplet() {
        return applet;
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

        if (accel.getNorm() / dist > maximumA && !isCentral()) {
            maximumA = accel.getNorm() / dist;
            getOrbit().setAnchor(source);
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
    public void calculateInfluence(SolarSystemState bodyStates, List<CelestialBody> others) {
        for (CelestialBody other : others) {
            Vector3D radius = position.subtract(other.position);
            double collDist = other.getRadius() / Physics.DISTANCE_SCALE + this.getRadius() / Physics.DISTANCE_SCALE;
            if (radius.getNorm() <= collDist) {
                bodyStates.collide(this, other);
            }
            double semiForce = Physics.BIGG / (radius.getNormSq() * Physics.DISTANCE_SCALE);
            double acc = this.getMass() * Physics.MASS_UPSCALE * semiForce / (Physics.DISTANCE_SCALE * Physics.DISTANCE_SCALE);
            Vector3D accel = radius.scalarMultiply(acc / radius.getNorm());
            influenceOther(accel, other, radius.getNorm());

            acc = other.getMass() * Physics.MASS_UPSCALE * semiForce / (Physics.DISTANCE_SCALE * Physics.DISTANCE_SCALE);
            accel = radius.scalarMultiply(-acc / radius.getNorm());
            addAcceleration(accel, other, radius.getNorm());
        }
    }

    //Exert gravitational influence over other body. Could be overridden for smaller bodies to negate influence.
    public void influenceOther(Vector3D accel, CelestialBody other, double rad) {
        other.addAcceleration(accel, this, rad);
    }

    protected void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }
}
