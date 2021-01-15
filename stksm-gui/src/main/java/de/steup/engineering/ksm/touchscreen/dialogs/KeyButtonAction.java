/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

/**
 *
 * @author sascha
 */
public abstract class KeyButtonAction {

    private final String caption;

    public KeyButtonAction(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public abstract String execute(DialogActionListener dialogActions, String currentText);
}
