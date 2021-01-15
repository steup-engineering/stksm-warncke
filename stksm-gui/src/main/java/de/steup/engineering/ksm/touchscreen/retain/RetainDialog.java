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
import de.steup.engineering.ksm.plc.entities.GuiInBevel;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.entities.GuiInStationInterface;
import java.awt.Window;
import java.io.IOException;

/**
 *
 * @author sascha
 */
public class RetainDialog extends JDialog {

    private static final long serialVersionUID = 4896331450411664212L;

    public static void showDialog(Window owner) {
        Machine mach = MachineThread.getInstance().getMachine();
        if (!mach.isConnected()) {
            JOptionPane.showMessageDialog(owner, "Maschine ist offline.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RetainMain retainData;
        try {
            retainData = mach.readRetainData();
        } catch (PlcRestException ex) {
            JOptionPane.showMessageDialog(owner, String.format("REST-Fehler %d.", ex.getStatus()), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RetainDialog dlg = new RetainDialog(owner, retainData);
        dlg.setVisible(true);
    }

    private RetainMain retainData;
    private JPanel spc;

    public RetainDialog(Window owner, RetainMain retainData) {
        super(owner, "Retain Values", ModalityType.APPLICATION_MODAL);

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

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadDialog.showDialog(RetainDialog.this, new PersUtil(), RetainDialog.this);
            }
        });
        buttonPanel.add(loadButton);

        JButton saveButton = new JButton("Speichern ...");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveDialog.showDialog(RetainDialog.this, RetainDialog.this.retainData);
            }
        });
        buttonPanel.add(saveButton);

        JButton applyButton = new JButton("Übernehmen");
        applyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Machine mach = MachineThread.getInstance().getMachine();
                if (!mach.isConnected()) {
                    JOptionPane.showMessageDialog(RetainDialog.this, "Maschine ist offline.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    mach.writeRetainData(RetainDialog.this.retainData);
                } catch (PlcRestException ex) {
                    JOptionPane.showMessageDialog(RetainDialog.this, String.format("ADS-Fehler %d (%s).", ex.getStatus(), ex.getMessage()), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
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

        super.setLocationRelativeTo(owner);
    }

    static public String getCaption(int index, String name, GuiInStationInterface guiInterface) {
        String guiCaption = guiInterface.getCaption();
        if (guiCaption == null) {
            return String.format("%s %d", name, index + 1);
        }

        return String.format("%s %d [%s]", name, index + 1, guiCaption);
    }

    public final void setRetainData(RetainMain retainData) {
        this.retainData = retainData;
        this.spc.removeAll();

        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();

        RetainFace faces[] = this.retainData.getFaces();
        GuiInStationInterface guiFaces[] = guiInData.getFaces();
        for (int i = 0; i < Main.FACE_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(
                    this,
                    getCaption(i, "Flächenmotor", guiFaces[i]),
                    faces[i]));
        }

        RetainFace cleaners[] = retainData.getCleaners();
        GuiInStationInterface guiCleaners[] = guiInData.getCleaners();
        for (int i = 0; i < Main.CLEANER_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(
                    this,
                    getCaption(i, "Cleaner", guiCleaners[i]),
                    cleaners[i]));
        }

        RetainFace unidevs[] = retainData.getUnidevs();
        GuiInStationInterface guiUnidevs[] = guiInData.getUnidevs();
        for (int i = 0; i < Main.UNIDEV_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(
                    this,
                    getCaption(i, "Universalaggregat", guiUnidevs[i]),
                    unidevs[i]));
        }

        RetainBevel bevels[] = retainData.getBevels();
        GuiInBevel guiBevels[] = guiInData.getBevels();
        spc.add(new BevelRetainPanel(this, "Fase unten", bevels[0], guiBevels[0], Main.BEVEL_COUNT));
        spc.add(new BevelRetainPanel(this, "Fase oben", bevels[1], guiBevels[1], Main.BEVEL_COUNT));

        RetainFace rolls[] = retainData.getRolls();
        GuiInStationInterface guiRolls[] = guiInData.getRolls();
        for (int i = 0; i < Main.ROLLS_COUNT; i++) {
            spc.add(new PosOffsetRetainPanel(
                    this,
                    getCaption(i, "Rolle", guiRolls[i]),
                    rolls[i]));
        }

        super.validate();
    }
}
