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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class CalibStartDialog extends JDialog {

    private static final long serialVersionUID = -22186225208259673L;

    private final JLabel errorLabel;

    public CalibStartDialog(Frame appFrame, String title, final GuiCalibInterface data) {
        super(appFrame, title, true);

        setResizable(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Container pane = this.getContentPane();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BorderLayout());

        JLabel msgLabel = new JLabel("Bitte Referenzmaterial auflegen.");
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dataPanel.add(msgLabel, BorderLayout.CENTER);

        errorLabel = new JLabel("Kalibrierungsfehler!");
        errorLabel.setForeground(Color.red);
        errorLabel.setVisible(false);
        dataPanel.add(errorLabel, BorderLayout.PAGE_END);

        mainPanel.add(dataPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonLayout.setHgap(10);
        buttonPanel.setLayout(buttonLayout);

        JButton contButton = new JButton("Weiter");
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

        setSize(450, 120);

        setLocationRelativeTo(appFrame);
    }

    public void setCalibError(boolean error) {
        errorLabel.setVisible(error);
    }

}
