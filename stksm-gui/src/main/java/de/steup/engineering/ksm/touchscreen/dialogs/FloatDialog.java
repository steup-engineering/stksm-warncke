/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.Window;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class FloatDialog extends NumDialog {

    private static final long serialVersionUID = -4456858803174622325L;

    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private FloatSetter setter = null;

    public static void showDialog(Window owner, String title, JTextField dest, double min, double max, FloatSetter setter) {
        NumDialog dlg = new FloatDialog(owner, title, dest, min, max, setter);
        dlg.setVisible(true);
    }

    public FloatDialog(Window owner, String title, JTextField dest, double min, double max, FloatSetter setter) {
        super(owner, title, dest);
        this.min = min;
        this.max = max;
        this.setter = setter;
    }

    @Override
    public void okAction() {
        double val;

        try {
            val = Double.parseDouble(inputField.getText());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid Float Number.");
            return;
        }

        if (val > max) {
            statusLabel.setText(String.format("Maximum value of %s exceeded.", Double.toString(max)));
            return;
        }

        if (val < min) {
            statusLabel.setText(String.format("Minimum value of %s exceeded.", Double.toString(min)));
            return;
        }

        if (dest != null) {
            dest.setText(Double.toString(val));
        }
        if (setter != null) {
            setter.setValue(val);
        }
        dispose();
    }
}
