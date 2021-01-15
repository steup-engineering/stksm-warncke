/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.process;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sascha
 */
@XmlRootElement(name = "bevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersBevelEnt implements Serializable {

    private static final long serialVersionUID = -1162286332457682497L;

    @XmlAttribute(name = "index")
    private int index;
    @XmlAttribute(name = "width")
    private double width;
    @XmlAttribute(name = "marginStart")
    private double marginStart;
    @XmlAttribute(name = "marginEnd")
    private double marginEnd;
    @XmlElement(name = "motor")
    private ArrayList<PersMotorEnt> motors;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getMarginEnd() {
        return marginEnd;
    }

    public void setMarginEnd(double marginEnd) {
        this.marginEnd = marginEnd;
    }

    public double getMarginStart() {
        return marginStart;
    }

    public void setMarginStart(double marginStart) {
        this.marginStart = marginStart;
    }

    public ArrayList<PersMotorEnt> getMotors() {
        return motors;
    }

    public void setMotors(ArrayList<PersMotorEnt> motors) {
        this.motors = motors;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

}
