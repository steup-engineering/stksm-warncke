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
@XmlRootElement(name = "unidev")
@XmlAccessorType(XmlAccessType.FIELD)
public class RetainUnidev implements Serializable {

    private static final long serialVersionUID = 7356938339789650648L;

    @XmlAttribute(name ="pos")
    private double pos;

    public static void update(RetainUnidev[] dst, RetainUnidev[] src) {
        if (src == null) {
            return;
        }

        for (int i = 0; i < Math.min(src.length, dst.length); i++) {
            dst[i].update(src[i]);
        }
    }

    public void update(RetainUnidev src) {
        if (src == null) {
            return;
        }

        pos = src.pos;
    }

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos = pos;
    }
}
