/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.steup.engineering.ksm.Main;
import javax.swing.JDialog;

/**
 *
 * @author sascha
 */
public class GuiOutBevel {

    public static final int CALIB_STEP_NONE = 0;
    public static final int CALIB_STEP_START = 1;
    public static final int CALIB_STEP_MATPREP = 2;
    public static final int CALIB_STEP_TOUCH = 3;

    private final GuiOutWhmStation motors[] = new GuiOutWhmStation[Main.BEVEL_MOTOR_COUNT];
    private double axisPos;
    private int calibStep;
    private boolean calibError;
    private final GuiOutWhm whm;

    @JsonIgnore
    private JDialog calibDialog;

    public GuiOutBevel() {
        for (int i = 0; i < Main.BEVEL_MOTOR_COUNT; i++) {
            motors[i] = new GuiOutWhmStation();
        }
        whm = new GuiOutWhm();
    }

    public static void update(GuiOutBevel[] dst, GuiOutBevel[] src) {
        if (src == null) {
            return;
        }

        for (int i = 0; i < Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }

    public void update(GuiOutBevel src) {
        if (src == null) {
            return;
        }

        axisPos = src.axisPos;
        calibStep = src.calibStep;
        calibError = src.calibError;

        GuiOutWhmStation.update(motors, src.motors);
        whm.update(src.whm);
    }

    public double getAxisPos() {
        return axisPos;
    }

    public void setAxisPos(double axisPos) {
        this.axisPos = axisPos;
    }

    public int getCalibStep() {
        return calibStep;
    }

    public void setCalibStep(int calibStep) {
        this.calibStep = calibStep;
    }

    public GuiOutWhmStation[] getMotors() {
        return motors;
    }

    public JDialog getCalibDialog() {
        return calibDialog;
    }

    public void setCalibDialog(JDialog calibDialog) {
        this.calibDialog = calibDialog;
    }

    public boolean isCalibError() {
        return calibError;
    }

    public void setCalibError(boolean calibError) {
        this.calibError = calibError;
    }

    public GuiOutWhm getWhm() {
        return whm;
    }
}
