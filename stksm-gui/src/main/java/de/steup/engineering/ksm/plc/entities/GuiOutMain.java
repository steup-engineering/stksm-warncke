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

    public static final int ERR_EMERG_STOP = (1 << 0);
    public static final int ERR_DOOR_INTERLOCK = (1 << 1);
    public static final int ERR_AIR_PRESS = (1 << 2);
    public static final int ERR_WATER_PRESS = (1 << 3);
    public static final int ERR_MOTOR_PROT = (1 << 4);
    public static final int ERR_BELT = (1 << 5);
    public static final int ERR_UNIDEV = (1 << 6);
    public static final int ERR_BEVEL_LOWER = (1 << 7);
    public static final int ERR_BEVEL_UPPER = (1 << 8);
    public static final int ERR_BUS_SYSTEM = (1 << 9);
    public static final int ERR_MODSEL_CLEAN1 = (1 << 10);

    private int errors;
    private boolean running;
    private double feedOverride;
    private double probePos;
    private final GuiOutWhmStation faces[] = new GuiOutWhmStation[Main.FACE_COUNT];
    private final GuiOutStation rolls[] = new GuiOutStation[Main.ROLLS_COUNT];
    private final GuiOutWhmStation cleaners[] = new GuiOutWhmStation[Main.CLEANER_COUNT];
    private final GuiOutCleanerMode cleanersMode[] = new GuiOutCleanerMode[Main.CLEANER_MODE_COUNT];
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
            cleanersMode[i] = new GuiOutCleanerMode();
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
        GuiOutCleanerMode.update(cleanersMode, src.cleanersMode);
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

    public GuiOutCleanerMode[] getCleanersMode() {
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
