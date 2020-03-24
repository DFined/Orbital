package DFined.gui;

import DFined.Physics.BodyState;
import DFined.Physics.CelestialBody;
import DFined.Physics.Physics;
import DFined.Physics.SolarSystemState;
import DFined.core.Model;
import DFined.core.Parameters;
import g4p_controls.*;
import processing.core.PApplet;

import java.awt.*;
import java.util.Iterator;

import static processing.core.PConstants.P3D;

public class GUI {
    public CelestialBody copy = null;
    public final GPanel RIGHT_PANEL;
    private SlidePanel LEFT_PANEL;
    private GPanel BOTTOM_PANEL;
    public final GView VIEW;
    private static final int LEFT_PANEL_SIZE = 200;
    private static final int RIGHT_PANEL_SIZE = 300;
    public static final int TEXT_SIZE = 100;
    public static final int PADDING = 15;
    private static final int BOTTOM_PANEL_SIZE = PADDING * 3;
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 22);
    private static GTextField NAME;
    private static GTextField MASS;
    private static GTextField RADIUS;
    private static GTextField SEARCH;
    private static GTextField TPSDISPLAY;

    public GUI(PApplet applet) {
        G4P.setCtrlMode(GControlMode.CORNERS);
        this.VIEW = new GView(
                applet,
                LEFT_PANEL_SIZE,
                0,
                applet.width - RIGHT_PANEL_SIZE,
                applet.height - BOTTOM_PANEL_SIZE,
                P3D
        );
        this.VIEW.addListener(new ViewHandler());
        this.RIGHT_PANEL = constructInfoPanel(applet);
        this.LEFT_PANEL = constructMainPanel(applet, Model.getSystem().iterator());
        this.BOTTOM_PANEL = constructBottomPanel(applet);

    }

    private GPanel constructBottomPanel(PApplet applet) {
        int WIDTH = applet.width - RIGHT_PANEL_SIZE - LEFT_PANEL_SIZE;
        GPanel panel = new GPanel(
                applet,
                LEFT_PANEL_SIZE,
                applet.height - BOTTOM_PANEL_SIZE,
                applet.width - RIGHT_PANEL_SIZE,
                applet.height
        );
        GTextField tpsDisplay = new GTextField(applet, PADDING, PADDING / 2, PADDING * 8, PADDING * 5 / 2);
        tpsDisplay.addEventHandler(this, "timeSpeedChanged");
        tpsDisplay.setFont(DEFAULT_FONT);
        panel.addControl(tpsDisplay);
        panel.setOpaque(false);
        return panel;
    }

    public void timeSpeedChanged(GTextField field, GEvent event) {
        if (event == GEvent.CHANGED) {
            int tpd = 0;
            if (!field.getText().trim().isEmpty()) {
                tpd = Integer.parseInt(field.getText());
            }
            Physics.setPhysicsTicksPerDraw(tpd);
        }
    }

    public Iterator<BodyState> updatePlanets(SolarSystemState system, String filter) {
        if (!filter.isEmpty()) {
            return system.get().stream().filter(
                    state -> state.getBody().getName().toLowerCase().contains(filter.toLowerCase())
            ).iterator();
        }
        return system.iterator();
    }

    public void addPlanetsToLeft(GPanel panel, PApplet applet, Iterator<BodyState> planets) {
        int i = 0;
        for (; planets.hasNext(); i++) {
            int startY = TEXT_SIZE + PADDING + (TEXT_SIZE / 3 + PADDING) * i;
            GButton planet = new GButton(
                    applet,
                    PADDING,
                    startY,
                    panel.getWidth() - PADDING * 2,
                    startY + TEXT_SIZE / 3,
                    planets.next().getBody().getName()
            );
            planet.setFont(DEFAULT_FONT);
            planet.addEventHandler(this, "planetPressed");
            panel.addControl(planet);
        }
    }

    public GPanel constructInfoPanel(PApplet applet) {
        GPanel panel = new GPanel(applet, applet.width - RIGHT_PANEL_SIZE, 0, applet.width, applet.height, "");
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
        MASS.setTextEditEnabled(false);
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

        panel.addControl(copy);
        panel.addControl(makeBody);

        return panel;
    }

    public void updateInfo(CelestialBody body) {
        NAME.setText(body.getName());
        MASS.setText(Double.toString(body.getMass()));
        RADIUS.setText(Double.toString(body.getRadius()));
    }

    public SlidePanel constructMainPanel(PApplet applet, Iterator planets) {
        SlidePanel panel = new SlidePanel(applet, 0, 0, LEFT_PANEL_SIZE, applet.height, "", TEXT_SIZE - PADDING);
        panel.setDraggable(false);
        if (SEARCH == null) {
            GPanel searchPanel = new GPanel(applet, 0, 0, panel.getWidth(), TEXT_SIZE, "Celestial Bodies");
            SEARCH = new GTextField(applet, PADDING, TEXT_SIZE / 2 - 15, panel.getWidth() - PADDING, TEXT_SIZE / 2 + 15);
            SEARCH.addEventHandler(this, "searchTyped");
            SEARCH.setFont(DEFAULT_FONT);
            searchPanel.addControl(SEARCH);
            panel.addControl(searchPanel);
        }
        addPlanetsToLeft(panel, applet, planets);
        return panel;
    }

    public void planetPressed(GButton button, GEvent event) {
        if (event == GEvent.CLICKED || event == GEvent.PRESSED) {
            Model.getRenderer().setFocus(
                    Model.getSystem().get().parallelStream().filter(
                            state -> state.getBody().getName().equals(button.getText())
                    ).findFirst().get().getBody()
            );
        }
    }

    public void searchTyped(GTextField textField, GEvent event) {
        if (event == GEvent.CHANGED) {
            reconstructMainPanel();
        }
    }

    public void reconstructMainPanel() {
        String filter = SEARCH.getText();
        PApplet applet = SEARCH.getPApplet();
        LEFT_PANEL.dispose();
        LEFT_PANEL.resetSlider();
        addPlanetsToLeft(LEFT_PANEL, applet, updatePlanets(Model.getSystem(), filter.trim()));
    }

    public void copyBodyEvent(GButton textField, GEvent event) {
        if (event == GEvent.CLICKED) {
            copy = Model.getRenderer().getFocus();
        }
    }
}