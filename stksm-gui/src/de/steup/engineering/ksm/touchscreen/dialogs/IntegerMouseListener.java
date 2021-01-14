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
public class IntegerMouseListener implements MouseListener {

    private final String title;
    private final JTextField field;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerMouseListener(String title, JTextField field, int min, int max) {
        this.title = title;
        this.field = field;
        this.min = min;
        this.max = max;
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
        IntegerDialog.showDialog(title, field, min, max);
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
