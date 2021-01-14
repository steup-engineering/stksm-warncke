/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public class CancelButton extends KeyButtonAction {

    public CancelButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DialogActionListener dialogActions, String currentText) {
        dialogActions.cancelAction();
        return currentText;
    }
}
