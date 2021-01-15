/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author sascha
 */
public class LogoLabel extends JLabel {

    private static final long serialVersionUID = -6072109124401703833L;

    public LogoLabel() {
        super();
        URL logoUrl = getClass().getResource("/de/steup/engineering/logo.png");
        ImageIcon logoIcon = new ImageIcon(logoUrl);
        setIcon(logoIcon);
    }

}
