package DFined.gui;

import DFined.Physics.CelestialBody;
import DFined.Physics.Physics;
import DFined.Physics.SolarSystemState;
import DFined.Util;
import DFined.core.Model;
import DFined.core.SimulationParameters;
import com.sun.org.apache.xpath.internal.operations.Mod;
import g4p_controls.*;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PApplet;

import java.awt.*;
import java.util.Iterator;

import static processing.core.PConstants.P3D;

public class GUI {
    public boolean lockInfo = false;
    public CelestialBody copy = null;
    public final GPanel RIGHT_PANEL;
    private SlidePanel LEFT_PANEL;
    private GPanel BOTTOM_PANEL;
    private GPanel TOP_PANEL;
    public final GView VIEW;
    public static final int PADDING = 15;
    private static final int LEFT_PANEL_SIZE = 200;
    private static final int RIGHT_PANEL_SIZE = 300;
    private static final int BOTTOM_PANEL_SIZE = PADDING * 6;
    private static final int TOP_PANEL_SIZE = PADDING * 3;
    public static final int TEXT_SIZE = 100;
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 22);
    private static final Font SMALLER_FONT = new Font("Monospaced", Font.PLAIN, 16);
    private static GSlider SCALE_SLIDER;
    private static GTextField NAME;
    private static GTextField MASS;
    private static GTextField RADIUS;
    private static GTextField SEARCH;
    private static GTextField TIME_SPEED;
    private static GTextField CURRENT_TIME;
    private static GTextField VX;
    private static GTextField VY;
    private static GTextField VZ;


    public void update() {
        CURRENT_TIME.setText(Util.formatSeconds(Math.round(Physics.getTime())));
        updateInfo(Model.getRenderer().getFocus(), false);
    }

    //Constructor initializes all the gui elements
    public GUI(PApplet applet) {
        G4P.setCtrlMode(GControlMode.CORNERS);
        this.VIEW = new GView(
                applet,
                LEFT_PANEL_SIZE,
                TOP_PANEL_SIZE,
                applet.width - RIGHT_PANEL_SIZE,
                applet.height - BOTTOM_PANEL_SIZE,
                P3D
        );
        this.VIEW.addListener(new ViewHandler());
        this.RIGHT_PANEL = constructInfoPanel(applet);
        this.LEFT_PANEL = constructMainPanel(applet, Model.getSystem().iterator());
        this.BOTTOM_PANEL = constructBottomPanel(applet);
        this.TOP_PANEL = constructTopPanel(applet);
    }

    //Initialization of top gui panel
    private GPanel constructTopPanel(PApplet applet) {
        GPanel panel = new GPanel(
                applet,
                LEFT_PANEL_SIZE,
                0,
                applet.width - RIGHT_PANEL_SIZE,
                TOP_PANEL_SIZE
        );
        panel.setOpaque(false);
        GLabel scaleLabel = new GLabel(
                applet,
                PADDING,
                PADDING / 2,
                PADDING * 4,
                PADDING * 5 / 2,
                "Scale:"
        );

        panel.addControl(scaleLabel);
        GButton addScale = new GButton(
                applet,
                PADDING * 5,
                PADDING / 2,
                PADDING * 7,
                PADDING * 5 / 2,
                "+"
        );
        GButton reduceScale = new GButton(
                applet,
                PADDING * 8,
                PADDING / 2,
                PADDING * 10,
                PADDING * 5 / 2,
                "-"
        );
        addScale.addEventHandler(this, "plusScale");
        reduceScale.addEventHandler(this, "subScale");
        panel.addControl(addScale);
        panel.addControl(reduceScale);

        GButton toggleLabels = new GButton(
                applet,
                PADDING * 12,
                PADDING / 2,
                PADDING * 18,
                PADDING * 5 / 2,
                "Toggle labels"
        );
        toggleLabels.addEventHandler(this, "toggleLabels");
        panel.addControl(toggleLabels);
        return panel;
    }

    //G4P GUI lib calls handlers via reflection. Handler for toggle labels button.
    public void toggleLabels(GButton button, GEvent event) {
        SimulationParameters.setDrawLabels(!SimulationParameters.isDrawLabels());
    }

    //G4P GUI lib calls handlers via reflection. Handler for increase scale button.
    public void plusScale(GButton button, GEvent event) {
        Model.getRenderer().setScale(Model.getRenderer().getScale() * 1.2f);
    }

    //G4P GUI lib calls handlers via reflection. Handler for decrease scale button.
    public void subScale(GButton button, GEvent event) {
        Model.getRenderer().setScale(Model.getRenderer().getScale() * 1 / 1.2f);
    }

    //Initialization of bottom gui panel
    private GPanel constructBottomPanel(PApplet applet) {
        int WIDTH = applet.width - RIGHT_PANEL_SIZE - LEFT_PANEL_SIZE;
        GPanel panel = new GPanel(
                applet,
                LEFT_PANEL_SIZE,
                applet.height - BOTTOM_PANEL_SIZE,
                applet.width - RIGHT_PANEL_SIZE,
                applet.height
        );

        GLabel tpsLabel = new GLabel(
                applet,
                PADDING,
                PADDING / 2,
                PADDING * 16,
                PADDING * 5 / 2
        );

        TIME_SPEED = new GTextField(
                applet,
                PADDING * 17,
                PADDING / 2,
                PADDING * 25,
                PADDING * 5 / 2
        );
        GButton apply = new GButton(
                applet,
                PADDING * 26,
                PADDING / 2,
                PADDING * 30,
                PADDING * 5 / 2,
                "Apply"
        );
        apply.addEventHandler(this, "timeSpeedChanged");
        tpsLabel.setText("Time speed multiplier");
        tpsLabel.setFont(SMALLER_FONT);
        TIME_SPEED.setFont(DEFAULT_FONT);
        TIME_SPEED.setText("0");
        panel.addControl(TIME_SPEED);
        panel.addControl(tpsLabel);
        panel.addControl(apply);
        GLabel ctLabel = new GLabel(
                applet,
                PADDING,
                PADDING * 7 / 2,
                PADDING * 140,
                PADDING * 11 / 2,
                "Current time"
        );

        ctLabel.setFont(SMALLER_FONT);

        CURRENT_TIME = new GTextField(
                applet,
                PADDING * 10,
                PADDING * 7 / 2,
                PADDING * 33,
                PADDING * 11 / 2
        );
        CURRENT_TIME.setFont(SMALLER_FONT);
        CURRENT_TIME.setTextEditEnabled(false);
        panel.addControl(CURRENT_TIME);
        panel.addControl(ctLabel);
        panel.setOpaque(false);
        return panel;
    }

    //G4P GUI lib calls handlers via reflection. Handler for Apply button for time speed changes.
    public void timeSpeedChanged(GButton button, GEvent event) {
        if (event == GEvent.CLICKED) {
            int tpd = 0;
            if (!TIME_SPEED.getText().trim().isEmpty()) {
                try {
                    tpd = DFined.Util.constrain(
                            0,
                            SimulationParameters.MAX_TIME_SPEED,
                            Integer.parseInt(TIME_SPEED.getText())
                    );
                } catch (NumberFormatException e) {
                }
            }
            TIME_SPEED.setText(Integer.toString(tpd));
            Physics.setTimeSpeed(tpd);
        }
    }

    //Filter function for searching relevant planets by name
    public Iterator<CelestialBody> updatePlanets(SolarSystemState system, String filter) {
        if (!filter.isEmpty()) {
            return system.get().stream().filter(
                    state -> state.getName().toLowerCase().contains(filter.toLowerCase())
            ).iterator();
        }
        return system.iterator();
    }

    //Function for adding planet selector buttons to left panel after a search
    public void addPlanetsToLeft(GPanel panel, PApplet applet, Iterator<CelestialBody> planets) {
        int i = 0;
        for (; planets.hasNext(); i++) {
            int startY = TEXT_SIZE + PADDING + (TEXT_SIZE / 3 + PADDING) * i;
            GButton planet = new GButton(
                    applet,
                    PADDING,
                    startY,
                    panel.getWidth() - PADDING * 2,
                    startY + TEXT_SIZE / 3,
                    planets.next().getName()
            );
            planet.setFont(DEFAULT_FONT);
            planet.addEventHandler(this, "planetPressed");
            panel.addControl(planet);
        }
    }

    //Initialization of right gui panel
    public GPanel constructInfoPanel(PApplet applet) {
        GPanel panel = new GPanel(
                applet,
                applet.width - RIGHT_PANEL_SIZE,
                0,
                applet.width,
                applet.height,
                ""
        );
        panel.setDraggable(false);
        NAME = new GTextField(
                applet,
                PADDING,
                panel.getTabHeight() + PADDING,
                RIGHT_PANEL_SIZE - PADDING,
                panel.getTabHeight() + PADDING + TEXT_SIZE / 3
        );
        NAME.setFont(DEFAULT_FONT);
        NAME.setTextEditEnabled(false);
        MASS = new GTextField(
                applet,
                RIGHT_PANEL_SIZE / 2,
                panel.getTabHeight() + PADDING * 2 + TEXT_SIZE / 4,
                RIGHT_PANEL_SIZE - PADDING,
                panel.getTabHeight() + PADDING * 2 + TEXT_SIZE * 2 / 4
        );
        RADIUS = new GTextField(
                applet,
                RIGHT_PANEL_SIZE / 2,
                panel.getTabHeight() + PADDING * 3 + TEXT_SIZE * 2 / 4,
                RIGHT_PANEL_SIZE - PADDING,
                panel.getTabHeight() + PADDING * 3 + TEXT_SIZE * 3 / 4
        );
        RADIUS.setTextEditEnabled(false);
        GLabel mass = new GLabel(
                applet,
                PADDING,
                panel.getTabHeight() + PADDING * 2 + TEXT_SIZE / 4,
                RIGHT_PANEL_SIZE / 2 - PADDING,
                panel.getTabHeight() + PADDING * 2 + TEXT_SIZE * 2 / 4,
                "Mass(*10^16 kg):"
        );
        GLabel radius = new GLabel(
                applet,
                PADDING,
                panel.getTabHeight() + PADDING * 3 + TEXT_SIZE * 2 / 4,
                RIGHT_PANEL_SIZE / 2 - PADDING,
                panel.getTabHeight() + PADDING * 3 + TEXT_SIZE * 3 / 4,
                "Radius(m)"
        );
        panel.addControl(NAME);
        panel.addControl(MASS);
        panel.addControl(RADIUS);
        panel.addControl(mass);
        panel.addControl(radius);

        int vS = TEXT_SIZE * 3 / 4 + PADDING * 5;
        VX = new GTextField(
                applet,
                RIGHT_PANEL_SIZE / 2,
                vS,
                RIGHT_PANEL_SIZE - PADDING,
                vS + TEXT_SIZE / 3
        );
        GLabel vx = new GLabel(applet, PADDING, vS, RIGHT_PANEL_SIZE / 2 - PADDING, vS + TEXT_SIZE / 3);
        vx.setText("Vx (1000 km/s)");
        panel.addControl(vx);
        VX.setFont(SMALLER_FONT);
        panel.addControl(VX);
        vS += TEXT_SIZE / 3 + PADDING;
        VY = new GTextField(
                applet,
                RIGHT_PANEL_SIZE / 2,
                vS,
                RIGHT_PANEL_SIZE - PADDING,
                vS + TEXT_SIZE / 3
        );
        VY.setFont(SMALLER_FONT);
        panel.addControl(VY);
        GLabel vy = new GLabel(applet, PADDING, vS, RIGHT_PANEL_SIZE / 2 - PADDING, vS + TEXT_SIZE / 3);
        vy.setText("Vy (1000 km/s)");
        panel.addControl(vy);
        vS += TEXT_SIZE / 3 + PADDING;
        VZ = new GTextField(
                applet,
                RIGHT_PANEL_SIZE / 2,
                vS,
                RIGHT_PANEL_SIZE - PADDING,
                vS + TEXT_SIZE / 3
        );
        VZ.setFont(SMALLER_FONT);
        panel.addControl(VZ);
        VX.addEventHandler(this, "infoLostFocus");
        VY.addEventHandler(this, "infoLostFocus");
        VZ.addEventHandler(this, "infoLostFocus");
        GLabel vz = new GLabel(applet, PADDING, vS, RIGHT_PANEL_SIZE / 2 - PADDING, vS + TEXT_SIZE / 3);
        vz.setText("Vz (1000 km/s)");
        panel.addControl(vz);
        vS += TEXT_SIZE / 3 + PADDING;
        GButton apply = new GButton(
                applet,
                PADDING * 3,
                vS,
                RIGHT_PANEL_SIZE - PADDING * 3,
                vS + TEXT_SIZE / 3,
                "Apply Changes"
        );
        apply.addEventHandler(this, "applyChanges");
        panel.addControl(apply);
        vS += TEXT_SIZE / 3 + PADDING;
        GLabel makeBody = new GLabel(applet, PADDING, vS, RIGHT_PANEL_SIZE - PADDING, vS + TEXT_SIZE / 3);
        makeBody.setFont(DEFAULT_FONT);
        makeBody.setText("Copy selected body:");
        vS += TEXT_SIZE / 3 + PADDING;
        GButton copy = new GButton(
                applet,
                PADDING * 3,
                vS,
                RIGHT_PANEL_SIZE - PADDING * 3,
                vS + TEXT_SIZE / 3,
                "Copy selected body"
        );
        copy.addEventHandler(this, "copyBodyEvent");
        vS += TEXT_SIZE / 3 + PADDING;
        GLabel pasteLabel = new GLabel(
                applet,
                PADDING * 4,
                vS,
                RIGHT_PANEL_SIZE - PADDING * 3,
                vS + TEXT_SIZE / 3,
                "Then click location to paste"
        );
        panel.addControl(copy);
        panel.addControl(makeBody);
        panel.addControl(pasteLabel);
        GButton reset = new GButton(
                applet,
                PADDING * 3,
                applet.height - PADDING * 6,
                RIGHT_PANEL_SIZE - PADDING * 3,
                applet.height - PADDING * 4,
                "RESET"
        );
        reset.setFont(DEFAULT_FONT);
        reset.addEventHandler(this, "reset");
        panel.addControl(reset);
        GButton exit = new GButton(
                applet,
                PADDING * 3,
                applet.height - PADDING * 3,
                RIGHT_PANEL_SIZE - PADDING * 3,
                applet.height - PADDING,
                "EXIT"
        );
        exit.setFont(DEFAULT_FONT);
        exit.addEventHandler(this, "exit");
        panel.addControl(exit);
        return panel;
    }

    //G4P GUI lib calls handlers via reflection. Handler for exit button.
    public void infoLostFocus(GTextField field, GEvent event) {
        if (event == GEvent.CHANGED || event == GEvent.GETS_FOCUS) {
            lockInfo = true;
        }
    }

    //G4P GUI lib calls handlers via reflection. Handler for apply changes button.
    public void applyChanges(GButton button, GEvent event) {
        Model.getRenderer().getFocus().setVelocity(
                new Vector3D(
                        Double.parseDouble(VX.getText()),
                        Double.parseDouble(VY.getText()),
                        Double.parseDouble(VZ.getText())
                )
        );
        lockInfo = false;
    }

    //G4P GUI lib calls handlers via reflection. Handler for reset button.
    public void reset(GButton button, GEvent event) {
        Physics.resetTime();
        Model.getSystem().clear();
        Model.getInstance().setupDefaultPlanets();
        Model.getRenderer().setFocus(Model.getSystem().get(0));
    }

    //G4P GUI lib calls handlers via reflection. Handler for exit button.
    public void exit(GButton button, GEvent event) {
        button.getPApplet().exit();
    }

    //Function which updates info displayed about body when focus is changed and on tick
    public void updateInfo(CelestialBody body, boolean force) {
        if ((!lockInfo && !VX.hasFocus() && !VY.hasFocus() && !VZ.hasFocus() && !MASS.hasFocus()) || force) {
            NAME.setText(body.getName());
            MASS.setText(Double.toString(body.getMass()));
            RADIUS.setText(Double.toString(body.getRadius()));
            VX.setText(String.format("%.7f", body.getVelocity().getX()));
            VY.setText(String.format("%.7f", body.getVelocity().getY()));
            VZ.setText(String.format("%.7f", body.getVelocity().getZ()));
        }
    }

    //Initialization of left gui panel
    public SlidePanel constructMainPanel(PApplet applet, Iterator planets) {
        SlidePanel panel = new SlidePanel(
                applet,
                0,
                0,
                LEFT_PANEL_SIZE,
                applet.height,
                "",
                TEXT_SIZE - PADDING
        );
        panel.setDraggable(false);
        if (SEARCH == null) {
            GPanel searchPanel = new GPanel(applet, 0, 0, panel.getWidth(), TEXT_SIZE, "Celestial Bodies");
            GLabel label = new GLabel(
                    applet,
                    PADDING,
                    PADDING,
                    panel.getWidth() - PADDING,
                    PADDING * 3,
                    "Planet filter:"
            );
            label.setFont(SMALLER_FONT);
            SEARCH = new GTextField(applet, PADDING, PADDING * 4, panel.getWidth() - PADDING, PADDING * 6);
            SEARCH.addEventHandler(this, "searchTyped");
            SEARCH.setFont(DEFAULT_FONT);
            searchPanel.addControl(SEARCH);
            searchPanel.addControl(label);
            panel.addControl(searchPanel);
        }
        addPlanetsToLeft(panel, applet, planets);
        return panel;
    }

    //G4P GUI lib calls handlers via reflection. Handler for planet selector buttons in list on left panel.
    public void planetPressed(GButton button, GEvent event) {
        if (event == GEvent.CLICKED || event == GEvent.PRESSED) {
            Model.getRenderer().setFocus(
                    Model.getSystem().get().parallelStream().filter(
                            state -> state.getName().equals(button.getText())
                    ).findFirst().get()
            );
        }
    }

    //G4P GUI lib calls handlers via reflection. Handler for search filed being typed.
    public void searchTyped(GTextField textField, GEvent event) {
        if (event == GEvent.CHANGED) {
            reconstructMainPanel();
        }
    }

    //Function for reconstructing list of planet selector buttons on left panel after the search filter is changed.
    public void reconstructMainPanel() {
        String filter = SEARCH.getText();
        PApplet applet = SEARCH.getPApplet();
        LEFT_PANEL.dispose();
        LEFT_PANEL.resetSlider();
        addPlanetsToLeft(LEFT_PANEL, applet, updatePlanets(Model.getSystem(), filter.trim()));
    }

    //G4P GUI lib calls handlers via reflection. Handler for copy body button.
    public void copyBodyEvent(GButton textField, GEvent event) {
        if (event == GEvent.CLICKED) {
            copy = Model.getRenderer().getFocus();
        }
    }
}