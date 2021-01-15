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

    private static final String[] ERRORS = {
        "Notaus",
        "Türe offen",
        "Luftdruck fehlt",
        "Wasserdruck fehlt",
        "Motorschutz ausgelöst",
        "Fehler Bandantrieb",
        "Fehler Universalagregat",
        "Fehler Fasenfräser unten",
        "Fehler Fasenfräser oben",
        "Fehler Bussystem",
        "Fehler Modusumschaltung Cleaner 1"
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
                        JCheckBox errorBox = errorBoxes[i];
                        errorBox.setSelected((err & (1 << i)) != 0);
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
            JCheckBox cb = new JCheckBox(ERRORS[i]);
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
