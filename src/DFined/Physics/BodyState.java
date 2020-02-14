package DFined.Physics;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

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

    public void tick(double deltaT, long timeSpeed) {
        velocity = this.velocity.add(deltaT*timeSpeed,this.acceleration);
        position = this.position.add(deltaT*timeSpeed,this.velocity);
    }
}
