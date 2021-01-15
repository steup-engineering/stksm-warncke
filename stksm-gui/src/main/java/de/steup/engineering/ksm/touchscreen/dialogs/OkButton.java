/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public class OkButton extends KeyButtonAction {

    public OkButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DialogActionListener dialogActions, String currentText) {
        dialogActions.okAction();
        return currentText;
    }
}
