/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public class GuiOutUnidev implements GuiOutStationInterface {

    private double selectedPos;
    private boolean active;
    private final GuiOutWhm whm;

    public GuiOutUnidev() {
        whm = new GuiOutWhm();
    }

    public static void update(GuiOutUnidev[] dst, GuiOutUnidev[] src) {
        if (src == null) {
            return;
        }

        for (int i = 0; i < Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }

    public void update(GuiOutUnidev src) {
        if (src == null) {
            return;
        }

        selectedPos = src.selectedPos;
        active = src.active;
        whm.update(src.whm);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public double getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(double selectedPos) {
        this.selectedPos = selectedPos;
    }

    public GuiOutWhm getWhm() {
        return whm;
    }
}
