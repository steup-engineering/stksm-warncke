/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public class SignButton extends KeyButtonAction {

    public SignButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DialogActionListener dialogActions, String currentText) {
        if (currentText.startsWith("-")) {
            return currentText.substring(1);
        } else {
            return "-" + currentText;
        }
    }
}
