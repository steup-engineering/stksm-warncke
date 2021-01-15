/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author sascha
 */
public class EnableMouseListener implements MouseListener {

    private final MouseListener impl;
    private boolean enabled;

    public EnableMouseListener(MouseListener impl) {
        this.impl = impl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (enabled) {
            impl.mouseClicked(me);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (enabled) {
            impl.mousePressed(me);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (enabled) {
            impl.mouseReleased(me);
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if (enabled) {
            impl.mouseEntered(me);
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (enabled) {
            impl.mouseExited(me);
        }
    }

}
