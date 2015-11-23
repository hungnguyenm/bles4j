package edu.upenn.cis.precise.bles4j.ubertooth;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import edu.upenn.cis.precise.bles4j.ubertooth.core.*;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothExceptionCode;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class UbertoothOne {
    private boolean initialized;
    private IUbertooth ubertooth;
    private IUbertoothControl ubertoothControl;

    // Device initialization
    private boolean connected;
    Pointer device;

    public UbertoothOne() throws UbertoothException {
        ubertooth = (IUbertooth) Native.loadLibrary("ubertooth", IUbertooth.class);
        ubertoothControl = (IUbertoothControl) Native.loadLibrary("ubertooth",
                IUbertoothControl.class);
        initialized = true;

        device = ubertooth.ubertooth_start(-1);
        if (device != null) {
            connected = true;
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NO_DEVICE.getCode());
        }
    }

    // Device Control
    public void stop() {
        if (connected) {
            ubertoothControl.cmd_stop(device);
        }
    }

    public void reset() throws UbertoothException {
        // Reset if device is connected
        if (connected) {
            try {
                connected = false;
                ubertoothControl.cmd_reset(device);
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        // Reconnect device
        device = ubertooth.ubertooth_start(-1);
        if (device != null) {
            connected = true;
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NO_DEVICE.getCode());
        }
    }

    public void cleanUp() {
        if (connected) {
            ubertooth.register_cleanup_handler(device);
        }
    }

    // Ubertooth Utilities
    public boolean isConnected() {
        return connected;
    }

    public String getFirmwareRevision() throws UbertoothException {
        if (connected) {
            byte[] version = new byte[255];
            ubertoothControl.cmd_get_rev_num(device, version, 255);
            return Native.toString(version);
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public String getMcuSerialNumber() throws UbertoothException {
        if (connected) {
            byte[] serial = new byte[17];
            int r = ubertoothControl.cmd_get_serial(device, serial);
            if (r < 0) {
                throw new UbertoothException(r);
            } else {
                String s = "";
                for (int i = 1; i < 14; i += 4) {
                    for (int j = 3; j >= 0; j--) {
                        s += String.format("%02x", serial[i + j]);
                    }
                }
                return s;
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public String getMcuPartNumber() throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_get_partnum(device);
            if (r < 0) {
                throw new UbertoothException(r);
            } else {
                return String.format("%02x", r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    // Debug Info
    public boolean isInitialized() {
        return initialized;
    }

    public void printLibraryVersion() throws UbertoothException {
        if (initialized) {
            ubertooth.print_version();
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_INITIALIZED.getCode());
        }
    }

    public void setTxLed(int state) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_txled(device, state);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setRxLed(int state) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_rxled(device, state);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setUsrLed(int state) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_usrled(device, state);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }
}