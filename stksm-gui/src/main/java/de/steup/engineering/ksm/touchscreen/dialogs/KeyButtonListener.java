/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class KeyButtonListener implements ActionListener {

    private KeyButtonAction action;
    private DialogActionListener dialogActions;
    private int maxLen;

    public KeyButtonListener(DialogActionListener dialogActions, KeyButtonAction action) {
        this(dialogActions, action, 0);
    }

    public KeyButtonListener(DialogActionListener dialogActions, KeyButtonAction action, int maxLen) {
        this.dialogActions = dialogActions;
        this.action = action;
        this.maxLen = maxLen;
    }

    public KeyButtonAction getAction() {
        return action;
    }

    public DialogActionListener getDialogActions() {
        return dialogActions;
    }

    public int getMaxLen() {
        return maxLen;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextField textField = dialogActions.getTextField();
        String newText = action.execute(dialogActions, textField.getText());
        if (maxLen > 0 && newText.length() > maxLen) {
            newText = newText.substring(0, maxLen);
        }
        textField.setText(newText);
    }

}
