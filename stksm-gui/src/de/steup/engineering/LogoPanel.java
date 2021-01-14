/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author sascha
 */
public class LogoPanel extends JPanel {

    private static final long serialVersionUID = -8523838241072934922L;

    private final BufferedImage logoImage;

    private static BufferedImage loadImage(URL url) {
        try {
            // load image
            return ImageIO.read(url);
        } catch (IOException ex) {
            return null;
        }
    }

    public LogoPanel() {
        super();

        // get logo url
        URL logoUrl = getClass().getResource("/de/steup/engineering/logo.png");
        logoImage = loadImage(logoUrl);

        if (logoImage != null) {
            setSize(logoImage.getWidth(), logoImage.getHeight());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(logoImage, 0, 0, null);
    }

}
