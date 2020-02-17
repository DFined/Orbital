package DFined.Physics;

import DFined.core.Parameters;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PApplet;

import java.util.*;
import java.util.function.Consumer;

public class SolarSystemState implements Iterable<BodyState> {

    private transient ArrayList<SolarSystemState> projectedState = new ArrayList<>(Parameters.PROJECTION_BUFFER_SIZE);
    private transient ArrayList<SolarSystemState> rejectedState = new ArrayList<>(Parameters.REJECTION_BUFFER_SIZE);

    private ArrayList<BodyState> bodies = new ArrayList<>();

    public boolean add(BodyState state){
        return bodies.add(state);
    }

    public BodyState get(int num){
        return bodies.get(num);
    }


    @Override
    public Iterator<BodyState> iterator() {
        return bodies.iterator();
    }

    @Override
    public void forEach(Consumer<? super BodyState> action) {
        bodies.forEach(action);
    }

    @Override
    public Spliterator<BodyState> spliterator() {
        return bodies.spliterator();
    }

    public int size() {
        return bodies.size();
    }

    public List<BodyState> get() {
        return bodies;
    }

    @Override
    public SolarSystemState clone() throws CloneNotSupportedException {
        SolarSystemState newState = new SolarSystemState();
        for(BodyState state: this){
            newState.add(state.clone());
        }
        return newState;
    }

    public void calculateInfluence(){
        for (int i = 0; i < this.size() - 1; i++) {
            this.get(i).calculateInfluence(this.get().subList(i + 1, this.size()));
        }
    }

    public void step(double dt) {
        for (BodyState state : this) {
            state.tick(dt);
        }
    }

    public void clearAcceleration() {
        for(BodyState state: this){
            state.clearAcceleration();
        }
    }

    public void project(double dt){
        try {
            SolarSystemState pr = this;
            projectedState = new ArrayList<>(Parameters.PROJECTION_BUFFER_SIZE);
            for(int i = 0; i < Parameters.PROJECTION_BUFFER_SIZE; i++) {
                pr = pr.clone();
                pr.calculateInfluence();
                pr.step(dt);
                projectedState.add(pr);
                pr.clearAcceleration();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void drawProjection(PApplet applet){
        Vector3D pos;
        for(int i = 0; i < this.size(); i++) {
            applet.beginShape();
            pos = this.get(i).getPosition();
            applet.curveVertex((float)pos.getX(),(float)pos.getY(),(float)pos.getZ());
            for(int j = 0; j < projectedState.size(); j++){
                pos = projectedState.get(j).get(i).getPosition();
                applet.curveVertex((float)pos.getX(),(float)pos.getY(),(float)pos.getZ());
            }
            applet.endShape();
        }
    }
}