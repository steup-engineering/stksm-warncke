package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.Main;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import de.steup.engineering.ksm.touchscreen.dialogs.files.AbstractSaveDialog;
import java.io.File;

/**
 *
 * @author christian
 */
public class SaveDialog extends AbstractSaveDialog {

    public static void showDialog(RetainMain retainData) {
        PersUtil retainPersUtil = new PersUtil();
        SaveDialog dlb = new SaveDialog(retainData, retainPersUtil);
        dlb.setVisible(true);
    }

    private final RetainMain retainData;
    private final PersUtil retainPersUtil;

    public SaveDialog(RetainMain retainData, PersUtil retainPersUtil) {
        super(Main.getParamPath());
        this.retainData = retainData;
        this.retainPersUtil = retainPersUtil;
    }

    @Override
    protected void saveFile(File file) {
        this.retainPersUtil.saveRetain(this.retainData, file);
    }

}
