/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public class GuiInCleanerMode {

    private boolean clean;
    private boolean calib;
    private double calibHeight;

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

    public double getCalibHeight() {
        return calibHeight;
    }

    public void setCalibHeight(double calibHeight) {
        this.calibHeight = calibHeight;
    }

}
