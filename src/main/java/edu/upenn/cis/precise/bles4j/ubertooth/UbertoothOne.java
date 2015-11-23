package edu.upenn.cis.precise.bles4j.ubertooth;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import edu.upenn.cis.precise.bles4j.ubertooth.core.*;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

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
            throw new UbertoothException(-2);
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
            throw new UbertoothException(-3);
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
            throw new UbertoothException(-1);
        }
    }
}
