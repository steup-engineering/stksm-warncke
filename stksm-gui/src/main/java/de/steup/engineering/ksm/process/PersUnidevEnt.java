/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.process;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sascha
 */
@XmlRootElement(name = "unidev")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersUnidevEnt implements Serializable {

    private static final long serialVersionUID = 430278511393134729L;

    @XmlAttribute(name = "index")
    private int index;
    @XmlAttribute(name = "ena")
    private boolean ena;
    @XmlAttribute(name = "marginStart")
    private double marginStart;
    @XmlAttribute(name = "marginEnd")
    private double marginEnd;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEna() {
        return ena;
    }

    public void setEna(boolean ena) {
        this.ena = ena;
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

}
