package edu.upenn.cis.precise.bles4j.ubertooth;

import edu.upenn.cis.precise.bles4j.ubertooth.devices.UbertoothOne;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class BtleSniffer {
    private static enum SniffMode {FOLLOW, PROMISCUOUS}

    private UbertoothOne ubertoothOne;
    private boolean initialized;

    public BtleSniffer() {
        initialized = false;
    }

    public void initialize() throws UbertoothException {
        ubertoothOne = new UbertoothOne();
        initialized = true;
    }

    // Internal sniffing handler
    private void start(SniffMode mode, int advIndex) {

    }

    // Info
    public boolean isInitialized() {
        return initialized;
    }
}
