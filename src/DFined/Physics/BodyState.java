package DFined.Physics;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public class BodyState {
    private CelestialBody body;
    private boolean central;
    private Vector3D position;
    private Vector3D velocity;
    private Vector3D acceleration = new Vector3D(0,0,0);
    private double maximumA = 0;

    public BodyState(boolean central, double apoapsis, double minimumV) {
        this.central = central;
        this.position = new Vector3D(apoapsis/Physics.DISTANCE_SCALE,0,0);
        this.velocity = new Vector3D(0,0,minimumV/Physics.DISTANCE_SCALE);
        this.acceleration = new Vector3D(0,0,0);
    }

    public BodyState(CelestialBody body, boolean central, Vector3D position, Vector3D velocity, Vector3D acceleration) {
        this.body = body;
        this.central = central;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public void addAcceleration(Vector3D accel, CelestialBody source){
            if(accel.getNorm() > maximumA && !isCentral()){
                maximumA = accel.getNorm();
                body.getOrbit().setAnchor(source);
            }
            this.acceleration = this.acceleration.add(accel);
    }

    public void setBody(CelestialBody body) {
        this.body = body;
    }

    public CelestialBody getBody() {
        return body;
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

    public double getX(){
        return position.getX();
    }

    public double getY(){
        return position.getY();
    }

    public double getZ(){
        return position.getZ();
    }

    public void clearAcceleration() {
         this.acceleration = new Vector3D(0,0,0);
    }

    public void tick(double deltaT) {
        velocity = this.velocity.add(deltaT,this.acceleration);
        position = this.position.add(deltaT,this.velocity);
    }

    @Override
    public BodyState clone() throws CloneNotSupportedException {
        return new BodyState(body, central, position.scalarMultiply(1),velocity.scalarMultiply(1),acceleration.scalarMultiply(1));
    }

    public void calculateInfluence(List<BodyState> others){
        for(BodyState other: others){
            Vector3D radius = position.subtract(other.position);
            double semiForce = Physics.BIGG/(radius.getNormSq()*Physics.DISTANCE_SCALE);
            double acc = this.body.getMass()*Physics.MASS_UPSCALE*semiForce/(Physics.DISTANCE_SCALE*Physics.DISTANCE_SCALE);
            Vector3D accel = radius.scalarMultiply(acc/radius.getNorm());
            other.addAcceleration(accel,this.getBody());

            acc = other.body.getMass()*Physics.MASS_UPSCALE*semiForce/(Physics.DISTANCE_SCALE*Physics.DISTANCE_SCALE);
            accel = radius.scalarMultiply(-acc/radius.getNorm());
            addAcceleration(accel, other.getBody());
        }
    }
}
