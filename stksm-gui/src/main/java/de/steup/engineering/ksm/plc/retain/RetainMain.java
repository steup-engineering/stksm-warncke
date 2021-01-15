/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.retain;

import de.steup.engineering.ksm.Main;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sascha
 */
@XmlRootElement(name = "retain")
@XmlAccessorType(XmlAccessType.FIELD)
public class RetainMain implements Serializable {

    private static final long serialVersionUID = 1756291145449745504L;

    @XmlElement(name = "retainFace")
    private final RetainFace faces[] = new RetainFace[Main.FACE_COUNT];
    @XmlElement(name = "cleaner")
    private final RetainFace cleaners[] = new RetainFace[Main.CLEANER_COUNT];
    @XmlElement(name = "unidev")
    private final RetainFace unidevs[] = new RetainFace[Main.UNIDEV_COUNT];
    @XmlElement(name = "bevel")
    private final RetainBevel bevels[] = new RetainBevel[Main.BEVEL_COUNT];
    @XmlElement(name = "roll")
    private final RetainFace rolls[] = new RetainFace[Main.ROLLS_COUNT];

    public RetainMain() {
        for (int i = 0; i < Main.FACE_COUNT; i++) {
            faces[i] = new RetainFace();
        }
        for (int i = 0; i < Main.CLEANER_COUNT; i++) {
            cleaners[i] = new RetainFace();
        }
        for (int i = 0; i < Main.UNIDEV_COUNT; i++) {
            unidevs[i] = new RetainFace();
        }
        for (int i = 0; i < Main.BEVEL_COUNT; i++) {
            bevels[i] = new RetainBevel();
        }
        for (int i = 0; i < Main.ROLLS_COUNT; i++) {
            rolls[i] = new RetainFace();
        }
    }

    public void update(RetainMain src) {
        if (src == null) {
            return;
        }

        RetainFace.update(faces, src.faces);
        RetainFace.update(cleaners, src.cleaners);
        RetainFace.update(unidevs, src.unidevs);
        RetainBevel.update(bevels, src.bevels);
        RetainFace.update(rolls, src.rolls);
    }

    public RetainBevel[] getBevels() {
        return bevels;
    }

    public RetainFace[] getCleaners() {
        return cleaners;
    }

    public RetainFace[] getFaces() {
        return faces;
    }

    public RetainFace[] getUnidevs() {
        return unidevs;
    }

    public RetainFace[] getRolls() {
        return rolls;
    }
}
