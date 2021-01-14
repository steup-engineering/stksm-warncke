/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm;

import de.steup.engineering.ksm.plc.rest.PlcRestException;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInBevel;
import de.steup.engineering.ksm.plc.entities.GuiOutBevel;
import de.steup.engineering.ksm.plc.entities.GuiOutMain;
import de.steup.engineering.ksm.touchscreen.MainPanel;
import de.steup.engineering.ksm.touchscreen.calib.CalibBusyDialog;
import de.steup.engineering.ksm.touchscreen.calib.CalibMatprepDialog;
import de.steup.engineering.ksm.touchscreen.calib.CalibStartDialog;
import de.steup.engineering.ksm.touchscreen.calib.CalibTouchDialog;
import de.steup.engineering.ksm.touchscreen.dialogs.files.PathConfig;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author sascha
 */
public class Main {

    public static final int FACE_COUNT = 10;
    public static final int CLEANER_COUNT = 3;
    public static final int CLEANER_MODE_COUNT = 1;
    public static final int ROLLS_COUNT = 31;
    public static final int UNIDEV_COUNT = 1;
    public static final int BEVEL_COUNT = 2;
    public static final int BEVEL_MOTOR_COUNT = 3;
    
    public static final boolean RIGHT_TO_LEFT = true;

    private static JFrame mainFrame;
    private static PathConfig processPath;
    private static PathConfig paramPath;
    
    public static JFrame getMainFrame() {
        return mainFrame;
    }

    public static PathConfig getProcessPath() {
        return processPath;
    }

    public static PathConfig getParamPath() {
        return paramPath;
    }

    /**
     * @param args the command line arguments
     * @throws de.steup.engineering.ksm.plc.rest.PlcRestException
     */
    public static void main(String[] args) throws PlcRestException {

        if (args.length != 3) {
            System.err.println("Usage: ksm <REST service base URL> <Process Directory> <ParamDirectory>");
            System.exit(1);
        }

        final String restBaseUrl = args[0];
        processPath = new PathConfig("Prozess", args[1]);
        paramPath = new PathConfig("Parameter", args[2]);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                MachineThread machineThread = MachineThread.getInstance();
                machineThread.setRestBaseUrl(restBaseUrl);

                JFrame mainFrame = new JFrame("STKSM 10-6B/1+3");
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setUndecorated(true);

                MainPanel mp = new MainPanel();

                mainFrame.add(mp);

                mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                mainFrame.setVisible(true);

                GuiInBevel bevelIn[] = machineThread.getGuiInData().getBevels();
                final CalibBusyDialog bevelBusyDialog[] = {
                    new CalibBusyDialog(mainFrame, "Unterfase kalibrieren", bevelIn[0]),
                    new CalibBusyDialog(mainFrame, "Oberfräse kalibrieren", bevelIn[1])
                };
                final CalibStartDialog bevelStartDialog[] = {
                    new CalibStartDialog(mainFrame, "Unterfase kalibrieren", bevelIn[0]),
                    new CalibStartDialog(mainFrame, "Oberfräse kalibrieren", bevelIn[1])
                };
                final CalibMatprepDialog bevelMatprepDialog[] = {
                    new CalibMatprepDialog(mainFrame, "Unterfase kalibrieren", bevelIn[0]),
                    new CalibMatprepDialog(mainFrame, "Oberfräse kalibrieren", bevelIn[1])
                };
                final CalibTouchDialog bevelTouchDialog[] = {
                    new CalibTouchDialog(mainFrame, "Unterfase kalibrieren", bevelIn[0]),
                    new CalibTouchDialog(mainFrame, "Oberfräse kalibrieren", bevelIn[1])
                };

                machineThread.addUpdateListener(new Runnable() {

                    @Override
                    public void run() {
                        GuiOutMain data = MachineThread.getInstance().getGuiOutData();
                        JDialog newDialog;

                        GuiOutBevel bevels[] = data.getBevels();
                        for (int i = 0; i < BEVEL_COUNT; i++) {
                            GuiOutBevel bevel = bevels[i];
                            final CalibStartDialog startDialog = bevelStartDialog[i];
                            final CalibMatprepDialog matprepDialog = bevelMatprepDialog[i];
                            final CalibTouchDialog touchDialog = bevelTouchDialog[i];
                            final CalibBusyDialog busyDialog = bevelBusyDialog[i];

                            if (bevel.getCalibStep() != 0) {
                                switch (bevel.getCalibStep()) {
                                    case GuiOutBevel.CALIB_STEP_START:
                                        newDialog = startDialog;
                                        break;

                                    case GuiOutBevel.CALIB_STEP_MATPREP:
                                        newDialog = matprepDialog;
                                        break;

                                    case GuiOutBevel.CALIB_STEP_TOUCH:
                                        newDialog = touchDialog;
                                        break;

                                    default:
                                        newDialog = busyDialog;
                                }
                            } else {
                                newDialog = null;
                            }

                            if (newDialog != bevel.getCalibDialog()) {
                                switchDialog(newDialog, bevel.getCalibDialog());
                                bevel.setCalibDialog(newDialog);
                            }

                            final boolean calibError = bevel.isCalibError();
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    startDialog.setCalibError(calibError);
                                    matprepDialog.setCalibError(calibError);
                                    touchDialog.setCalibError(calibError);
                                }
                            });
                        }
                    }
                });

                machineThread.start();

            }
        });
    }

    private static void switchDialog(final JDialog nd, final JDialog od) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (od != null) {
                    od.setVisible(false);
                }
                if (nd != null) {
                    nd.setVisible(true);
                }
            }
        });
    }
}
