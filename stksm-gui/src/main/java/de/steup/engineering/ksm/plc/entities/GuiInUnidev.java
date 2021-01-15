/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public class GuiInUnidev implements GuiInStationInterface, GuiInDevInterface {

    private boolean manu;
    private boolean ena;
    private double marginStart;
    private double marginEnd;

    @Override
    public double getDist() {
        return 0.0;
    }

    @Override
    public void setDist(double val) {
        // NOP
    }

    @Override
    public boolean isEna() {
        return ena;
    }

    @Override
    public void setEna(boolean ena) {
        this.ena = ena;
    }

    @Override
    public boolean isManu() {
        return manu;
    }

    @Override
    public void setManu(boolean manu) {
        this.manu = manu;
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
    public String getCaption() {
        return null;
    }

    @Override
    public void setCaption(String caption) {
        // NOP
    }

    @Override
    public boolean isCaptionEditable() {
        return false;
    }
}
