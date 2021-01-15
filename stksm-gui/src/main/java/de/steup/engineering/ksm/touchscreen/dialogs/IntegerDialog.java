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
public class IntegerDialog extends NumDialog {

    private static final long serialVersionUID = -7380894324065576936L;

    private final int min;
    private final int max;

    public static void showDialog(Window owner, String title, JTextField dest, int min, int max) {
        NumDialog dlg = new IntegerDialog(owner, title, dest, min, max);
        dlg.setVisible(true);
    }

    public IntegerDialog(Window owner, String title, JTextField dest, int min, int max) {
        super(owner, title, dest, null);
        this.min = min;
        this.max = max;
    }

    @Override
    public void okAction() {
        int val;

        try {
            val = Integer.parseInt(inputField.getText());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Ungültige Integerzahl.");
            return;
        }

        if (val > max) {
            statusLabel.setText(String.format("Maximalwert %d überschritten.", max));
            return;
        }

        if (val < min) {
            statusLabel.setText(String.format("Minimalwert %d unterschritten.", min));
            return;
        }

        if (dest != null) {
            dest.setText(Integer.toString(val));
        }
        dispose();
    }
}
