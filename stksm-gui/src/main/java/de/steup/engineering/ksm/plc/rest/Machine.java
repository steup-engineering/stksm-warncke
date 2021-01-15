/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.steup.engineering.ksm.plc.entities.GuiInMain;
import de.steup.engineering.ksm.plc.entities.GuiOutMain;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author sascha
 */
public class Machine {

    private static final int CONNECTION_TIMEOUT_MILLSEC = 5000;

    private boolean connected = false;
    private URL restUrlGuiOutMain;
    private URL restUrlGuiInMain;
    private URL restUrlRetainMain;

    private final ObjectMapper mapper = new ObjectMapper();

    public void connect(String url) throws PlcRestException {
        if (connected) {
            throw new PlcRestException("Already connected.");
        }

        URL restUrl;
        try {
            restUrl = new URL(url);
        } catch (MalformedURLException ex) {
            throw new PlcRestException("Unable to parse PLC URL.", ex);
        }

        try {
            restUrlGuiOutMain = new URL(restUrl, "GuiOutMain");
            restUrlGuiInMain = new URL(restUrl, "GuiInMain");
            restUrlRetainMain = new URL(restUrl, "RetainMain");
        } catch (MalformedURLException ex) {
            throw new PlcRestException("Unable to build endpoint URL.", ex);
        }

        connected = true;
    }

    public void disconnect() throws PlcRestException {
        connected = false;
        restUrlGuiInMain = null;
        restUrlGuiOutMain = null;
        restUrlRetainMain = null;
    }

    public boolean isConnected() {
        return connected;
    }

    public void writeGuiInData(GuiInMain data) throws PlcRestException {
        writeGenericData(restUrlGuiInMain, data);
    }

    public void readGuiOutData(GuiOutMain data) throws PlcRestException {
        data.update((GuiOutMain) readGenericData(restUrlGuiOutMain, GuiOutMain.class));
    }

    public void writeRetainData(RetainMain data) throws PlcRestException {
        writeGenericData(restUrlRetainMain, data);
    }

    public RetainMain readRetainData() throws PlcRestException {
        return (RetainMain) readGenericData(restUrlRetainMain, RetainMain.class);
    }

    private Object readGenericData(URL url, Class clazz) throws PlcRestException {
        if (!connected) {
            throw new PlcRestException("Not connected.");
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            disconnect();
            throw new PlcRestException("Unable to connect to "
                    + clazz.getSimpleName() + " endpoint", ex);
        }

        try {
            InputStream is;
            int status;
            try {
                conn.setReadTimeout(CONNECTION_TIMEOUT_MILLSEC);  // TODO SocketTimeoutException
                is = conn.getInputStream();
                status = conn.getResponseCode();
            } catch (IOException ex) {
                disconnect();
                throw new PlcRestException("Unable to read from "
                        + clazz.getSimpleName() + " endpoint", ex);
            }

            if (status != 200) {
                throw new PlcRestException("Server error", status);
            }

            try {
                return mapper.readValue(is, clazz);
            } catch (IOException ex) {
                disconnect();
                throw new PlcRestException("Unable to deserialize data from "
                        + clazz.getSimpleName() + " endpoint", ex);
            }
        } finally {
            conn.disconnect();
        }
    }

    private void writeGenericData(URL url, Object data) throws PlcRestException {
        if (!connected) {
            throw new PlcRestException("Not connected.");
        }

        Class clazz = data.getClass();

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            disconnect();
            throw new PlcRestException("Unable to connect to "
                    + clazz.getSimpleName() + " endpoint", ex);
        }

        try {
            OutputStream os;
            try {
                // TODO timeout ?
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                os = conn.getOutputStream();
            } catch (IOException ex) {
                disconnect();
                throw new PlcRestException("Unable to write to "
                        + clazz.getSimpleName() + " endpoint", ex);
            }

            int status;
            try {
                mapper.writeValue(os, data);
                status = conn.getResponseCode();
            } catch (IOException ex) {
                disconnect();
                throw new PlcRestException("Unable to serialize data to "
                        + clazz.getSimpleName() + " endpoint", ex);
            }

            if (status != 200) {
                throw new PlcRestException("Server error", status);
            }
        } finally {
            conn.disconnect();
        }
    }

}
