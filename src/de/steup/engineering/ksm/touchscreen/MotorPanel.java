/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.entities.GuiInStationInterface;
import de.steup.engineering.ksm.plc.entities.GuiOutStationInterface;
import de.steup.engineering.ksm.touchscreen.dialogs.StringMouseListener;
import de.steup.engineering.ksm.touchscreen.dialogs.StringSetter;
import de.steup.engineering.ksm.touchscreen.util.MotorData;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author sascha
 */
public class MotorPanel extends MotorBasePanel implements UpdatePanelInterface {

    private static final long serialVersionUID = 5400429730103122718L;

    private final List<JTextField> captionText = new ArrayList<>();
    private final List<JButton> enaButton = new ArrayList<>();
    private final List<MotorData> motors;
    private Color buttonDefaultColor = null;

    public MotorPanel(String title, List<MotorData> motors, boolean hasLabel, boolean hasEnable) {
        super(title, motors.size());
        
        motors = new ArrayList<>(motors);
        if (Main.RIGHT_TO_LEFT) {
            Collections.reverse(motors);
        }
        this.motors = motors;

        if (hasLabel) {
            for (int i = 0; i < motors.size(); i++) {
                final MotorData motor = motors.get(i);
                final JTextField nt = new JTextField();
                nt.setEditable(false);
                nt.setHorizontalAlignment(JTextField.CENTER);
                nt.setText(motor.getEffCaption());
                nt.setBackground(Color.LIGHT_GRAY);
                if (motor.isCaptionEditable()) {
                    StringSetter setter = new StringSetter() {

                        @Override
                        public void setValue(String value) {
                            motor.setCaption(value);
                        }
                    };
                    nt.addMouseListener(new StringMouseListener("Motor Name", nt, 1, 16, setter));
                }
                captionText.add(nt);
                add(nt);
            }
        }

        if (hasEnable) {
            for (int i = 0; i < motors.size(); i++) {
                final JButton nb = new JButton("aktiv");
                if (buttonDefaultColor == null) {
                    buttonDefaultColor = nb.getBackground();
                }
                enaButton.add(nb);
                add(nb);

                final GuiInStationInterface stationIn = motors.get(i).getInData();
                nb.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        synchronized (MachineThread.getInstance().getGuiInData()) {
                            stationIn.setEna(!stationIn.isEna());
                            if (stationIn.isEna()) {
                                nb.setBackground(Color.green);
                            } else {
                                nb.setBackground(buttonDefaultColor);
                            }
                        }
                    }
                });
            }
        }

        for (int i = 0; i < motors.size(); i++) {
            final JButton nb = new JButton("manu");
            final Color defaultColor = nb.getBackground();
            add(nb);

            final GuiInStationInterface stationIn = motors.get(i).getInData();
            final GuiOutStationInterface stationOut = motors.get(i).getOutData();
            nb.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    // NOP
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    synchronized (MachineThread.getInstance().getGuiInData()) {
                        stationIn.setManu(true);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    synchronized (MachineThread.getInstance().getGuiInData()) {
                        stationIn.setManu(false);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // NOP
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // NOP
                }
            });

            MachineThread.getInstance().addUpdateListener(new Runnable() {

                @Override
                public void run() {
                    final boolean state = stationOut.isActive();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (state) {
                                nb.setBackground(Color.red);
                            } else {
                                nb.setBackground(defaultColor);
                            }
                        }
                    });

                }
            });
        }
    }

    @Override
    public void update() {
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            for (int i = 0; i < motors.size(); i++) {
                MotorData motor = motors.get(i);

                if (!captionText.isEmpty()) {
                    captionText.get(i).setText(motor.getEffCaption());
                }

                if (!enaButton.isEmpty()) {
                    JButton nb = enaButton.get(i);
                    if (motor.getInData().isEna()) {
                        nb.setBackground(Color.green);
                    } else {
                        nb.setBackground(buttonDefaultColor);
                    }
                }
            }
        }
    }
}
