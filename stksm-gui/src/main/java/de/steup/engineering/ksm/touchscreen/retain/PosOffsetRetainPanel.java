/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.plc.retain.PosOffsetInterface;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatSetter;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class PosOffsetRetainPanel extends JPanel {

    private static final long serialVersionUID = 6950515801162545923L;

    private final static DecimalFormat DIST_FORMAT = new DecimalFormat("#0.0");

    private static final int TEXT_FIELD_COLUMNS = 10;

    protected final GridBagConstraints labelConst;
    protected final GridBagConstraints textConst;

    public PosOffsetRetainPanel(Window owner, String title, final PosOffsetInterface retainData) {
        GridBagLayout paramLayout = new GridBagLayout();
        setLayout(paramLayout);

        labelConst = new GridBagConstraints();
        labelConst.anchor = GridBagConstraints.LINE_START;
        labelConst.fill = GridBagConstraints.HORIZONTAL;
        labelConst.gridx = 0;
        labelConst.gridy = 0;

        textConst = new GridBagConstraints();
        textConst.anchor = GridBagConstraints.LINE_END;
        textConst.fill = GridBagConstraints.HORIZONTAL;
        textConst.gridx = 1;
        textConst.gridy = 0;

        FloatSetter posSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {
                retainData.setPos(value);
            }
        };
        addParamItem(owner, labelConst, textConst, "Mitte [mm]", 0.0, 6000.0, retainData.getPos(), DIST_FORMAT, posSetter);

        FloatSetter onOffsetSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {
                retainData.setOnOffset(value);
            }
        };
        addParamItem(owner, labelConst, textConst, "Offset ein [mm]", -999.0, 999.0, retainData.getOnOffset(), DIST_FORMAT, onOffsetSetter);

        FloatSetter offOffsetSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {
                retainData.setOffOffset(value);
            }
        };
        addParamItem(owner, labelConst, textConst, "Offset aus [mm]", -999.0, 999.0, retainData.getOffOffset(), DIST_FORMAT, offOffsetSetter);

        setBorder(BorderFactory.createTitledBorder(title));
    }

    protected JTextField addParamItem(Window owner, GridBagConstraints labelConst, GridBagConstraints textConst, String labelText, double min, double max, double deflt, DecimalFormat format, FloatSetter setter) {
        JLabel label = new JLabel(labelText + ": ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        add(label, labelConst);
        labelConst.gridy++;

        final JTextField textField = new JTextField(TEXT_FIELD_COLUMNS);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setText(format.format(deflt));
        textField.addMouseListener(new FloatMouseListener(owner, labelText, textField, min, max, format, setter));
        add(textField, textConst);
        textConst.gridy++;

        return textField;
    }

}
