/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public class GuiOutWhmStation implements GuiOutStationInterface {

    private boolean active;
    private final GuiOutWhm whm;

    public GuiOutWhmStation() {
        whm = new GuiOutWhm();
    }

    public static void update(GuiOutWhmStation[] dst, GuiOutWhmStation[] src) {
        if (src == null) {
            return;
        }

        for (int i = 0; i < Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }

    public void update(GuiOutWhmStation src) {
        if (src == null) {
            return;
        }

        active = src.active;
        whm.update(src.whm);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public GuiOutWhm getWhm() {
        return whm;
    }
}
