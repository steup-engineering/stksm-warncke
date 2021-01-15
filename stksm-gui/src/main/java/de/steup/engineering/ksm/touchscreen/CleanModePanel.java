/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.ksm.touchscreen.util.CaptionChangeListener;
import de.steup.engineering.ksm.plc.entities.GuiCleanerMode;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.touchscreen.util.MotorData;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 *
 * @author sascha
 */
public class CleanModePanel extends JPanel implements UpdatePanelInterface, CaptionChangeListener {

    private static final long serialVersionUID = -2052546243588241937L;

    private final MotorData motor;
    private final TitledBorder border;

    private interface StateInterface {

        public boolean getState();

        public void setState(boolean state);
    }

    public CleanModePanel(final MotorData motor, final GuiCleanerMode currentMode, final GuiCleanerMode requestMode) {
        super();

        this.motor = motor;
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
                return currentMode.isClean();
            }

            @Override
            public void setState(boolean state) {
                requestMode.setClean(state);
            }
        }));

        add(createButton("Kalibrieren (fräsen)", new StateInterface() {
            @Override
            public boolean getState() {
                return currentMode.isCalib();
            }

            @Override
            public void setState(boolean state) {
                requestMode.setCalib(state);
            }
        }));
    }

    private String buildCaption() {
        return "Modus " + motor.getEffCaption();
    }

    private JButton createButton(final String caption, final StateInterface iface) {
        final JButton nb = new JButton(caption);
        final Color defaultColor = nb.getBackground();

        nb.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // NOP
            }

            @Override
            public void mousePressed(MouseEvent e) {
                synchronized (MachineThread.getInstance().getGuiInData()) {
                    iface.setState(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                synchronized (MachineThread.getInstance().getGuiInData()) {
                    iface.setState(false);
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

    @Override
    public void update() {
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            updateCaption();
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
