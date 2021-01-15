/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.process;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.touchscreen.UpdatePanelInterface;
import de.steup.engineering.ksm.touchscreen.dialogs.files.AbstractLoadDialog;
import java.io.File;
import javax.swing.SwingUtilities;

/**
 *
 * @author sascha
 */
public class LoadDialog extends AbstractLoadDialog {

    private static final long serialVersionUID = -2122793712889935530L;

    private final PersUtil persUtil;
    private final UpdatePanelInterface loadUpdater;

    public static void showDialog(PersUtil persUtil, UpdatePanelInterface loadUpdater) {

        LoadDialog dlg = new LoadDialog(persUtil, loadUpdater);
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
    }

    public LoadDialog(PersUtil persUtil, UpdatePanelInterface loadUpdater) {
        super(Main.getProcessPath());

        this.persUtil = persUtil;
        this.loadUpdater = loadUpdater;
    }

    @Override
    protected void loadFile(File file) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                GuiInMain data = MachineThread.getInstance().getGuiInData();
                String fn = file.getName();

                synchronized (data) {
                    persUtil.loadProcess(data, file);

                    data.setProcessName(fn.substring(0, fn.length() - 4));
                }

                if (loadUpdater != null) {
                    loadUpdater.update();
                }

                dispose();
            }
        });
    }
}
