package DFined.gui;

import DFined.core.Model;
import DFined.core.Renderer;
import g4p_controls.GViewListener;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import processing.core.PGraphics;

public class ViewHandler extends GViewListener {

    //Handler for the main 3D view for Per-tick events. Used mainly for rendering.
    @Override
    public void update() {
        PGraphics graphics = getGraphics();
        graphics.beginDraw();
        Model.getRenderer().render(graphics, Model.getSystem(), mouseX() - width() / 2.f, mouseY() - height() / 2.f);
        graphics.endDraw();
        invalidate();
    }

    //Handler for the main 3D view for mouse drag events.
    @Override
    public void mouseDragged() {
        Model.getRenderer().mouseDragged(mouseX(), mouseY());
    }

    //Handler for the main 3D view for mouse press events.
    @Override
    public void mousePressed() {
        Renderer renderer = Model.getRenderer();
        if (button() == LEFT) {
            renderer.mousePressed(mouseX(), mouseY());
        }
        Vector3D pos = renderer.mouseToLocal(mouseX() - width() / 2.f, mouseY() - height() / 2.f);
        if (Model.getGui().copy != null) {
            Model.getRenderer().setFocus(
                    Model.getSystem().add(
                            Model.getGui().copy.clone(pos.add(renderer.getFocus().getPosition()))
                    )
            );
            Model.getGui().copy = null;
            Model.getGui().reconstructMainPanel();

        }
    }

    //Handler for the main 3D view for mouse entered events. Used to exclude bugs with the rest of the gui
    @Override
    public void mouseEntered() {
        getPApplet().noCursor();
        Model.getRenderer().mousePressed(mouseX(), mouseY());
        invalidate();
    }

    //Handler for the main 3D view for mouse exited events.
    @Override
    public void mouseExited() {
        getPApplet().cursor();
    }
}
