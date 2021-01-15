package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import de.steup.engineering.ksm.touchscreen.dialogs.files.AbstractLoadDialog;
import java.awt.Window;
import java.io.File;

/**
 *
 * @author christian
 */
public class LoadDialog extends AbstractLoadDialog {

    public static void showDialog(Window owner, PersUtil retainPersUtil, RetainDialog update) {
        LoadDialog dlg = new LoadDialog(owner, retainPersUtil, update);
        dlg.setVisible(true);
    }

    private final PersUtil retainPersUtil;
    private final RetainDialog update;

    public LoadDialog(Window owner, PersUtil retainPersUtil, RetainDialog update) {
        super(owner, Main.getParamPath());
        this.retainPersUtil = retainPersUtil;
        this.update = update;
    }

    @Override
    protected void loadFile(File file) {
        RetainMain retainData = this.retainPersUtil.loadRetain(this, file);
        update.setRetainData(retainData);
    }

}
