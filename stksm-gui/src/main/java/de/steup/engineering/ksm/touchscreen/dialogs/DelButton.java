/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public class DelButton extends KeyButtonAction {

    public DelButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DialogActionListener dialogActions, String currentText) {
        if (!currentText.isEmpty()) {
            currentText = currentText.substring(0, currentText.length() - 1);
        }
        return currentText;
    }
}
