/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormatSymbols;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class KeyButtonListener implements ActionListener {

    private final KeyButtonAction action;
    private final DialogActionListener dialogActions;
    private final int maxLen;
    private final DecimalFormatSymbols decimalFormatSymbols;

    public KeyButtonListener(DialogActionListener dialogActions, KeyButtonAction action, DecimalFormatSymbols decimalFormatSymbols) {
        this(dialogActions, action, 0, decimalFormatSymbols);
    }

    public KeyButtonListener(DialogActionListener dialogActions, KeyButtonAction action, int maxLen) {
        this(dialogActions, action, maxLen, null);
    }

    public KeyButtonListener(DialogActionListener dialogActions, KeyButtonAction action, int maxLen, DecimalFormatSymbols decimalFormatSymbols) {
        this.dialogActions = dialogActions;
        this.action = action;
        this.maxLen = maxLen;
        this.decimalFormatSymbols = decimalFormatSymbols;
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

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return decimalFormatSymbols;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextField textField = dialogActions.getTextField();
        String newText = action.execute(decimalFormatSymbols, dialogActions, textField.getText());
        if (maxLen > 0 && newText.length() > maxLen) {
            newText = newText.substring(0, maxLen);
        }
        textField.setText(newText);
    }

}
