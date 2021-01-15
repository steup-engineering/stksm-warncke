/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import de.steup.engineering.ksm.Main;
import java.awt.Color;

/**
 *
 * @author sascha
 */
public abstract class NumDialog extends JDialog implements DialogActionListener {

    private static final long serialVersionUID = -6640413890000237925L;

    private static final KeyButtonAction[][] BUTTON_ACTIONS = {
        {new CharButton("7", '7'), new CharButton("8", '8'), new CharButton("9", '9'), new DelButton("löschen")},
        {new CharButton("4", '4'), new CharButton("5", '5'), new CharButton("6", '6'), new ClearButton("leeren")},
        {new CharButton("1", '1'), new CharButton("2", '2'), new CharButton("3", '3'), new CancelButton("Abbruch")},
        {new CharButton("0", '0'), new CharButton(".", '.'), new SignButton("+/-"), new OkButton("OK")}
    };

    protected JTextField dest;
    protected JTextField inputField;
    protected JLabel statusLabel;

    public NumDialog(String title, JTextField dest) {
        super(Main.getMainFrame(), title, true);
        this.dest = dest;

        setResizable(false);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        Container pane = this.getContentPane();

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
                KeyButtonListener touchListener = new KeyButtonListener(this, action);
                touchButton.addActionListener(touchListener);
                buttonPanel.add(touchButton);
            }
        }

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(JLabel.LEFT);

        pane.add(inputField, BorderLayout.PAGE_START);
        pane.add(buttonPanel, BorderLayout.CENTER);
        pane.add(statusLabel, BorderLayout.PAGE_END);

        setSize(400, 400);
        setLocationRelativeTo(Main.getMainFrame());
    }

    @Override
    public void cancelAction() {
        dispose();
    }

    @Override
    public abstract void okAction();

    @Override
    public JTextField getTextField() {
        return inputField;
    }
}
