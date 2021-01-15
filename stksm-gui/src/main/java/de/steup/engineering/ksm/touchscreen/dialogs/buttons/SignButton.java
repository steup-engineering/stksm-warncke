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
public class SignButton extends KeyButtonAction {

    public SignButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DecimalFormatSymbols decimalFormatSymbols, DialogActionListener dialogActions, String currentText) {
        if (currentText.startsWith("-")) {
            return currentText.substring(1);
        } else {
            return "-" + currentText;
        }
    }
}
