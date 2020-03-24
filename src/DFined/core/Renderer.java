package DFined.core;

import DFined.Physics.CelestialBody;
import DFined.Physics.Physics;
import DFined.Physics.SolarSystemState;
import DFined.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

public class Renderer {
    private float scale = 0.1f;
    private static final double PI = 3.1415926535;
    private float pitch = (float) PI / 2;
    private float yaw = (float) 0;
    private boolean isDragging = false;
    private Vector2D lastMouse = new Vector2D(0, 0);
    private PApplet applet;
    CelestialBody focus;

    public CelestialBody getFocus() {
        return focus;
    }

    PShape mouseSphere;

    public Renderer(PApplet applet) {
        this.applet = applet;
        mouseSphere = applet.createShape(applet.SPHERE, 10);
        mouseSphere.setStroke(255);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    private static int i = 0;

    public void render(PGraphics graphics, SolarSystemState system, float mouseX, float mouseY) {
        graphics.background(0);

        graphics.text(applet.frameRate, 20, 50);
        graphics.text(scale, 20, 100);
        graphics.text(Util.formatSeconds(Math.round(Physics.getTime())), 20, 150);
        graphics.text(Physics.getPhysicsTicksPerDraw(), 20, 200);
        if (!focus.isCentral()) {
            graphics.text(focus.getOrbit().toString(), 20, 250);
        }

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
            {
                graphics.translate((float) (state.getX() - focus.getPosition().getX()), (float) (state.getY() - focus.getPosition().getY()), (float) (state.getZ() - focus.getPosition().getZ()));
                state.draw(this, graphics);
            }
            graphics.popMatrix();
        }
    }

    public void setFocus(CelestialBody focus) {
        this.focus = focus;
        Model.getGui().updateInfo(focus);
    }


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

    public Vector3D mouseToLocal(float mouseX, float mouseY) {
        Vector3D pos = new Vector3D(mouseX, 0, mouseY);
        pos = pos.scalarMultiply(PI / getScale());
        float angle = -getYaw();
        Vector3D xRot = new Vector3D(Math.cos(angle), 0, -Math.sin(angle));
        Vector3D zRot = new Vector3D(Math.sin(angle), 0, Math.cos(angle));
        pos = new Vector3D(pos.dotProduct(xRot), 0, pos.dotProduct(zRot));
        return pos;
    }

    public void mousePressed(int x, int y) {
        lastMouse = new Vector2D(x, y);
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
