/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.plc.rest.PlcRestException;
import de.steup.engineering.ksm.plc.rest.Machine;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.retain.RetainBevel;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import de.steup.engineering.ksm.plc.retain.RetainFace;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import de.steup.engineering.ksm.Main;
import java.io.IOException;

/**
 *
 * @author sascha
 */
public class RetainDialog extends JDialog {

    private static final long serialVersionUID = 4896331450411664212L;

    private static final String BEVEL_CAPTIONS[] = {
        "Fase unten",
        "Fase oben"
    };

    public static void showDialog() {
        Machine mach = MachineThread.getInstance().getMachine();
        if (!mach.isConnected()) {
            JOptionPane.showMessageDialog(Main.getMainFrame(), "Maschine ist offline.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RetainMain retainData = new RetainMain();

        try {
            mach.readRetainData(retainData);
        } catch (PlcRestException ex) {
            JOptionPane.showMessageDialog(Main.getMainFrame(), String.format("REST-Fehler %d.", ex.getStatus()), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
        }

        RetainDialog dlg = new RetainDialog(retainData);
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
    }

    private RetainMain retainData;
    private JPanel spc;

    public RetainDialog(RetainMain retainData) {
        super(Main.getMainFrame(), "Retain Values", true);

        super.setResizable(false);

        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container pane = super.getContentPane();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.spc = new JPanel();

        BoxLayout layout = new BoxLayout(spc, BoxLayout.Y_AXIS);
        spc.setLayout(layout);

        setRetainData(retainData);

        mainPanel.add(new JScrollPane(spc), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        GridLayout buttonLayout = new GridLayout(0, 5);
        buttonLayout.setHgap(10);
        buttonPanel.setLayout(buttonLayout);

        JButton loadButton = new JButton("Laden ...");
        
        // used to referenced to this in ebmedded classes
        final RetainDialog context = this;
        
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadDialog.showDialog(new PersUtil(), context);
            }
        });
        buttonPanel.add(loadButton);

        JButton saveButton = new JButton("Speichern ...");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveDialog.showDialog(context.retainData);
            }
        });
        buttonPanel.add(saveButton);

        JButton applyButton = new JButton("Übernehmen");
        applyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Machine mach = MachineThread.getInstance().getMachine();
                if (!mach.isConnected()) {
                    JOptionPane.showMessageDialog(Main.getMainFrame(), "Maschine ist offline.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    mach.writeRetainData(context.retainData);
                } catch (PlcRestException ex) {
                    JOptionPane.showMessageDialog(Main.getMainFrame(), String.format("ADS-Fehler %d (%s).", ex.getStatus(), ex.getMessage()), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
                }

                dispose();
            }
        });
        buttonPanel.add(applyButton);

        JButton cancelButton = new JButton("Abbruch");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

        JButton remoteButton = new JButton("Fernwartung");
        remoteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Runtime rt = Runtime.getRuntime();
                try {
                    rt.exec("/usr/local/bin/start_remote_session.sh");
                } catch (IOException ex) {
                    // NOP
                }
            }
        });
        buttonPanel.add(remoteButton);

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        pane.add(mainPanel);

        super.setSize(800, 800);

        super.setLocationRelativeTo(Main.getMainFrame());
    }

    public final void setRetainData(RetainMain retainData) {
        this.retainData = retainData;
        this.spc.removeAll();
        
        RetainFace faces[] = this.retainData.getFaces();
        for (int i = 0; i < Main.FACE_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(String.format("Flächenmotor %d", i + 1), faces[i]));
        }

        RetainFace cleaners[] = retainData.getCleaners();
        for (int i = 0; i < Main.CLEANER_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(String.format("Cleaner %d", i + 1), cleaners[i]));
        }

        RetainFace unidevs[] = retainData.getUnidevs();
        for (int i = 0; i < Main.UNIDEV_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(String.format("Universalaggregat %d", i + 1), unidevs[i]));
        }

        RetainBevel bevels[] = retainData.getBevels();
        for (int i = 0; i < Main.BEVEL_COUNT; i++) {
            spc.add(new BevelRetainPanel(BEVEL_CAPTIONS[i], bevels[i]));
        }

        RetainFace rolls[] = retainData.getRolls();
        for (int i = 0; i < Main.ROLLS_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(String.format("Rolle %d", i + 1), rolls[i]));
        }

        super.validate();
    }
}
