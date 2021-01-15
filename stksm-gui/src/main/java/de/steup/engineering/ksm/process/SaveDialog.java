/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.process;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.touchscreen.UpdatePanelInterface;
import de.steup.engineering.ksm.touchscreen.dialogs.files.AbstractSaveDialog;
import java.awt.Window;
import java.io.File;

/**
 *
 * @author sascha
 */
public class SaveDialog extends AbstractSaveDialog {

    private static final long serialVersionUID = 6140459098008820795L;

    private final PersUtil persUtil;
    private final UpdatePanelInterface loadUpdater;

    public static void showDialog(Window owner, PersUtil persUtil, UpdatePanelInterface loadUpdater) {

        SaveDialog dlg = new SaveDialog(owner, persUtil, loadUpdater);
        dlg.setVisible(true);
    }

    public SaveDialog(Window owner, PersUtil persUtil, UpdatePanelInterface loadUpdater) {
        super(owner, Main.getProcessPath());
        this.persUtil = persUtil;
        this.loadUpdater = loadUpdater;
    }

    @Override
    protected void saveFile(File file) {

        GuiInMain data = MachineThread.getInstance().getGuiInData();
        String fn = file.getName();

        synchronized (data) {
            persUtil.saveProcess(this, data, file);
            data.setProcessName(fn.substring(0, fn.length() - 4));
        }

        if (loadUpdater != null) {
            loadUpdater.update();
        }

        dispose();
    }
}
