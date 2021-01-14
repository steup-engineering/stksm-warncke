/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiOutBevel;
import de.steup.engineering.ksm.plc.entities.GuiOutMain;
import de.steup.engineering.ksm.plc.entities.GuiOutUnidev;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
public class PosPanel extends JPanel implements UpdatePanelInterface {

    private static final long serialVersionUID = 3559246967649842336L;

    private static final int TEXT_FIELD_COLUMNS = 10;

    public PosPanel(String title) {
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

        final JTextField uniPos = addDisplayItem(this, labelConst, textConst, "Armierung/Wassernase [mm]");
        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                GuiOutUnidev[] data = MachineThread.getInstance().getGuiOutData().getUnidevs();
                final double pos = data[0].getSelectedPos();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        uniPos.setText(String.format("%.2f", pos));
                    }
                });

            }
        });

        final JTextField bevelUpper = addDisplayItem(this, labelConst, textConst, "Fase oben [mm]");
        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                GuiOutBevel[] data = MachineThread.getInstance().getGuiOutData().getBevels();
                final double pos = data[1].getAxisPos();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        bevelUpper.setText(String.format("%.2f", pos));
                    }
                });

            }
        });

        final JTextField bevelLower = addDisplayItem(this, labelConst, textConst, "Fase unten [mm]");
        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                GuiOutBevel[] data = MachineThread.getInstance().getGuiOutData().getBevels();
                final double pos = data[0].getAxisPos();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        bevelLower.setText(String.format("%.2f", pos));
                    }
                });

            }
        });

        final JTextField potPos = addDisplayItem(this, labelConst, textConst, "Höhenabtastung [mm]");
        MachineThread.getInstance().addUpdateListener(new Runnable() {

            @Override
            public void run() {
                GuiOutMain data = MachineThread.getInstance().getGuiOutData();
                final double pos = data.getProbePos();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        potPos.setText(String.format("%.2f", pos));
                    }
                });

            }
        });
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
        // NOP
    }
}
