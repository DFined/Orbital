package DFined.gui;

import g4p_controls.GEvent;
import g4p_controls.GPanel;
import g4p_controls.GSlider;
import processing.core.PApplet;

public class SlidePanel extends GPanel {
    private static final int SLIDER_THICKNESS = 10;
    private static float sliderVal = 0f;
    GSlider slider;

    public SlidePanel(PApplet theApplet, float xs, float ys, float xe, float ye, String label, int offset) {
        super(theApplet, xs, ys, xe, ye, label);
        width = xe - xs;
        slider = new GSlider(
                theApplet,
                width - SLIDER_THICKNESS,
                SLIDER_THICKNESS * 3 + offset,
                theApplet.height + width - SLIDER_THICKNESS * 6 - offset,
                SLIDER_THICKNESS * 4 + offset,
                SLIDER_THICKNESS
        );
        slider.setRotation(PI / 2);
        slider.addEventHandler(this, "onSliderUpdate");
        slider.setValue(0f);

        this.addControl(slider);
    }

    public void onSliderUpdate(GSlider slider, GEvent event) {
        float dVal = slider.getValueF() - sliderVal;
        int tabSize = GUI.TEXT_SIZE / 3 + GUI.PADDING;
        int capacity = ((int) this.height - GUI.TEXT_SIZE) / tabSize;
        int total = this.children.size() - 2;
        float offset = (total - capacity) * tabSize * dVal;

        for (g4p_controls.GAbstractControl child : children) {
            if (!(child instanceof GPanel || child instanceof GSlider)) {
                child.moveTo(child.getX(), child.getY() - offset);
            }
        }
        sliderVal = slider.getValueF();
    }

    public void resetSlider(){
        sliderVal = 0f;
        slider.setValue(0f);
    }

    public void dispose() {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (!(children.get(i) instanceof GPanel) && !(children.get(i) instanceof GSlider)) {
                children.get(i).dispose();
                children.remove(i);
            }
        }
    }
}
