/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.retain.RetainBevel;
import de.steup.engineering.ksm.plc.retain.RetainFace;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatSetter;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class BevelRetainPanel extends JPanel {

    private static final long serialVersionUID = -1702642935703092092L;

    private static final int TEXT_FIELD_COLUMNS = 10;

    public BevelRetainPanel(String title, final RetainBevel retainData) {

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        JPanel globalPanel = new JPanel();
        GridBagLayout paramLayout = new GridBagLayout();
        globalPanel.setLayout(paramLayout);

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

        FloatSetter widthOffsetSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {

                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                synchronized (guiInData) {
                    retainData.setWidthOffset(value);
                }
            }
        };
        addParamItem(globalPanel, labelConst, textConst, "Breiten-Offset [mm]", -10.0, 10.0, retainData.getWidthOffset(), widthOffsetSetter);

        add(globalPanel);

        add(new PosOffsetRetainPanel("Positionierung", retainData.getPosctl()));

        RetainFace motors[] = retainData.getMotors();
        for (int i = 0; i < Main.BEVEL_MOTOR_COUNT; i++) {
            add(new PosOffsetRetainPanel(String.format("Motor %d", i + 1), motors[i]));
        }

        setBorder(BorderFactory.createTitledBorder(title));
    }

    private JTextField addParamItem(JPanel panel, GridBagConstraints labelConst, GridBagConstraints textConst, String labelText, double min, double max, double deflt, FloatSetter setter) {
        JLabel label = new JLabel(labelText + ": ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, labelConst);
        labelConst.gridy++;

        final JTextField textField = new JTextField(TEXT_FIELD_COLUMNS);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setText(Double.toString(deflt));
        textField.addMouseListener(new FloatMouseListener(labelText, textField, min, max, setter));
        panel.add(textField, textConst);
        textConst.gridy++;

        return textField;
    }

}
