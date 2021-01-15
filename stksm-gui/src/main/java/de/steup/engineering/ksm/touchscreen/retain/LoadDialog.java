package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import de.steup.engineering.ksm.touchscreen.dialogs.files.AbstractLoadDialog;
import java.io.File;

/**
 *
 * @author christian
 */
public class LoadDialog extends AbstractLoadDialog {

    public static void showDialog(PersUtil retainPersUtil, RetainDialog update) {
        LoadDialog dlg = new LoadDialog(retainPersUtil, update);
        dlg.setVisible(true);
    }

    private final PersUtil retainPersUtil;
    private final RetainDialog update;

    public LoadDialog(PersUtil retainPersUtil, RetainDialog update) {
        super(Main.getParamPath());
        this.retainPersUtil = retainPersUtil;
        this.update = update;
    }

    @Override
    protected void loadFile(File file) {
        RetainMain retainData = this.retainPersUtil.loadRetain(file);
        update.setRetainData(retainData);
    }

}
