/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class FloatMouseListener implements MouseListener {

    private final String title;
    private final JTextField field;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private final FloatSetter setter;

    public FloatMouseListener(String title, JTextField field, double min, double max, FloatSetter setter) {
        this.title = title;
        this.field = field;
        this.min = min;
        this.max = max;
        this.setter = setter;
    }

    public String getTitle() {
        return title;
    }

    public JTextField getField() {
        return field;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        FloatDialog.showDialog(title, field, min, max, setter);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // NOP
    }

}
