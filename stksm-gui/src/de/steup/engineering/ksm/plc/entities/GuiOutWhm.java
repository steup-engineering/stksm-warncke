/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

import java.io.Serializable;

/**
 *
 * @author sascha
 */
public class GuiOutWhm implements Serializable {

    private static final long serialVersionUID = -4931475735887218287L;

    private int secs;
    private int dcms;

    public void update(GuiOutWhm src) {
        if (src == null) {
            return;
        }

        secs = src.secs;
        dcms = src.dcms;
    }

    public int getDcms() {
        return dcms;
    }

    public int getSecs() {
        return secs;
    }

    @Override
    public String toString() {
        int dh = secs;
        int ds = dh % 60;
        dh = dh / 60;
        int dm = dh % 60;
        dh = dh / 60;
        return String.format("%d:%02d:%02dh %.1fm",
                dh, dm, ds,
                (double) dcms * 0.1);
    }

    public void setSecs(int workingSecs) {
        this.secs = workingSecs;
    }
}
