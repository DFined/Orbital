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
    private BodyState state;
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

    public CelestialBody setKinetics(BodyState state){
        this.state = state;
        state.setBody(this);
        return this;
    }

    public Orbit getOrbit() {
        return orbit;
    }

    public void postTick(){
        this.state.clearAcceleration();
    }

    public void update(){
        if(!this.state.isCentral()) {
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

    public BodyState getState(){
        return state;
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

    public boolean isCentral() {
        return state.isCentral();
    }

    public Vector3D getVelocity(){
        return state.getVelocity();
    }

    public Vector3D getPosition(){
        return state.getPosition();
    }

    public double getRadius() {
        return radius;
    }

    public CelestialBody clone(Vector3D pos, PApplet applet){
        CelestialBody clone = new CelestialBody(BodyPresets.getPreset(this.registryName)).initGraphics(applet);
        int rand = (int)applet.random(0,10000);
        clone.setName("Planet " + rand);
        clone.setRegistryName("planet_" + rand);
        clone.state = new BodyState(clone,this.isCentral(), pos, this.getVelocity(), Vector3D.ZERO);
        return clone;
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
}
