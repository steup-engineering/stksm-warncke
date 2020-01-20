/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.steup.engineering.ksm.touchscreen.dialogs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;

/**
 *
 * @author sascha
 */
public class FilenameMouseListener implements MouseListener {

    private final String title;
    private final JTextField field;
    private final int minLen;
    private final int maxLen;
    private final StringSetter setter;

    public FilenameMouseListener(String title, JTextField field, int minLen, int maxLen, StringSetter setter) {
        this.title = title;
        this.field = field;
        this.minLen = minLen;
        this.maxLen = maxLen;
        this.setter = setter;
    }

    public String getTitle() {
        return title;
    }

    public JTextField getField() {
        return field;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public int getMinLen() {
        return minLen;
    }

    public StringSetter getSetter() {
        return setter;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        AlphaDialog.showDialog(title, field, minLen, maxLen, setter);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // NOP
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // NOP
    }

}
