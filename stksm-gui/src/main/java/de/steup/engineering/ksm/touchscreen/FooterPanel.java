/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiOutMain;
import de.steup.engineering.ksm.touchscreen.errors.ErrorDialog;
import de.steup.engineering.ksm.touchscreen.retain.RetainDialog;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.process.LoadDialog;
import de.steup.engineering.ksm.process.PersUtil;
import de.steup.engineering.ksm.process.SaveDialog;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author sascha
 */
public class FooterPanel extends JPanel {

    private static final long serialVersionUID = -8354129910941931790L;

    private PersUtil persUtil = new PersUtil();

    public FooterPanel(final UpdatePanelInterface loadUpdater) {
        super();
        setBorder(BorderFactory.createEtchedBorder());

        GridLayout layout = new GridLayout(0, 6);
        layout.setHgap(10);
        setLayout(layout);

        final JButton loadButton = new JButton("Laden ...");
        final JButton saveButton = new JButton("Speichern ...");
        final JButton errorButton = new JButton("Fehler ...");
        final Color errorDefaultColor = errorButton.getBackground();
        final JButton settingsButton = new JButton("Einstellungen ...");
        final JButton jobResetButton = new JButton("Auftrag Null");
        final JButton shutdownButton = new JButton("Ausschalten");

        add(loadButton);
        add(saveButton);
        add(errorButton);
        add(settingsButton);
        add(jobResetButton);
        add(shutdownButton);

        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LoadDialog.showDialog(persUtil, loadUpdater);
            }
        });

        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SaveDialog.showDialog(persUtil, loadUpdater);
            }
        });

        errorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ErrorDialog.showDialog();
            }
        });

        jobResetButton.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // NOP
            }

            @Override
            public void mousePressed(MouseEvent e) {
                final GuiInMain gi = MachineThread.getInstance().getGuiInData();
                synchronized (gi) {
                    gi.setWhmJobReset(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                final GuiInMain gi = MachineThread.getInstance().getGuiInData();
                synchronized (gi) {
                    gi.setWhmJobReset(false);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // NOP
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // NOP
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // NOP
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // NOP
            }
        });

        shutdownButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (JOptionPane.showConfirmDialog(
                        Main.getMainFrame(),
                        "Maschine wirklich herunterfahren?",
                        "Sicherheitsabfrage",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }

                Runtime rt = Runtime.getRuntime();
                try {
                    rt.exec("sudo shutdown -h now");
                } catch (IOException ex) {
                    // NOP
                }
            }
        });

        settingsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                RetainDialog.showDialog();
            }
        });

        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                GuiOutMain outData = MachineThread.getInstance().getGuiOutData();
                final boolean errors;
                final boolean running;
                synchronized (outData) {
                    errors = (outData.getErrors() != 0);
                    running = outData.isRunning();
                }

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (errors) {
                            errorButton.setBackground(Color.red);
                        } else {
                            errorButton.setBackground(errorDefaultColor);
                        }

                        loadButton.setEnabled(!running);
                        shutdownButton.setEnabled(!running);
                    }
                });
            }
        });

    }
}
