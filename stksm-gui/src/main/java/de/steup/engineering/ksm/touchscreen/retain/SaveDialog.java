package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import de.steup.engineering.ksm.touchscreen.dialogs.files.AbstractSaveDialog;
import java.awt.Window;
import java.io.File;

/**
 *
 * @author christian
 */
public class SaveDialog extends AbstractSaveDialog {

    public static void showDialog(Window owner, RetainMain retainData) {
        PersUtil retainPersUtil = new PersUtil();
        SaveDialog dlb = new SaveDialog(owner, retainData, retainPersUtil);
        dlb.setVisible(true);
    }

    private final RetainMain retainData;
    private final PersUtil retainPersUtil;

    public SaveDialog(Window owner, RetainMain retainData, PersUtil retainPersUtil) {
        super(owner, Main.getParamPath());
        this.retainData = retainData;
        this.retainPersUtil = retainPersUtil;
    }

    @Override
    protected void saveFile(File file) {
        this.retainPersUtil.saveRetain(this, this.retainData, file);
    }

}
