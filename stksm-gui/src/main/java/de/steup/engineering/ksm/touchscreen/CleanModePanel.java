/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.plc.entities.GuiInCleanerMode;
import de.steup.engineering.ksm.touchscreen.util.CaptionChangeListener;
import de.steup.engineering.ksm.plc.entities.GuiOutCleanerMode;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.FloatSetter;
import de.steup.engineering.ksm.touchscreen.util.MachButtonListener;
import de.steup.engineering.ksm.touchscreen.util.MotorData;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 *
 * @author sascha
 */
public class CleanModePanel extends JPanel implements UpdatePanelInterface, CaptionChangeListener {

    private static final long serialVersionUID = -2052546243588241937L;

    private static final DecimalFormat HEIGHT_FORMAT = new DecimalFormat("#0.0");
    private static final int TEXT_FIELD_COLUMNS = 10;

    private final MotorData motor;
    private final GuiInCleanerMode guiInMode;
    private final TitledBorder border;
    private final JTextField calibHeight;

    private interface StateInterface {

        public boolean getState();

        public void setState(boolean state);
    }

    public CleanModePanel(Window owner, final MotorData motor, final GuiOutCleanerMode guiOutMode, final GuiInCleanerMode guiInMode) {
        super();

        this.motor = motor;
        this.guiInMode = guiInMode;

        this.border = new TitledBorder(buildCaption());

        motor.addCaptionChangeListener(this);

        setBorder(border);

        GridLayout layout = new GridLayout(0, 1);
        layout.setHgap(4);
        layout.setVgap(10);
        setLayout(layout);

        add(createButton("Cleanen (schleifen)", new StateInterface() {
            @Override
            public boolean getState() {
                return guiOutMode.isClean();
            }

            @Override
            public void setState(boolean state) {
                guiInMode.setClean(state);
            }
        }));

        add(createButton("Kalibrieren (fräsen)", new StateInterface() {
            @Override
            public boolean getState() {
                return guiOutMode.isCalib();
            }

            @Override
            public void setState(boolean state) {
                guiInMode.setCalib(state);
            }
        }));

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

        FloatSetter calibHeightSetter = new FloatSetter() {

            @Override
            public void setValue(double value) {

                GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
                synchronized (guiInData) {
                    guiInMode.setCalibHeight(value);
                }
            }
        };
        calibHeight = addParamItem(owner, paramPanel, labelConst, textConst, "Höhe Kalibrierer [mm]", 0.0, 100.0, 0.0, HEIGHT_FORMAT, calibHeightSetter);

        paramPanel.setBorder(BorderFactory.createEtchedBorder());
        add(paramPanel);

    }

    private String buildCaption() {
        return "Modus " + motor.getEffCaption();
    }

    private JButton createButton(final String caption, final StateInterface iface) {
        final JButton nb = new JButton(caption);
        final Color defaultColor = nb.getBackground();

        nb.addMouseListener(new MachButtonListener() {
            @Override
            protected void stateChanged(GuiInMain guiInData, boolean pressed) {
                iface.setState(pressed);
            }
        });

        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                final boolean state = iface.getState();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (state) {
                            nb.setBackground(Color.green);
                        } else {
                            nb.setBackground(defaultColor);
                        }
                    }
                });

            }
        });

        return nb;
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
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            updateCaption();

            calibHeight.setText(HEIGHT_FORMAT.format(guiInMode.getCalibHeight()));
        }
    }

    @Override
    public void updateCaption() {

        final String caption = buildCaption();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                border.setTitle(caption);
                repaint();
            }
        });

    }
}
