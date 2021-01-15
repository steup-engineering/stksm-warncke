/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class FloatMouseListener implements MouseListener {

    private final Window owner;
    private final String title;
    private final JTextField field;
    private final double min;
    private final double max;
    private final DecimalFormat format;
    private final FloatSetter setter;

    public FloatMouseListener(Window owner, String title, JTextField field, double min, double max, DecimalFormat format, FloatSetter setter) {
        this.owner = owner;
        this.title = title;
        this.field = field;
        this.min = min;
        this.max = max;
        this.format = format;
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
        FloatDialog.showDialog(owner, title, field, min, max, format, setter);
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
