package DFined.Physics;

import java.util.*;
import java.util.function.Consumer;

public class SolarSystemState implements Iterable<BodyState> {
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
}
