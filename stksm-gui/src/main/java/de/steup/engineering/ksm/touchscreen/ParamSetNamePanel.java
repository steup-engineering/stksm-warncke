/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.rest.Machine;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.rest.PlcRestException;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import de.steup.engineering.ksm.touchscreen.dialogs.StringSetter;
import de.steup.engineering.ksm.touchscreen.dialogs.files.FileMouseListener;
import de.steup.engineering.ksm.touchscreen.retain.PersUtil;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author christian
 */
public class ParamSetNamePanel extends JPanel implements UpdatePanelInterface {

    private final Log logger = LogFactory.getLog(ParamSetNamePanel.class.toString());

    private final Window owner;
    private final JTextField paramNameField;

    private class ParamGridBagConstraints extends GridBagConstraints {

        public ParamGridBagConstraints(double weightx, int fill) {
            super(
                    RELATIVE,
                    RELATIVE,
                    1,
                    1,
                    weightx,
                    0.0,
                    ParamGridBagConstraints.CENTER,
                    fill,
                    new Insets(2, 2, 2, 2),
                    0,
                    0);
        }

    }

    public ParamSetNamePanel(Window owner, String title) {
        super();
        this.owner = owner;

        setBorder(BorderFactory.createTitledBorder(title));

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        StringSetter paramNameSetter = new StringSetter() {
            @Override
            public void setValue(String value) {
                if (loadRetainData(value)) {
                    paramNameField.setText(value);

                    GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                    synchronized (guiInData) {
                        guiInData.setParamSetName(value);
                    }
                }
            }
        };

        paramNameField = new JTextField();
        paramNameField.setEditable(false);
        paramNameField.setBackground(Color.WHITE);
        this.add(paramNameField, new ParamGridBagConstraints(1.0, GridBagConstraints.HORIZONTAL));
        FileMouseListener lookupListener = new FileMouseListener(owner, Main.getParamPath(), paramNameSetter);
        paramNameField.addMouseListener(lookupListener);

        final JButton selectButton = new JButton("Auswahl ...");
        this.add(selectButton, new ParamGridBagConstraints(0.0, GridBagConstraints.NONE));
        selectButton.addActionListener(lookupListener);

        final JButton clearButton = new JButton("Leeren");
        this.add(clearButton, new ParamGridBagConstraints(0.0, GridBagConstraints.NONE));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paramNameSetter.setValue(null);
                paramNameField.setText(null);
            }
        });
    }

    @Override
    public void update() {
        String paramName;
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            paramName = guiInData.getParamSetName();
        }

        if (loadRetainData(paramName)) {
            paramNameField.setText(paramName);
        }
    }

    private boolean loadRetainData(String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }

        File file = new File(Main.getParamPath().getRoot(), name + ".xml");
        if (!file.canRead()) {
            String msg = String.format("Parameterdatei %s kann nicht gelesen werden.", file.getName());
            logger.error(msg);
            JOptionPane.showMessageDialog(owner,
                    msg, "Fehler beim Laden", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        PersUtil persUtil = new PersUtil();
        RetainMain retainData = persUtil.loadRetain(owner, file);
        if (retainData == null) {
            return false;
        }

        Machine mach = MachineThread.getInstance().getMachine();
        if (!mach.isConnected()) {
            return true;
        }

        try {
            mach.writeRetainData(retainData);
        } catch (PlcRestException ex) {
            JOptionPane.showMessageDialog(owner, String.format("ADS-Fehler %d (%s).", ex.getStatus(), ex.getMessage()), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }
}
