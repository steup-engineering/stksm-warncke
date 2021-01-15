/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.errors;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.entities.GuiOutMain;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Window;

/**
 *
 * @author sascha
 */
public class ErrorDialog extends JDialog {

    private static final long serialVersionUID = 3313303411371387035L;

    private static class ErrorItem {

        private final int mask;
        private final String caption;

        public ErrorItem(int mask, String caption) {
            this.mask = mask;
            this.caption = caption;
        }

        public int getMask() {
            return mask;
        }

        public String getCaption() {
            return caption;
        }

        public boolean isActive(int error) {
            return (error & mask) != 0;
        }
    };

    private static final ErrorItem[] ERRORS = {
        new ErrorItem(GuiOutMain.ERR_EMERG_STOP, "Notaus"),
        new ErrorItem(GuiOutMain.ERR_DOOR_INTERLOCK, "Türe offen"),
        new ErrorItem(GuiOutMain.ERR_AIR_PRESS, "Luftdruck fehlt"),
        new ErrorItem(GuiOutMain.ERR_WATER_PRESS, "Wasserdruck fehlt"),
        new ErrorItem(GuiOutMain.ERR_MOTOR_PROT, "Motorschutz ausgelöst"),
        new ErrorItem(GuiOutMain.ERR_BELT, "Fehler Bandantrieb"),
        new ErrorItem(GuiOutMain.ERR_UNIDEV, "Fehler Universalagregat"),
        new ErrorItem(GuiOutMain.ERR_BEVEL_LOWER, "Fehler Fasenfräser unten"),
        new ErrorItem(GuiOutMain.ERR_BEVEL_UPPER, "Fehler Fasenfräser oben"),
        new ErrorItem(GuiOutMain.ERR_BUS_SYSTEM, "Fehler Bussystem"),
        new ErrorItem(GuiOutMain.ERR_MODSEL_CLEAN1, "Fehler Modusumschaltung Cleaner 1")
    };

    private final JCheckBox errorBoxes[];
    private final Runnable updateListener = new Runnable() {

        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    GuiOutMain guiOutData = MachineThread.getInstance().getGuiOutData();
                    int err;
                    synchronized (guiOutData) {
                        err = guiOutData.getErrors();
                    }
                    for (int i = 0; i < errorBoxes.length; i++) {
                        ErrorItem ei = ERRORS[i];
                        JCheckBox errorBox = errorBoxes[i];
                        errorBox.setSelected(ei.isActive(err));
                    }
                }
            });
        }
    };

    public static void showDialog(Window owner) {
        ErrorDialog dlg = new ErrorDialog(owner);
        MachineThread.getInstance().addUpdateListener(dlg.updateListener);
        dlg.setVisible(true);
        MachineThread.getInstance().removeUpdateListener(dlg.updateListener);
    }

    public ErrorDialog(Window owner) {
        super(owner, "Fehlerstatus", ModalityType.APPLICATION_MODAL);

        super.setResizable(false);

        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container pane = super.getContentPane();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel spc = new JPanel();

        BoxLayout layout = new BoxLayout(spc, BoxLayout.Y_AXIS);
        spc.setLayout(layout);

        errorBoxes = new JCheckBox[ERRORS.length];
        for (int i = 0; i < ERRORS.length; i++) {
            ErrorItem ei = ERRORS[i];
            JCheckBox cb = new JCheckBox(ei.getCaption());
            cb.setEnabled(false);
            spc.add(cb);
            errorBoxes[i] = cb;
        }

        mainPanel.add(new JScrollPane(spc), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonLayout.setHgap(10);
        buttonPanel.setLayout(buttonLayout);

        JButton resetButton = new JButton("Zurücksetzen");
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();

                synchronized (guiInData) {
                    guiInData.setResetError(true);
                }
            }
        });

        buttonPanel.add(resetButton);

        JButton closelButton = new JButton("Schliessen");
        closelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(closelButton);

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        pane.add(mainPanel);

        super.setSize(400, 500);

        super.setLocationRelativeTo(owner);
    }
}
