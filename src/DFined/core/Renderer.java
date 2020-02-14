package DFined.core;

import DFined.Physics.*;
import DFined.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.List;

public class Renderer {
    private float scale = 0.1f;
    private static final double PI = 3.1415926535;
    private float pitch = (float)PI/2;
    private float yaw = (float)0;
    private boolean isDragging = false;
    private Vector2D lastMouse = new Vector2D(0,0);
    CelestialBody focus;
    public CelestialBody getFocus() {
        return focus;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    private static int i = 0;

    public void render(PApplet applet, SolarSystemState system){
        applet.background(0);

        applet.text(applet.frameRate,20,50);
        applet.text(scale,20,100);
        applet.text(Util.formatSeconds(Math.round(Physics.getTime())),20,150);
        applet.text(Physics.getPhysicsTicksPerDraw(),20,200);
        if(focus != system.get(0).getBody()) {
            applet.text(focus.getOrbit().toString(), 20, 250);
        }

        applet.perspective((float) (PI/3.0),(float)applet.width/applet.height,1,1000000000);
        applet.fill(255);
        applet.lights();
        applet.translate(applet.width/2, applet.height/2,  - 2000);
        applet.rotateX(-pitch);
        applet.rotateY(-yaw);
        applet.scale(scale);

        applet.noFill();

        //Render the planets
        for(BodyState state: system) {
            applet.pushMatrix();
            {
                applet.translate((float) (state.getX() - focus.getPosition().getX()), (float) (state.getY() - focus.getPosition().getY()), (float) (state.getZ() - focus.getPosition().getZ()));
                state.getBody().draw(this, applet);
            }
            applet.popMatrix();
        }

        //render the orbits
        applet.strokeWeight(2/this.scale);
        applet.pushMatrix();
        applet.translate((float) (-focus.getPosition().getX()), (float) (-focus.getPosition().getY()), (float) (-focus.getPosition().getZ()));
        //applet.line(0,0,0,200000,0,0);
        for(BodyState state: system) {
            if(!state.getBody().isCentral()) {
                applet.pushMatrix();
                {
                    Vector3D center = state.getBody().getOrbit().getAnchor().getPosition();
                    applet.translate((float) (center.getX()), (float) (center.getY()), (float) (center.getZ()));
                    applet.rotateX((float) Math.PI / 2);
          //          body.getOrbit().draw(applet);
                }
                applet.popMatrix();
            }
        }
        applet.popMatrix();
    }

    public void setFocus(CelestialBody focus) {
        this.focus = focus;
    }


    public void mouseDragged(MouseEvent event){
        if(isDragging) {
            double dx = event.getX() - this.lastMouse.getX();
            double dy = event.getY() - this.lastMouse.getY();
            pitch += dy / 200;
            yaw -= dx / 200;
        }else{
            isDragging = true;
        }
        lastMouse = new Vector2D(event.getX(),event.getY());
    }

    public void mousePressed(MouseEvent event){
        lastMouse = new Vector2D(event.getX(),event.getY());
    }
}
