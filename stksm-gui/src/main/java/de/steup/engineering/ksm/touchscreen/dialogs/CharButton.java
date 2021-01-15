/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public class CharButton extends KeyButtonAction {

    private final char keyChar;

    public CharButton(String caption, char keyChar) {
        super(caption);
        this.keyChar = keyChar;
    }

    @Override
    public String execute(DialogActionListener dialogActions, String currentText) {
        return currentText + keyChar;
    }

}
