/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatSetter;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author sascha
 */
public class BeltPanel extends JPanel implements UpdatePanelInterface {

    private static final long serialVersionUID = -2052546243588241937L;

    private static final int TEXT_FIELD_COLUMNS = 10;
    private static final double FEED_FACTOR = 1000.0 / 60.0;

    private final JTextField feedText;

    public BeltPanel(Window owner, String title) {
        super();
        setBorder(BorderFactory.createTitledBorder(title));

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

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

        FloatSetter feedSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {

                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                synchronized (guiInData) {
                    guiInData.setBeltFeed(value * FEED_FACTOR);
                }
            }
        };
        feedText = addParamItem(owner, this, labelConst, textConst, "Bandvorschub [m/min]", 0.2, 3.0, 1.0, feedSetter);

        final JTextField feedOvrField = addDisplayItem(this, labelConst, textConst, "Übersteuerung [%]");
        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                final double feed = MachineThread.getInstance().getGuiInData().getBeltFeed();
                final double override = MachineThread.getInstance().getGuiOutData().getFeedOverride();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        feedOvrField.setText(String.format("%.0f => %.2f m/s", override * 100, feed * override / FEED_FACTOR));
                    }
                });

            }
        });

        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        guiInData.setBeltFeed(1.0 * FEED_FACTOR);
    }

    private JTextField addParamItem(Window owner, JPanel panel, GridBagConstraints labelConst, GridBagConstraints textConst, String labelText, double min, double max, double deflt, FloatSetter setter) {
        JLabel label = new JLabel(labelText + ": ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, labelConst);
        labelConst.gridy++;

        final JTextField textField = new JTextField(TEXT_FIELD_COLUMNS);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setText(Double.toString(deflt));
        textField.addMouseListener(new FloatMouseListener(owner, labelText, textField, min, max, setter));
        panel.add(textField, textConst);
        textConst.gridy++;

        return textField;
    }

    private JTextField addDisplayItem(JPanel panel, GridBagConstraints labelConst, GridBagConstraints textConst, String labelText) {
        JLabel label = new JLabel(labelText + ": ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, labelConst);
        labelConst.gridy++;

        final JTextField textField = new JTextField(TEXT_FIELD_COLUMNS);
        textField.setEditable(false);
        panel.add(textField, textConst);
        textConst.gridy++;

        return textField;
    }

    @Override
    public void update() {
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            feedText.setText(Double.toString(guiInData.getBeltFeed() / FEED_FACTOR));
        }
    }
}
