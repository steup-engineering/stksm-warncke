/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs.files;

import java.io.File;

/**
 *
 * @author christian
 */
public class PathConfig {

    final private String desc;
    final private File root;
    private File lastPath;

    public PathConfig(String desc, String root) {
        this(desc, new File(root));
    }

    public PathConfig(String desc, File root) {
        this.desc = desc;
        this.root = root;
        this.lastPath = root;
    }

    public String getDesc() {
        return desc;
    }

    public File getRoot() {
        return root;
    }

    public File getLastPath() {
        return lastPath;
    }

    public void setLastPath(File lastPath) {
        if (lastPath == null) {
            lastPath = root;
        }

        this.lastPath = lastPath;
    }
}
