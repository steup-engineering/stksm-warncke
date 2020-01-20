/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author sascha
 */
public class MainPanel extends JPanel implements UpdatePanelInterface {

    private static final long serialVersionUID = -1443069100727965677L;

    private static final Dimension PREFERED_SIZE = new Dimension(1024, 768);
    private static final KeyStroke EXIT_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
    private static final ActionListener EXIT_ACTION_LISTENER = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    private final List<UpdatePanelInterface> updatePanels = new ArrayList<>();

    public MainPanel() {
        super();
        setPreferredSize(PREFERED_SIZE);

        registerKeyboardAction(EXIT_ACTION_LISTENER, EXIT_KEY_STROKE, JComponent.WHEN_IN_FOCUSED_WINDOW);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        HeaderPanel headerPanel = new HeaderPanel();
        updatePanels.add(headerPanel);
        ProcessPanel procPanel = new ProcessPanel();
        updatePanels.add(procPanel);
        FooterPanel footerPanel = new FooterPanel(this);

        add(headerPanel, BorderLayout.PAGE_START);
        add(procPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.PAGE_END);
    }

    @Override
    public void update() {
        for (UpdatePanelInterface updatePanel : updatePanels) {
            updatePanel.update();
        }
    }
}
