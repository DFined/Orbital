package DFined.core;

import DFined.Physics.*;
import DFined.gui.GUI;
import g4p_controls.G4P;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model extends PApplet {
    private static Renderer renderer;
    private static final Logger logger = Logger.getLogger("Orbital");
    private static SolarSystemState system;
    private static long lastTick = 0;
    private static GUI gui;
    private static Model instance;

    public Model() {
        super();
        system = new SolarSystemState();
        instance = this;
    }

    @Override
    public void settings() {
        size(1000, 700, P3D);
        G4P.messagesEnabled(false);
        fullScreen();
    }

    public static void loadResources() {
        File data = new File("resources/Data");
        Collection<File> resources = FileUtils.listFiles(data, FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter());
        for (File file : resources) {
            try {
                BodyParameters.addPreset(FileUtils.readFileToString(file, "utf8"));
            } catch (IOException e) {
                logger.log(Level.WARNING, String.format("Error loading resource \'%s\'", file.getName()), e);
            }
        }
    }

    public void setup() {
        loadResources();

        renderer = new Renderer(this);

        stroke(255);

        this.addBody(BodyParameters.SUN, 0, 0, true, false);

        this.addBody(BodyParameters.MERCURY, 69816900000L, 38860f, false, false);

        this.addBody(BodyParameters.VENUS, -108940000000L, -(34780f), false, false);

        CelestialBody earth = this.addBody(BodyParameters.EARTH, 152098233000L, 29292f, false, false);

        this.attachBody(BodyParameters.MOON, -405500000L, 970f, earth, true);

        CelestialBody mars = this.addBody(BodyParameters.MARS, -249200000000L, -22000f, false, false);

        this.attachBody(BodyParameters.PHOBOS, -9517000L, 2138f, mars, true);

        this.attachBody(BodyParameters.DEIMOS, -23470000L, 1351f, mars, true);

        CelestialBody jupiter = this.addBody(BodyParameters.JUPITER, 816620000000L, 12440f, false, false);

        this.attachBody(BodyParameters.EUROPA, 676938000L, 13740f, jupiter, true);

        this.attachBody(BodyParameters.GANYMEDE, -1071600000L, 10880f, jupiter, true);

        this.attachBody(BodyParameters.IO, -423400000L, 17334f, jupiter, true);

        this.attachBody(BodyParameters.CALLISTO, 1897000000L, 8204f, jupiter, true);

        CelestialBody saturn = this.addBody(BodyParameters.SATURN, -1514500000000L, -9090f, false, false);

        this.attachBody(BodyParameters.TITAN, 1257060000L, 5570f, saturn, true);

        this.attachBody(BodyParameters.ENCELADUS, -239156000L, 12635f, saturn, true);

        this.attachBody(BodyParameters.MIMAS, 189176000L, 14280f, saturn, true);

        this.addBody(BodyParameters.URANUS, 3003620000000L, 6490f, false, false);

        this.addBody(BodyParameters.NEPTUNE, -4545670000000L, -5370f, false, false);

        this.addBody(BodyParameters.PLUTO, -7375930000000L, -3710f, false, false);

        gui = new GUI(this);

        renderer.setFocus(system.get(0));
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public Vector3D randomV3D(float minC, float maxC) {
        return new Vector3D(random(minC, maxC), 0, random(minC, maxC));
    }

    public void draw() {
        double dt = ((float) (millis() - lastTick)) / 1000;
        lastTick = millis();
        Physics.tick(dt, system);
        //renderer.render(this, system);
    }

    public static void main(String[] args) {
        PApplet.main("DFined.core.Model", args);
    }

    public static SolarSystemState getSystem() {
        return system;
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        if (event.getCount() > 0) {
            renderer.setScale(renderer.getScale() / 1.1f);
        } else {
            renderer.setScale(renderer.getScale() * 1.1f);
        }
    }

    public static GUI getGui() {
        return gui;
    }

    private static int focus = 0;

    private CelestialBody addBody(String registryName, long apoapsis, float apoV, boolean central, boolean light) {
        return system.add(new CelestialBody(BodyParameters.getPreset(registryName))
                .setKinetics(central, apoapsis, apoV)
                .initGraphics(this)
        );
    }

    private void attachBody(String registryName, long apoapsis, float apoV, CelestialBody center, boolean light) {
        system.add(new CelestialBody(BodyParameters.getPreset(registryName))
                .setKinetics(
                        false,
                        center.getPosition().getX() * Physics.DISTANCE_SCALE + apoapsis,
                        center.getVelocity().getZ() * Physics.DISTANCE_SCALE + apoV
                )
                .initGraphics(this)
        );
    }

    public static Model getInstance() {
        return instance;
    }
}
