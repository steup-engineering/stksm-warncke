/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.calib;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiCalibInterface;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author sascha
 */
public class CalibAckDialog extends JDialog {

    private static final long serialVersionUID = -2471474229666009191L;

    private final JLabel hintLabel;
    private final JLabel errorLabel;
    private final JButton contButton;

    public CalibAckDialog(Frame appFrame, String title, final GuiCalibInterface data) {
        super(appFrame, title, true);

        setResizable(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Container pane = this.getContentPane();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel dataPanel = new JPanel();
        BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.Y_AXIS);
        dataPanel.setLayout(layout);

        hintLabel = new JLabel("Sollen die Werte übernommen werden?");
        dataPanel.add(hintLabel);

        errorLabel = new JLabel("Kalibrierungsfehler!");
        errorLabel.setForeground(Color.red);
        errorLabel.setVisible(false);
        dataPanel.add(errorLabel);

        mainPanel.add(dataPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonLayout.setHgap(10);
        buttonPanel.setLayout(buttonLayout);

        contButton = new JButton("Weiter");
        contButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();

                synchronized (guiInData) {
                    data.setCalibStart(true);
                }
            }
        });

        buttonPanel.add(contButton);

        JButton cancelButton = new JButton("Abbruch");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setCalibCancel(true);
            }
        });
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        pane.add(mainPanel);

        setSize(250, 100);

        setLocationRelativeTo(appFrame);
    }

    public void setCalibError(boolean error) {
        errorLabel.setVisible(error);
        hintLabel.setVisible(!error);
        contButton.setVisible(!error);
    }
}
