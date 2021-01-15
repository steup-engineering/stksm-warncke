/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInDevInterface;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.touchscreen.util.MotorData;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatSetter;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class DevPanel extends JPanel implements UpdatePanelInterface {

    private static final long serialVersionUID = -1887846471221849425L;

    private final static DecimalFormat DIST_FORMAT = new DecimalFormat("#0.0");

    private static final int TEXT_FIELD_COLUMNS = 10;
    private final List<UpdatePanelInterface> updatePanels = new ArrayList<>();
    private final GuiInDevInterface devData;
    private JTextField distText;
    private JTextField margStartText;
    private JTextField margEndText;

    public DevPanel(Window owner, String title, List<MotorData> motors, ActionListener calibAction, String distLabel, boolean hasMotorLabel, final GuiInDevInterface devData) {
        super();
        setBorder(BorderFactory.createTitledBorder(title));

        this.devData = devData;

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        if (motors != null) {
            MotorPanel motorPanel = new MotorPanel(owner, null, motors, hasMotorLabel, true);
            updatePanels.add(motorPanel);
            add(motorPanel);
        }

        distText = null;
        margStartText = null;
        margEndText = null;

        JPanel paramPanel = new JPanel();

        GridBagLayout paramLayout = new GridBagLayout();
        paramPanel.setLayout(paramLayout);

        GridBagConstraints labelConst = new GridBagConstraints();
        labelConst.anchor = GridBagConstraints.LINE_START;
        labelConst.fill = GridBagConstraints.HORIZONTAL;
        labelConst.gridx = 0;
        labelConst.gridy = 0;

        GridBagConstraints textConst = new GridBagConstraints();
        textConst.anchor = GridBagConstraints.LINE_END;
        textConst.fill = GridBagConstraints.HORIZONTAL;
        textConst.gridx = 1;
        textConst.gridy = 0;

        if (distLabel != null) {
            FloatSetter distSetter = new FloatSetter() {

                @Override
                public void setValue(double value) {

                    GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                    synchronized (guiInData) {
                        devData.setDist(value);
                    }
                }
            };
            distText = addParamItem(owner, paramPanel, labelConst, textConst, distLabel, 0.0, 30.0, 0.0, DIST_FORMAT, distSetter);
        }

        FloatSetter margStartSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {

                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                synchronized (guiInData) {
                    devData.setMarginStart(value);
                }
            }
        };
        margStartText = addParamItem(owner, paramPanel, labelConst, textConst, "Rand links [mm]", 0.0, 4000.0, 0.0, DIST_FORMAT, margStartSetter);

        FloatSetter margEndSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {

                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                synchronized (guiInData) {
                    devData.setMarginEnd(value);
                }
            }
        };
        margEndText = addParamItem(owner, paramPanel, labelConst, textConst, "Rand rechts [mm]", 0.0, 4000.0, 0.0, DIST_FORMAT, margEndSetter);

        paramPanel.setBorder(BorderFactory.createEtchedBorder());

        add(paramPanel);

        if (calibAction != null) {
            JPanel calibPanel = new JPanel();
            JButton calibButton = new JButton("Kalibrieren ...");
            calibButton.addActionListener(calibAction);
            calibPanel.add(calibButton);
            add(calibPanel);
        }
    }

    private JTextField addParamItem(Window owner, JPanel panel, GridBagConstraints labelConst, GridBagConstraints textConst, String labelText, double min, double max, double deflt, DecimalFormat format, FloatSetter setter) {
        JLabel label = new JLabel(labelText + ": ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, labelConst);
        labelConst.gridy++;

        final JTextField textField = new JTextField(TEXT_FIELD_COLUMNS);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setText(format.format(deflt));
        textField.addMouseListener(new FloatMouseListener(owner, labelText, textField, min, max, format, setter));
        panel.add(textField, textConst);
        textConst.gridy++;

        return textField;
    }

    @Override
    public void update() {
        for (UpdatePanelInterface updatePanel : updatePanels) {
            updatePanel.update();
        }

        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            if (distText != null) {
                distText.setText(DIST_FORMAT.format(devData.getDist()));
            }
            if (margStartText != null) {
                margStartText.setText(DIST_FORMAT.format(devData.getMarginStart()));
            }
            if (margEndText != null) {
                margEndText.setText(DIST_FORMAT.format(devData.getMarginEnd()));
            }
        }
    }
}
