/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public class ClearButton extends KeyButtonAction {

    public ClearButton(String caption) {
        super(caption);
    }

    @Override
    public String execute(DialogActionListener dialogActions, String currentText) {
        return "";
    }
}
