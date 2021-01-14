/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

/**
 *
 * @author sascha
 */
public interface GuiCalibInterface {

    public boolean isCalibCancel();

    public void setCalibCancel(boolean calibCancel);

    public boolean isCalibStart();

    public void setCalibStart(boolean calibStart);
}
