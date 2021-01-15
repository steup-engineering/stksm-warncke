/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.util;

import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author sascha
 */
public abstract class MachButtonListener implements MouseListener {

    @Override
    final public void mouseClicked(MouseEvent me) {
        // NOP
    }

    @Override
    final public void mousePressed(MouseEvent me) {
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            stateChanged(guiInData, true);
        }
    }

    @Override
    final public void mouseReleased(MouseEvent me) {
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            stateChanged(guiInData, false);
        }
    }

    @Override
    final public void mouseEntered(MouseEvent me) {
        // NOP
    }

    @Override
    final public void mouseExited(MouseEvent me) {
        // NOP
    }

    protected abstract void stateChanged(GuiInMain guiInData, boolean pressed);
}
