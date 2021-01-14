/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

import de.steup.engineering.ksm.Main;

/**
 *
 * @author sascha
 */
public class GuiOutMain {

    public static final int ERR_EMERG_STOP = 0x00000001;
    public static final int ERR_DOOR_INTERLOCK = 0x00000002;
    public static final int ERR_AIR_PRESS = 0x00000004;
    public static final int ERR_WATER_PRESS = 0x00000008;
    public static final int ERR_MOTOR_PROT = 0x00000010;
    public static final int ERR_BELT = 0x00000020;
    public static final int ERR_UNIDEV = 0x00000040;
    public static final int ERR_BEVEL_LOWER = 0x00000080;
    public static final int ERR_BEVEL_UPPER = 0x00000100;
    public static final int ERR_BUS = 0x00000200;

    private int errors;
    private boolean running;
    private double feedOverride;
    private double probePos;
    private final GuiOutWhmStation faces[] = new GuiOutWhmStation[Main.FACE_COUNT];
    private final GuiOutStation rolls[] = new GuiOutStation[Main.ROLLS_COUNT];
    private final GuiOutWhmStation cleaners[] = new GuiOutWhmStation[Main.CLEANER_COUNT];
    private final GuiCleanerMode cleanersMode[] = new GuiCleanerMode[Main.CLEANER_MODE_COUNT];
    private final GuiOutUnidev unidevs[] = new GuiOutUnidev[Main.UNIDEV_COUNT];
    private final GuiOutBevel bevels[] = new GuiOutBevel[Main.BEVEL_COUNT];
    private final GuiOutWhm whm;
    private final GuiOutWhm whmJob;

    public GuiOutMain() {
        for (int i = 0; i < Main.FACE_COUNT; i++) {
            faces[i] = new GuiOutWhmStation();
        }
        for (int i = 0; i < Main.ROLLS_COUNT; i++) {
            rolls[i] = new GuiOutStation();
        }
        for (int i = 0; i < Main.CLEANER_COUNT; i++) {
            cleaners[i] = new GuiOutWhmStation();
        }
        for (int i = 0; i < Main.CLEANER_MODE_COUNT; i++) {
            cleanersMode[i] = new GuiCleanerMode();
        }
        for (int i = 0; i < Main.UNIDEV_COUNT; i++) {
            unidevs[i] = new GuiOutUnidev();
        }
        for (int i = 0; i < Main.BEVEL_COUNT; i++) {
            bevels[i] = new GuiOutBevel();
        }
        whm = new GuiOutWhm();
        whmJob = new GuiOutWhm();
    }

    public void update(GuiOutMain src) {
        if (src == null) {
            return;
        }

        errors = src.errors;
        running = src.running;
        feedOverride = src.feedOverride;
        probePos = src.probePos;

        GuiOutWhmStation.update(faces, src.faces);
        GuiOutStation.update(rolls, src.rolls);
        GuiOutWhmStation.update(cleaners, src.cleaners);
        GuiCleanerMode.update(cleanersMode, src.cleanersMode);
        GuiOutUnidev.update(unidevs, src.unidevs);
        GuiOutBevel.update(bevels, src.bevels);
        
        whm.update(src.whm);
        whmJob.update(src.whmJob);
    }

    public int getErrors() {
        return errors;
    }

    public double getFeedOverride() {
        return feedOverride;
    }

    public double getProbePos() {
        return probePos;
    }

    public boolean isRunning() {
        return running;
    }

    public GuiOutBevel[] getBevels() {
        return bevels;
    }

    public GuiOutWhmStation[] getCleaners() {
        return cleaners;
    }

    public GuiCleanerMode[] getCleanersMode() {
        return cleanersMode;
    }

    public GuiOutWhmStation[] getFaces() {
        return faces;
    }

    public GuiOutStation[] getRolls() {
        return rolls;
    }

    public GuiOutUnidev[] getUnidevs() {
        return unidevs;
    }

    public GuiOutWhm getWhm() {
        return whm;
    }

    public GuiOutWhm getWhmJob() {
        return whmJob;
    }
}
