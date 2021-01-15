/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public class GuiCleanerMode {

    private boolean clean;
    private boolean calib;

    public static void update(GuiCleanerMode[] dst, GuiCleanerMode[] src) {
        if (src == null) {
            return;
        }

        for (int i=0; i<Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }

    public void update(GuiCleanerMode src) {
        if (src == null) {
            return;
        }

        clean = src.clean;
        calib = src.calib;
    }

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public boolean isCalib() {
        return calib;
    }

    public void setCalib(boolean calib) {
        this.calib = calib;
    }

}
