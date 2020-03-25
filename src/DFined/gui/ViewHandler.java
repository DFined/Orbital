package DFined.gui;

import DFined.core.Model;
import DFined.core.Renderer;
import g4p_controls.GViewListener;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PGraphics;

public class ViewHandler extends GViewListener {

    @Override
    public void update() {
        PGraphics graphics = getGraphics();
        graphics.beginDraw();
        Model.getRenderer().render(graphics, Model.getSystem(), mouseX() - width()/2.f, mouseY() - height()/2.f);
        graphics.endDraw();
        invalidate();
    }

    @Override
    public void mouseDragged() {
        Model.getRenderer().mouseDragged(mouseX(), mouseY());
    }

    @Override
    public void mousePressed() {
        Renderer renderer = Model.getRenderer();
        if (button() == LEFT) {
            renderer.mousePressed(mouseX(), mouseY());
        }
        Vector3D pos = renderer.mouseToLocal(mouseX() - width()/2.f, mouseY() - height()/2.f);
        if (Model.getGui().copy != null) {
            Model.getSystem().add(
                    Model.getGui().copy.clone(pos.add(renderer.getFocus().getPosition()), getPApplet())
            );
            Model.getGui().copy = null;
            Model.getGui().reconstructMainPanel();
        }
    }

    @Override
    public void mouseMoved() {
        if(Model.getGui().copy != null){

        }
    }

    @Override
    public void mouseEntered() {
        getPApplet().noCursor();
        Model.getRenderer().mousePressed(mouseX(), mouseY());
        invalidate();
    }

    @Override
    public void mouseExited() {
        getPApplet().cursor();
    }
}
