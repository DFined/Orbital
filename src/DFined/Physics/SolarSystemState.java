package DFined.Physics;

import java.util.*;
import java.util.function.Consumer;

public class SolarSystemState implements Iterable<BodyState> {

    private transient ArrayList<SolarSystemState> projectedState;
    private transient ArrayList<SolarSystemState> rejectedState;

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
}
