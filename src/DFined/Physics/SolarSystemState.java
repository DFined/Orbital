package DFined.Physics;

import DFined.core.Model;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SolarSystemState implements Iterable<CelestialBody> {

    private ArrayList<CelestialBody> toAdd = new ArrayList<>();
    private ArrayList<CelestialBody> toRemove = new ArrayList<>();

    private ArrayList<CelestialBody> bodies = new ArrayList<>();

    public CelestialBody add(CelestialBody state) {
        bodies.add(state);
        return state;
    }

    public CelestialBody get(int num) {
        return bodies.get(num);
    }


    @Override
    public Iterator<CelestialBody> iterator() {
        return bodies.iterator();
    }

    @Override
    public void forEach(Consumer<? super CelestialBody> action) {
        bodies.forEach(action);
    }

    @Override
    public Spliterator<CelestialBody> spliterator() {
        return bodies.spliterator();
    }

    public int size() {
        return bodies.size();
    }

    public List<CelestialBody> get() {
        return bodies;
    }

    //Calculate all mutual gravitational influences of the bodies in this system
    public void calculateInfluence() {
        for (int i = 0; i < this.size() - 1; i++) {
            this.get(i).calculateInfluence(this, this.get().subList(i + 1, this.size()));
        }
    }

    //Perform actual physics update to velocities and positions. Also add/remove required bodies for any collisions.
    public void step(double dt) {
        if (bodies.addAll(toAdd) || bodies.removeAll(toRemove)) {
            toAdd.clear();
            toRemove.clear();
            Model.getGui().reconstructMainPanel();
        }
        for (CelestialBody state : this) {
            state.tick(dt);
        }
    }

    public void clearAcceleration() {
        for (CelestialBody state : this) {
            state.clearAcceleration();
        }
    }

    /*Handle collisions between bodies. Cant add or remove bodies here, because of concurrent modifiation,
    so they are queued*/
    public void collide(CelestialBody CelestialBody, CelestialBody other) {
        CelestialBody larger = CelestialBody;
        CelestialBody smaller = other;
        if (other.getMass() > larger.getMass()) {
            larger = other;
            smaller = CelestialBody;
        }
        larger.setMass(larger.getMass() + smaller.getMass());
        larger.setRadius(
                Math.cbrt(Math.pow(larger.getRadius(), 3) + Math.pow(smaller.getRadius(), 3))
        );
        larger.setVelocity(
                larger.getVelocity()
                        .scalarMultiply(larger.getMass() / 2)
                        .add(smaller.getMass() / 2, smaller.getVelocity())
                        .scalarMultiply(1 / (larger.getMass() + smaller.getMass()))
        );
        larger.initGraphics(larger.getApplet());
        this.toRemove.add(smaller);
    }

    //Create new body from planetary and kinetics parameters
    public CelestialBody addBody(String registryName, long apoapsis, float apoV, boolean central, PApplet applet) {
        return add(new CelestialBody(BodyParameters.getPreset(registryName))
                .setKinetics(central, apoapsis, apoV)
                .initGraphics(applet)
        );
    }

    //Create new body from planetary and kinetics parameters relative to existing body
    public void attachBody(String registryName, long apoapsis, float apoV, CelestialBody center, PApplet applet) {
        add(new CelestialBody(BodyParameters.getPreset(registryName))
                .setKinetics(
                        false,
                        center.getPosition().getX() * Physics.DISTANCE_SCALE + apoapsis,
                        center.getVelocity().getZ() * Physics.DISTANCE_SCALE + apoV
                )
                .initGraphics(applet)
        );
    }
}