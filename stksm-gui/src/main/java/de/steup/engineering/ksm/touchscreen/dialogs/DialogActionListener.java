/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.touchscreen.dialogs;

import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public interface DialogActionListener {

    public void cancelAction();
    public void okAction();
    public JTextField getTextField();

}
