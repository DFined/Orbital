package DFined.core;

import DFined.Physics.CelestialBody;
import DFined.Physics.Physics;
import DFined.Physics.SolarSystemState;
import DFined.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import processing.core.*;

import java.util.HashMap;

public class Renderer {
    private float scale = 0.1f;
    private static final double PI = 3.1415926535;
    private float pitch = (float) PI / 2;
    private float yaw = (float) 0;
    private boolean isDragging = false;
    private Vector2D lastMouse = new Vector2D(0, 0);
    private PApplet applet;
    private HashMap<String, PImage> textureBank;
    CelestialBody focus;

    //Focus is the celestial body on which the camera is centered
    public CelestialBody getFocus() {
        return focus;
    }

    PShape mouseSphere;

    public Renderer(PApplet applet) {
        this.applet = applet;
        mouseSphere = applet.createShape(applet.SPHERE, 10);
        mouseSphere.setStroke(255);
        this.textureBank = new HashMap<>();
    }

    //Load texture with caching
    private PImage getOrLoadTexture(String texture) {
        if (!textureBank.containsKey(texture)) {
            textureBank.put(texture, this.applet.loadImage(texture));
        }
        return textureBank.get(texture);
    }

    //Initialize required PGraphics elements for rendering.
    public void initGraphics(CelestialBody body) {
        PShape shape = applet.createShape(applet.SPHERE, (float) (body.getRadius() / Physics.DISTANCE_SCALE));
        shape.setStroke(false);
        shape.setTexture(getOrLoadTexture(body.getTexture()));
        PShape marker = applet.createShape(applet.SPHERE, 20);
        marker.setStroke(false);
        marker.setTexture(getOrLoadTexture(body.getTexture()));
        body.setShape(shape);
        body.setMarker(marker);
    }


    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    private static int i = 0;

    //Main rendering method for the app. Utilizes opengl wrapper from PGraphics heavily
    public void render(PGraphics graphics, SolarSystemState system, float mouseX, float mouseY) {
        graphics.textSize(50);
        graphics.background(0);

        graphics.perspective((float) (PI / 3.0), (float) graphics.width / graphics.height, 1, 1000000000);
        graphics.fill(255);
        graphics.lights();
        graphics.translate(graphics.width / 2, graphics.height / 2, -2000);

        graphics.rotateX(-pitch);
        graphics.rotateY(-yaw);

        graphics.scale(scale);

        graphics.pushMatrix();
        Vector3D mPos = mouseToLocal(mouseX, mouseY);
        graphics.translate((float) mPos.getX(), (float) mPos.getY(), (float) mPos.getZ());
        graphics.scale(1f / scale);
        graphics.shape(mouseSphere);
        graphics.popMatrix();

        graphics.noFill();

        //Render the planets
        for (CelestialBody state : system) {
            graphics.pushMatrix();
            graphics.translate(
                    (float) (state.getX() - focus.getPosition().getX()),
                    (float) (state.getY() - focus.getPosition().getY()),
                    (float) (state.getZ() - focus.getPosition().getZ())
            );

            if (state.getShape() == null) {
                initGraphics(state);
            }

            draw(state, graphics);
            graphics.popMatrix();
        }
    }

    //Self-render method. Draws the body. All necessary transforms are handled by the renderer.
    public void draw(CelestialBody body, PGraphics graphics) {
        graphics.pushMatrix();
        graphics.shape(body.getShape());
        graphics.scale(1.f / getScale());
        graphics.shape(body.getMarker());
        if (SimulationParameters.isDrawLabels()) {
            graphics.rotateX(PConstants.PI / 2);
            graphics.text(body.getName(), -body.getName().length() * 15, -80, 0);
        }
        graphics.popMatrix();
    }

    public void setFocus(CelestialBody focus) {
        this.focus = focus;
        Model.getGui().updateInfo(focus, true);
    }

    //Mouse drag handler for pitch/yaw changes. Called externally from gui.ViewHandler
    public void mouseDragged(int x, int y) {
        if (isDragging) {
            double dx = x - this.lastMouse.getX();
            double dy = y - this.lastMouse.getY();
            pitch = (float) Util.constrain(-PI / 2, PI / 2, pitch + dy / 200);
            yaw -= dx / 200;
        } else {
            isDragging = true;
        }
        lastMouse = new Vector2D(x, y);
    }

    //Transforms mouse coordinate in screen space to the coordinate in the Solar System plane
    public Vector3D mouseToLocal(float mouseX, float mouseY) {
        Vector3D pos = new Vector3D(mouseX, 0, mouseY);
        pos = pos.scalarMultiply(PI / getScale());
        float angle = -getYaw();
        Vector3D xRot = new Vector3D(Math.cos(angle), 0, -Math.sin(angle));
        Vector3D zRot = new Vector3D(Math.sin(angle), 0, Math.cos(angle));
        pos = new Vector3D(pos.dotProduct(xRot), 0, pos.dotProduct(zRot));
        return pos;
    }

    //Mouse press handler. Called externally from gui.ViewHandler
    public void mousePressed(int x, int y) {
        lastMouse = new Vector2D(x, y);
    }

    public float getYaw() {
        return yaw;
    }


}
