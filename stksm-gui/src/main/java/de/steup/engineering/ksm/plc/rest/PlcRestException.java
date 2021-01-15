/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.plc.rest;

import java.io.IOException;

/**
 *
 * @author sascha
 */
public class PlcRestException extends IOException {

    private static final long serialVersionUID = -3546385925752536135L;

    private int status = 0;

    public PlcRestException(String message) {
        super(message);
    }

    public PlcRestException(String message, int status) {
        super(message);
        this.status = status;
    }

    public PlcRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatus() {
        return status;
    }

}
