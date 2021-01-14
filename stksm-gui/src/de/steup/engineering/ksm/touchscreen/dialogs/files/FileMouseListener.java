/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs.files;

import de.steup.engineering.ksm.touchscreen.dialogs.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 *
 * @author sascha
 */
public class FileMouseListener implements MouseListener, ActionListener {

    private final PathConfig pathConfig;
    private final StringSetter setter;

    private class LookupDialog extends AbstractLoadDialog {

        public LookupDialog() {
            super(pathConfig);
        }

        @Override
        protected void loadFile(File file) {
            
            String rootPath = pathConfig.getRoot().getAbsolutePath();
            String val = file.getAbsolutePath().substring(rootPath.length() + 1);
            int i = val.lastIndexOf('.');
            if (i >= 0) {
                val = val.substring(0, i);
            }

            if (setter != null) {
                setter.setValue(val);
            }
        }

    }

    public FileMouseListener(PathConfig pathConfig, StringSetter setter) {
        this.pathConfig = pathConfig;
        this.setter = setter;
    }

    public PathConfig getPathConfig() {
        return pathConfig;
    }

    public StringSetter getSetter() {
        return setter;
    }

    private void showDialog() {
        LookupDialog dlg = new LookupDialog();
        dlg.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        showDialog();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // NOP
    }

}
