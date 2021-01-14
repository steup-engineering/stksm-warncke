/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.plc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.steup.engineering.ksm.Main;

/**
 *
 * @author sascha
 */
public class GuiInBevel implements GuiInDevInterface, GuiCalibInterface {

    private boolean calibStart;
    private boolean calibCancel;
    private final GuiInStation motors[] = new GuiInStation[Main.BEVEL_MOTOR_COUNT];
    private double width;
    private double marginStart;
    private double marginEnd;

    public GuiInBevel() {
        for (int i=0; i<Main.BEVEL_MOTOR_COUNT; i++) {
            motors[i] = new GuiInStation();
        }
    }

    @JsonIgnore
    @Override
    public double getDist() {
        return getWidth();
    }

    @Override
    public void setDist(double val) {
        setWidth(val);
    }

    @Override
    public double getMarginStart() {
        return marginStart;
    }

    @Override
    public void setMarginStart(double marginStart) {
        this.marginStart = marginStart;
    }

    @Override
    public double getMarginEnd() {
        return marginEnd;
    }

    @Override
    public void setMarginEnd(double marginEnd) {
        this.marginEnd = marginEnd;
    }

    @Override
    public boolean isCalibCancel() {
        return calibCancel;
    }

    @Override
    public void setCalibCancel(boolean calibCancel) {
        this.calibCancel = calibCancel;
    }

    @Override
    public boolean isCalibStart() {
        return calibStart;
    }

    @Override
    public void setCalibStart(boolean calibStart) {
        this.calibStart = calibStart;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public GuiInStation[] getMotors() {
        return motors;
    }
}
