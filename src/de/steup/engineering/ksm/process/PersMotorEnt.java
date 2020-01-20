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
@XmlRootElement(name = "motor")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersMotorEnt implements Serializable {

    private static final long serialVersionUID = -7420784315838580219L;

    @XmlAttribute(name = "index")
    private int index;
    @XmlAttribute(name = "ena")
    private boolean ena;
    @XmlAttribute(name = "caption")
    private String caption;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isEna() {
        return ena;
    }

    public void setEna(boolean ena) {
        this.ena = ena;
    }

}
