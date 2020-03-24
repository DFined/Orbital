package DFined.Physics;

import DFined.core.Model;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SolarSystemState implements Iterable<BodyState> {

    private ArrayList<BodyState> toAdd = new ArrayList<>();
    private ArrayList<BodyState> toRemove = new ArrayList<>();

    private ArrayList<BodyState> bodies = new ArrayList<>();

    public BodyState add(BodyState state) {
        bodies.add(state);
        return state;
    }

    public BodyState get(int num) {
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

    public void calculateInfluence() {
        for (int i = 0; i < this.size() - 1; i++) {
            this.get(i).calculateInfluence(this, this.get().subList(i + 1, this.size()));
        }
    }

    public void step(double dt) {
        if(bodies.addAll(toAdd) || bodies.removeAll(toRemove)) {
            toAdd.clear();
            toRemove.clear();
            Model.getGui().reconstructMainPanel();
        }
        for (BodyState state : this) {
            state.tick(dt);
        }
    }

    public void clearAcceleration() {
        for (BodyState state : this) {
            state.clearAcceleration();
        }
    }

    public void collide(BodyState bodyState, BodyState other) {
        BodyState larger = bodyState;
        BodyState smaller = other;
        if (other.getBody().getMass() > larger.getBody().getMass()) {
            larger = other;
            smaller = bodyState;
        }
        larger.getBody().setMass(larger.getBody().getMass() + smaller.getBody().getMass());
        larger.getBody().setRadius(
                Math.cbrt(Math.pow(larger.getBody().getRadius(), 3) + Math.pow(smaller.getBody().getRadius(), 3))
        );
        larger.setVelocity(
                larger.getVelocity()
                        .scalarMultiply(larger.getBody().getMass() / 2)
                        .add(smaller.getBody().getMass() / 2, smaller.getVelocity())
                        .scalarMultiply(1/(larger.getBody().getMass() + smaller.getBody().getMass()))
        );
        larger.getBody().initGraphics(larger.getBody().getApplet());
        this.toRemove.add(smaller);
    }
}