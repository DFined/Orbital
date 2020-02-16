package DFined.Physics;

import DFined.core.Renderer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PApplet;
import processing.core.PShape;

import java.util.List;

public class CelestialBody {
    private BodyState state;
    private double mass; //*10^18 kg
    private double radius; //*10^18 kg
    private PShape shape;
    private PShape marker;
    private String name;
    private String texture;
    private Orbit orbit = new Orbit(this);

    public CelestialBody(BodyParameters params) {
        this.mass = params.getMass();
        this.radius = params.getRadius();
        this.name = params.getDisplayName();
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


    public void draw(Renderer renderer, PApplet applet){
        applet.pushMatrix();
        applet.shape(shape);
        applet.scale(1/renderer.getScale());
        applet.shape(marker);
        applet.popMatrix();
    }

    public BodyState getState(){
        return state;
    }

    public CelestialBody initGraphics(PApplet pApplet) {
        shape = pApplet.createShape(pApplet.SPHERE, (float) (radius/Physics.DISTANCE_SCALE));
        shape.setStroke(false);
        shape.setTexture(pApplet.loadImage(texture));
        marker = pApplet.createShape(pApplet.SPHERE, 20);
        marker.setStroke(false);
        marker.setTexture(pApplet.loadImage(texture));
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
}
