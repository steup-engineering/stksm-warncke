/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author sascha
 */
public class MotorBasePanel extends JPanel {

    private static final long serialVersionUID = 4448745129273227435L;

    public MotorBasePanel(String title, int rowCount) {
        super();

        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        }

        GridLayout layout = new GridLayout(0, rowCount);
        layout.setHgap(4);
        layout.setVgap(10);
        setLayout(layout);
    }

}
