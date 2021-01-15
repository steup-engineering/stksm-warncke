/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen;

import de.steup.engineering.LogoLabel;
import de.steup.engineering.ksm.plc.rest.MachineThread;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author sascha
 */
public class HeaderPanel extends JPanel implements UpdatePanelInterface {

    private static final long serialVersionUID = 3970127795209077747L;

    private static final String NON_PROC_NAME = "<Kein Ablauf geladen>";
    private final JLabel procLabel;

    public HeaderPanel() {
        super();
        setBorder(BorderFactory.createEtchedBorder());

        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        LogoLabel logoLabel = new LogoLabel();

        JLabel titleLabel = new JLabel("Kantenschleifautomat STKSM 10-6B/1+3 Modell SIRIUS");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        procLabel = new JLabel(NON_PROC_NAME);

        add(logoLabel, BorderLayout.LINE_START);
        add(titleLabel, BorderLayout.CENTER);
        add(procLabel, BorderLayout.LINE_END);

    }

    @Override
    public void update() {
        GuiInMain guiInData = MachineThread.getInstance().getGuiInData();
        synchronized (guiInData) {
            String procName = guiInData.getProcessName();
            if (procName == null || procName.isEmpty()) {
                procName = NON_PROC_NAME;
            }
            procLabel.setText(procName);
        }
    }
}
