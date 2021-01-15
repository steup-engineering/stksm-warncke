/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import de.steup.engineering.ksm.touchscreen.dialogs.buttons.OkButton;
import de.steup.engineering.ksm.touchscreen.dialogs.buttons.CancelButton;
import de.steup.engineering.ksm.touchscreen.dialogs.buttons.CharButton;
import de.steup.engineering.ksm.touchscreen.dialogs.buttons.ClearButton;
import de.steup.engineering.ksm.touchscreen.dialogs.buttons.DelButton;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Window;

/**
 *
 * @author sascha
 */
public class AlphaDialog extends JDialog implements DialogActionListener {

    private static final long serialVersionUID = 3677103392140572613L;

    private static final KeyButtonAction[][] BUTTON_ACTIONS = {
        {
            new CharButton("1", '1'),
            new CharButton("2", '2'),
            new CharButton("3", '3'),
            new CharButton("4", '4'),
            new CharButton("5", '5'),
            new CharButton("6", '6'),
            new CharButton("7", '7'),
            new CharButton("8", '8'),
            new CharButton("9", '9'),
            new CharButton("0", '0'),
            new CharButton("_", '_')
        },
        {
            new CharButton("Q", 'Q'),
            new CharButton("W", 'W'),
            new CharButton("E", 'E'),
            new CharButton("R", 'R'),
            new CharButton("T", 'T'),
            new CharButton("Z", 'Z'),
            new CharButton("U", 'U'),
            new CharButton("I", 'I'),
            new CharButton("O", 'O'),
            new CharButton("P", 'P'),
            new CharButton("+", '+')
        },
        {
            new CharButton("A", 'A'),
            new CharButton("S", 'S'),
            new CharButton("D", 'D'),
            new CharButton("F", 'F'),
            new CharButton("G", 'G'),
            new CharButton("H", 'H'),
            new CharButton("J", 'J'),
            new CharButton("K", 'K'),
            new CharButton("L", 'L'),
            new DelButton("löschen"),
            new ClearButton("leeren")
        },
        {
            new CharButton("Y", 'Y'),
            new CharButton("X", 'X'),
            new CharButton("C", 'C'),
            new CharButton("V", 'V'),
            new CharButton("B", 'B'),
            new CharButton("N", 'N'),
            new CharButton("M", 'M'),
            new CharButton("-", '-'),
            new CharButton("<LEER>", ' '),
            new CancelButton("Abbr"),
            new OkButton("OK")
        }
    };

    private final JTextField dest;
    private final JTextField inputField;
    private final JLabel statusLabel;
    private final StringSetter setter;
    private final int minLen;

    public static void showDialog(Window owner, String title, JTextField dest, int minLen, int maxLen, StringSetter setter) {
        AlphaDialog dlg = new AlphaDialog(owner, title, dest, minLen, maxLen, setter);
        dlg.setVisible(true);
    }

    public AlphaDialog(Window owner, String title, JTextField dest, int minLen, int maxLen, StringSetter setter) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.dest = dest;
        this.setter = setter;
        this.minLen = minLen;

        super.setResizable(false);

        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        BorderLayout layout = new BorderLayout();
        super.setLayout(layout);

        Container pane = super.getContentPane();

        inputField = new JTextField();
        inputField.setEditable(false);
        inputField.setBackground(Color.WHITE);
        if (dest != null) {
            inputField.setText(dest.getText());
        }

        JPanel buttonPanel = new JPanel();
        GridLayout buttonLayout = new GridLayout(4, 4);

        buttonPanel.setLayout(buttonLayout);
        for (KeyButtonAction[] BUTTON_ACTIONS1 : BUTTON_ACTIONS) {
            for (KeyButtonAction action : BUTTON_ACTIONS1) {
                JButton touchButton = new JButton(action.getCaption());
                KeyButtonListener touchListener = new KeyButtonListener(this, action, maxLen);
                touchButton.addActionListener(touchListener);
                buttonPanel.add(touchButton);
            }
        }

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(JLabel.LEFT);

        pane.add(inputField, BorderLayout.PAGE_START);
        pane.add(buttonPanel, BorderLayout.CENTER);
        pane.add(statusLabel, BorderLayout.PAGE_END);

        super.setSize(900, 400);
        super.setLocationRelativeTo(owner);
    }

    @Override
    public void cancelAction() {
        dispose();
    }

    @Override
    public void okAction() {
        String val = inputField.getText();

        if (minLen > 0 && val.length() < minLen) {
            statusLabel.setText(String.format("Mindestens %d Zeichen erforderlich.", minLen));
            return;
        }

        if (dest != null) {
            dest.setText(val);
        }
        if (setter != null) {
            setter.setValue(val);
        }
        dispose();
    }

    @Override
    public JTextField getTextField() {
        return inputField;
    }
}
