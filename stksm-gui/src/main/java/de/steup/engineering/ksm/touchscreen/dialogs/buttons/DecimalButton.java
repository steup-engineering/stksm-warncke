/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs.buttons;

import de.steup.engineering.ksm.touchscreen.dialogs.DialogActionListener;
import de.steup.engineering.ksm.touchscreen.dialogs.KeyButtonAction;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author sascha
 */
public class DecimalButton extends KeyButtonAction {

    public DecimalButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DecimalFormatSymbols decimalFormatSymbols, DialogActionListener dialogActions, String currentText) {
        if (decimalFormatSymbols == null) {
            return currentText;
        }
        return currentText + decimalFormatSymbols.getDecimalSeparator();
    }
}
