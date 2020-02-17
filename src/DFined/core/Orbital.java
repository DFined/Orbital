package DFined.core;

import DFined.Physics.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Orbital extends PApplet {
    private static Renderer renderer = new Renderer();
    private static final Logger logger = Logger.getLogger("Orbital");
    private static final SolarSystemState system = new SolarSystemState();
    private static long lastTick = 0;
    @Override
    public void settings() {
        size(1000,900,P3D);
    }

    public static void loadResources(){
        File data = new File("resources/Data");
        Collection<File> resources = FileUtils.listFiles(data, FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter());
        for (File file: resources) {
            try {
                BodyPresets.addPreset(FileUtils.readFileToString(file,"utf8"));
            } catch (IOException e) {
                logger.log(Level.WARNING, String.format("Error loading resource \'%s\'",file.getName()), e);
            }
        }
    }

    public void setup(){
        loadResources();

        stroke(255);

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.SUN))
                .setKinetics(new BodyState(true, 0, 0))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.MERCURY))
            .setKinetics(new BodyState(false,69816900000L,38860f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.VENUS))
                .setKinetics(new BodyState(false,-108940000000L,  -(34780f)))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.EARTH))
                .setKinetics(new BodyState(false, 152098233000L, 29292f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.MARS))
                .setKinetics(new BodyState(false, -249200000000L, -22000f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.JUPITER))
                .setKinetics(new BodyState(false, 816620000000L, 12440f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.SATURN))
                .setKinetics(new BodyState(false,-1514500000000L, -9090f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.URANUS))
                .setKinetics(new BodyState(	false,3003620000000L, 6490f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.NEPTUNE))
                .setKinetics(new BodyState(	false, -4545670000000L, -5370f))
                .initGraphics(this).getState()
        );

        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.PLUTO))
                .setKinetics(new BodyState(	false, -7375930000000d,-3710f))
                .initGraphics(this).getState()
        );
        system.add(new CelestialBody(BodyPresets.getPreset(BodyPresets.SUN))
                .setKinetics(new BodyState(true, -722300000000d, 5000))
                .initGraphics(this).getState()
        );

        renderer.setFocus(system.get(0).getBody());
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public Vector3D randomV3D(float minC, float maxC){
        return new Vector3D(random(minC,maxC),0,random(minC,maxC));
    }

    public void draw(){
        double dt = ((float)(millis()-lastTick))/1000;
        lastTick = millis();
        Physics.tick(dt,system);
        renderer.render(this, system);
    }

    public static void main(String[] args){
        PApplet.main("DFined.core.Orbital",args);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        if (event.getCount() > 0) {
            renderer.setScale(renderer.getScale()/1.1f);
        } else {
            renderer.setScale(renderer.getScale()*1.1f);
        }
    }

    private static int focus = 0;

    @Override
    public void keyPressed(KeyEvent event) {
        if (keyCode == UP){
            Physics.incrementTPD();
        }else if(keyCode == DOWN){
            Physics.decrementTPD();
        }else{
            focus = (focus+1)%system.size();
            renderer.setFocus(system.get(focus).getBody());
        }
    }


    @Override
    public void mouseDragged(MouseEvent event) {
        renderer.mouseDragged(event);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if(mouseButton==LEFT){
            renderer.mousePressed(event);
        }
    }

}
