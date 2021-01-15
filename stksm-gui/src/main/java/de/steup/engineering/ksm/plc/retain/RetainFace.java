/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.retain;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sascha
 */
@XmlRootElement(name = "face")
@XmlAccessorType(XmlAccessType.FIELD)
public class RetainFace implements PosOffsetInterface, Serializable {

    private static final long serialVersionUID = -6750150560154553901L;

    @XmlAttribute(name ="pos")
    private double pos;
    @XmlAttribute(name ="onOffset")
    private double onOffset;
    @XmlAttribute(name ="offOffset")
    private double offOffset;

    public static void update(RetainFace[] dst, RetainFace[] src) {
        if (src == null) {
            return;
        }

        for (int i = 0; i < Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }

    public void update(RetainFace src) {
        if (src == null) {
            return;
        }

        pos = src.pos;
        onOffset = src.onOffset;
        offOffset = src.offOffset;
    }

    @Override
    public double getOffOffset() {
        return offOffset;
    }

    @Override
    public void setOffOffset(double offOffset) {
        this.offOffset = offOffset;
    }

    @Override
    public double getOnOffset() {
        return onOffset;
    }

    @Override
    public void setOnOffset(double onOffset) {
        this.onOffset = onOffset;
    }

    @Override
    public double getPos() {
        return pos;
    }

    @Override
    public void setPos(double pos) {
        this.pos = pos;
    }
}
