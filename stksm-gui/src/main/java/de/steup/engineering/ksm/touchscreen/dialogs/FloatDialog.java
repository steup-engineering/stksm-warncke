/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.Window;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class FloatDialog extends NumDialog {

    private static final long serialVersionUID = -4456858803174622325L;

    private final double min;
    private final double max;
    private final DecimalFormat format;
    private final FloatSetter setter;

    public static void showDialog(Window owner, String title, JTextField dest, double min, double max, DecimalFormat format, FloatSetter setter) {
        NumDialog dlg = new FloatDialog(owner, title, dest, min, max, format, setter);
        dlg.setVisible(true);
    }

    public FloatDialog(Window owner, String title, JTextField dest, double min, double max, DecimalFormat format, FloatSetter setter) {
        super(owner, title, dest, format.getDecimalFormatSymbols());
        this.min = min;
        this.max = max;
        this.format = format;
        this.setter = setter;
    }

    @Override
    public void okAction() {
        double val;

        try {
            val = format.parse(inputField.getText()).doubleValue();
        } catch (ParseException ex) {
            statusLabel.setText("Ungültige Gleitkommazahl.");
            return;
        }

        if (val > max) {
            statusLabel.setText(String.format("Maximalwert %s überschritten.", format.format(max)));
            return;
        }

        if (val < min) {
            statusLabel.setText(String.format("Minimalwert %s unterschritten.", format.format(min)));
            return;
        }

        if (dest != null) {
            dest.setText(format.format(val));
        }
        if (setter != null) {
            setter.setValue(val);
        }
        dispose();
    }
}
