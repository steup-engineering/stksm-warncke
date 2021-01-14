/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.retain.RetainUnidev;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatSetter;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class UnidevRetainPanel extends JPanel {

    private static final long serialVersionUID = -3057335985123274090L;

    private static final int TEXT_FIELD_COLUMNS = 10;

    public UnidevRetainPanel(String title, final RetainUnidev retainData) {
        GridBagLayout paramLayout = new GridBagLayout();
        setLayout(paramLayout);

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

        FloatSetter posSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {

                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                synchronized (guiInData) {
                    retainData.setPos(value);
                }
            }
        };
        addParamItem(labelConst, textConst, "Pos [mm]", 0.0, 6000.0, retainData.getPos(), posSetter);

        setBorder(BorderFactory.createTitledBorder(title));
    }

    private JTextField addParamItem(GridBagConstraints labelConst, GridBagConstraints textConst, String labelText, double min, double max, double deflt, FloatSetter setter) {
        JLabel label = new JLabel(labelText + ": ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        add(label, labelConst);
        labelConst.gridy++;

        final JTextField textField = new JTextField(TEXT_FIELD_COLUMNS);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setText(Double.toString(deflt));
        textField.addMouseListener(new FloatMouseListener(labelText, textField, min, max, setter));
        add(textField, textConst);
        textConst.gridy++;

        return textField;
    }

}
