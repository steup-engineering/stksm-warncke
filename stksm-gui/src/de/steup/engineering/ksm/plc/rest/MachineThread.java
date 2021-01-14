/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.rest;

import de.steup.engineering.ksm.plc.entities.GuiInBevel;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.entities.GuiOutMain;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sascha
 */
public class MachineThread extends Thread {

    private static MachineThread instance = null;
    private static final int POLL_SLEEP_TIME = 200;
    private static final int RECONNECT_SLEEP_TIME = 3000;
    private boolean terminated = false;
    private final Machine machine = new Machine();
    private String restBaseUrl;
    private final GuiInMain guiInData = new GuiInMain();
    private final GuiOutMain guiOutData = new GuiOutMain();
    private final List<Runnable> updateListeners = new ArrayList<>();

    private MachineThread() {
        super();
    }

    public static MachineThread getInstance() {
        if (instance == null) {
            instance = new MachineThread();
        }
        return instance;
    }

    public GuiInMain getGuiInData() {
        return guiInData;
    }

    public GuiOutMain getGuiOutData() {
        return guiOutData;
    }

    public String getRestBaseUrl() {
        return restBaseUrl;
    }

    public void setRestBaseUrl(String restBaseUrl) {
        this.restBaseUrl = restBaseUrl;
    }

    public void terminate() throws InterruptedException {
        terminated = true;
        join();
    }

    public void addUpdateListener(Runnable listener) {
        synchronized (updateListeners) {
            updateListeners.add(listener);
        }
    }

    public void removeUpdateListener(Runnable listener) {
        synchronized (updateListeners) {
            updateListeners.remove(listener);
        }
    }

    public Machine getMachine() {
        return machine;
    }

    @Override
    public void run() {
        while (!terminated) {
            try {
                machine.connect(restBaseUrl);
                try {
                    while (!terminated) {
                        synchronized (guiOutData) {
                            machine.readGuiOutData(guiOutData);
                        }

                        synchronized (updateListeners) {
                            for (Runnable listener : updateListeners) {
                                listener.run();
                            }
                        }

                        synchronized (guiInData) {
                            machine.writeGuiInData(guiInData);

                            // reset flags
                            guiInData.setResetError(false);
                            for (GuiInBevel bevel : guiInData.getBevels()) {
                                bevel.setCalibStart(false);
                                bevel.setCalibCancel(false);
                            }
                        }

                        try {
                            sleep(POLL_SLEEP_TIME);
                        } catch (InterruptedException eex) {
                            // NOP;
                        }
                    }
                } finally {
                    machine.disconnect();
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                guiOutData.update(null);
                try {
                    machine.disconnect();
                } catch (PlcRestException eex) {
                    // NOP
                }
                try {
                    sleep(RECONNECT_SLEEP_TIME);
                } catch (InterruptedException eex) {
                    // NOP;
                }
            }
        }
        terminated = false;
    }
}
