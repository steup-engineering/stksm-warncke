/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author sascha
 */
public class GuiInStation implements GuiInStationInterface {

    private boolean manu;
    private boolean ena;

    @JsonIgnore
    private String caption;

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
    public String getCaption() {
        return caption;
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public boolean isCaptionEditable() {
        return true;
    }
}
