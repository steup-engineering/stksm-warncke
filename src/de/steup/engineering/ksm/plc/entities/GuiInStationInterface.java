/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author sascha
 */
public interface GuiInStationInterface {

    public boolean isEna();

    public void setEna(boolean ena);

    public boolean isManu();

    public void setManu(boolean manu);

    @JsonIgnore
    public boolean isCaptionEditable();

    @JsonIgnore
    public String getCaption();

    public void setCaption(String caption);

}
