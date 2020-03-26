package DFined.core;

import DFined.Physics.BodyParameters;
import DFined.Physics.CelestialBody;
import DFined.Physics.Physics;
import DFined.Physics.SolarSystemState;
import DFined.gui.GUI;
import g4p_controls.G4P;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
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

    public Model() {
        super();
        system = new SolarSystemState();
    }

    //PApplet method required for some pre-start settings
    @Override
    public void settings() {
        size(1000, 700, P3D);
        G4P.messagesEnabled(false);
        fullScreen();
    }

    //Load and deserialize all of the required data from disk. Planet data, later Solar system data.
    public static void loadResources() {
        File data = new File("resources/Data");
        Collection<File> resources = FileUtils.listFiles(data, FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter());
        for (File file : resources) {
            try {
                BodyParameters.addPreset(FileUtils.readFileToString(file, "utf8"));
            } catch (IOException e) {
                logger.log(Level.WARNING, String.format("Error loading resource '%s'", file.getName()), e);
            }
        }
    }


    //PApplet method for setup to be run once before main loop
    public void setup() {
        loadResources();

        renderer = new Renderer(this);

        stroke(255);

        system.addBody(BodyParameters.SUN, 0, 0, true, this);

        system.addBody(BodyParameters.MERCURY, 69816900000L, 38860f, false, this);

        system.addBody(BodyParameters.VENUS, -108940000000L, -(34780f), false, this);

        CelestialBody earth = system.addBody(BodyParameters.EARTH, 152098233000L, 29292f, false, this);

        system.attachBody(BodyParameters.MOON, -405500000L, 970f, earth, this);

        CelestialBody mars = system.addBody(BodyParameters.MARS, -249200000000L, -22000f, false, this);

        system.attachBody(BodyParameters.PHOBOS, -9517000L, 2138f, mars, this);

        system.attachBody(BodyParameters.DEIMOS, -23470000L, 1351f, mars, this);

        CelestialBody jupiter = system.addBody(BodyParameters.JUPITER, 816620000000L, 12440f, false, this);

        system.attachBody(BodyParameters.EUROPA, 676938000L, 13740f, jupiter, this);

        system.attachBody(BodyParameters.GANYMEDE, -1071600000L, 10880f, jupiter, this);

        system.attachBody(BodyParameters.IO, -423400000L, 17334f, jupiter, this);

        system.attachBody(BodyParameters.CALLISTO, 1897000000L, 8204f, jupiter, this);

        CelestialBody saturn = system.addBody(BodyParameters.SATURN, -1514500000000L, -9090f, false, this);

        system.attachBody(BodyParameters.TITAN, 1257060000L, 5570f, saturn, this);

        system.attachBody(BodyParameters.ENCELADUS, -239156000L, 12635f, saturn, this);

        system.attachBody(BodyParameters.MIMAS, 189176000L, 14280f, saturn, this);

        system.addBody(BodyParameters.URANUS, 3003620000000L, 6490f, false, this);

        system.addBody(BodyParameters.NEPTUNE, -4545670000000L, -5370f, false, this);

        system.addBody(BodyParameters.PLUTO, -7375930000000L, -3710f, false, this);

        gui = new GUI(this);

        renderer.setFocus(system.get(0));

    }

    public static Renderer getRenderer() {
        return renderer;
    }

    //PApplet method - called as main loop regularly with a maintained frame rate
    public void draw() {
        background(181, 171, 255);
        double dt = ((float) (millis() - lastTick)) / 1000;
        lastTick = millis();
        Physics.tick(dt, system);
        gui.update();
    }

    public static void main(String[] args) {
        PApplet.main("DFined.core.Model", args);
    }

    public static SolarSystemState getSystem() {
        return system;
    }

    //Handler for mouse wheel events. Used to change renderer scale as alternative to scale buttons in gui
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
}
