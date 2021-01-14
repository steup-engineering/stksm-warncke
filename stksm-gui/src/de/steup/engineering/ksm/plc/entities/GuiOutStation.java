/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public class GuiOutStation implements GuiOutStationInterface {

    private boolean active;

    public static void update(GuiOutStation[] dst, GuiOutStation[] src) {
        if (src == null) {
            return;
        }

        for (int i=0; i<Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }
    
    public void update(GuiOutStation src) {
        if (src == null) {
            return;
        }
        
        active = src.active;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
}
