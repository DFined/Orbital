package DFined.Physics;

import DFined.core.Renderer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
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

    public void postTick(){
        clearAcceleration();
    }

    public void update(){
        if(!this.isCentral()) {
            this.orbit.update();
        }
    }


    public void draw(Renderer renderer, PGraphics graphics){
        graphics.pushMatrix();
        graphics.shape(shape);
        graphics.scale(1/renderer.getScale());
        graphics.shape(marker);
        graphics.popMatrix();
    }

    public CelestialBody initGraphics(PApplet pApplet) {
        shape = pApplet.createShape(pApplet.SPHERE, (float) (radius/Physics.DISTANCE_SCALE));
        shape.setStroke(false);
        if(textureImage == null) {
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

    public CelestialBody clone(Vector3D pos, PApplet applet){
        CelestialBody clone = new CelestialBody(BodyParameters.getPreset(this.registryName)).initGraphics(applet);
        int rand = (int)applet.random(0,10000);
        clone.setName("Planet " + rand);
        clone.setRegistryName("planet_" + rand);
        return clone.setKinetics(clone,this.isCentral(), pos, this.getVelocity(), Vector3D.ZERO);
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

    private boolean central;
    private Vector3D position;
    private Vector3D velocity;
    private Vector3D acceleration = new Vector3D(0, 0, 0);
    private double maximumA = 0;

    public CelestialBody setKinetics(boolean central, double apoapsis, double minimumV) {
        this.central = central;
        this.position = new Vector3D(apoapsis / Physics.DISTANCE_SCALE, 0, 0);
        this.velocity = new Vector3D(0, 0, minimumV / Physics.DISTANCE_SCALE);
        this.acceleration = new Vector3D(0, 0, 0);
        return this;
    }

    public CelestialBody setKinetics(CelestialBody body, boolean central, Vector3D position, Vector3D velocity, Vector3D acceleration) {
        this.central = central;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        return this;
    }

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

    public Vector3D getAcceleration() {
        return acceleration;
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

    public void clearAcceleration() {
        this.acceleration = new Vector3D(0, 0, 0);
    }

    public void tick(double deltaT) {
        velocity = this.velocity.add(deltaT, this.acceleration);
        position = this.position.add(deltaT, this.velocity);
    }

    public void calculateInfluence(SolarSystemState bodyStates, List<CelestialBody> others) {
        for (CelestialBody other : others) {
            Vector3D radius = position.subtract(other.position);
            double collDist = other.getRadius()/Physics.DISTANCE_SCALE + this.getRadius()/Physics.DISTANCE_SCALE;
            if(radius.getNorm() <= collDist){
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

    public void influenceOther(Vector3D accel, CelestialBody other, double rad) {
        other.addAcceleration(accel, this, rad);
    }

    protected void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(Vector3D acceleration) {
        this.acceleration = acceleration;
    }
}
