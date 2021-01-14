/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.calib;

import de.steup.engineering.ksm.plc.entities.GuiCalibInterface;
import java.awt.Container;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class CalibBusyDialog extends JDialog {

    private static final long serialVersionUID = 6702791160710087514L;

    public CalibBusyDialog(Frame appFrame, String title, final GuiCalibInterface data) {
        super(appFrame, title, true);

        setResizable(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Container pane = this.getContentPane();

        JLabel busyLabel = new JLabel("Bitte warten...");
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pane.add(busyLabel);

        setSize(250, 100);

        setLocationRelativeTo(appFrame);
    }
}
