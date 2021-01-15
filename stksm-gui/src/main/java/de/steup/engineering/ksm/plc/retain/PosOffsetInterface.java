/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.retain;

/**
 *
 * @author sascha
 */
public interface PosOffsetInterface {

    public double getOffOffset();

    public void setOffOffset(double offOffset);

    public double getOnOffset();

    public void setOnOffset(double onOffset);

    public double getPos();

    public void setPos(double pos);

}
