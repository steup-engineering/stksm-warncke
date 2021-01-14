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
public class GuiInMain {

    @JsonIgnore
    private String processName = null;
    @JsonIgnore
    private String paramSetName = null;

    private boolean resetError;
    private double beltFeed;
    private boolean whmJobReset;
    private final GuiInStation faces[] = new GuiInStation[Main.FACE_COUNT];
    private final GuiInStation rolls[] = new GuiInStation[Main.ROLLS_COUNT];
    private final GuiInStation cleaners[] = new GuiInStation[Main.CLEANER_COUNT];
    private final GuiCleanerMode cleanersMode[] = new GuiCleanerMode[Main.CLEANER_MODE_COUNT];
    private final GuiInUnidev unidevs[] = new GuiInUnidev[Main.UNIDEV_COUNT];
    private final GuiInBevel bevels[] = new GuiInBevel[Main.BEVEL_COUNT];

    public GuiInMain() {
        for (int i = 0; i < Main.FACE_COUNT; i++) {
            faces[i] = new GuiInStation();
        }
        for (int i = 0; i < Main.ROLLS_COUNT; i++) {
            rolls[i] = new GuiInStation();
        }
        for (int i = 0; i < Main.CLEANER_COUNT; i++) {
            cleaners[i] = new GuiInStation();
        }
        for (int i = 0; i < Main.CLEANER_MODE_COUNT; i++) {
            cleanersMode[i] = new GuiCleanerMode();
        }
        for (int i = 0; i < Main.UNIDEV_COUNT; i++) {
            unidevs[i] = new GuiInUnidev();
        }
        for (int i = 0; i < Main.BEVEL_COUNT; i++) {
            bevels[i] = new GuiInBevel();
        }
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getParamSetName() {
        return paramSetName;
    }

    public void setParamSetName(String paramSetName) {
        this.paramSetName = paramSetName;
    }

    public double getBeltFeed() {
        return beltFeed;
    }

    public void setBeltFeed(double beltFeed) {
        this.beltFeed = beltFeed;
    }

    public boolean isResetError() {
        return resetError;
    }

    public void setResetError(boolean resetError) {
        this.resetError = resetError;
    }

    public boolean isWhmJobReset() {
        return whmJobReset;
    }

    public void setWhmJobReset(boolean whmJobReset) {
        this.whmJobReset = whmJobReset;
    }

    public GuiInBevel[] getBevels() {
        return bevels;
    }

    public GuiInStation[] getCleaners() {
        return cleaners;
    }

    public GuiCleanerMode[] getCleanersMode() {
        return cleanersMode;
    }

    public GuiInStation[] getFaces() {
        return faces;
    }

    public GuiInStation[] getRolls() {
        return rolls;
    }

    public GuiInUnidev[] getUnidevs() {
        return unidevs;
    }
}
