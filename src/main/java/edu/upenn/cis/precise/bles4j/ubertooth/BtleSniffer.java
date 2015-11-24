package edu.upenn.cis.precise.bles4j.ubertooth;

import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface.*;
import edu.upenn.cis.precise.bles4j.ubertooth.devices.UbertoothOne;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothExceptionCode;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class BtleSniffer {
    private enum SniffMode {FOLLOW, PROMISCUOUS}

    private UbertoothOne ubertoothOne;
    private boolean initialized;
    private short channel;

    public BtleSniffer() {
        initialized = false;
    }

    public void initialize() throws UbertoothException {
        ubertoothOne = new UbertoothOne();
        initialized = true;
        channel = 0;
    }

    public void reset() throws UbertoothException {
        ubertoothOne.stop();
        ubertoothOne.reset();
    }

    // Internal sniffing handler
    private void start(SniffMode sniffMode, short jamMode, int advIndex) throws UbertoothException {
        if (ubertoothOne.isPingable()) {
            // 1. Configure Jam mode
            ubertoothOne.setJamMode(jamMode);

            // 2. Configure modulation to BTLE
            ubertoothOne.setModulation(Modulations.MOD_BT_LOW_ENERGY);

            // 3. Start sniffing
            if (sniffMode == SniffMode.FOLLOW) {
                switch (advIndex) {
                    case 37:
                        channel = 2402;
                        break;
                    case 38:
                        channel = 2426;
                        break;
                    case 39:
                        channel = 2480;
                    default:
                        throw new UbertoothException(
                                UbertoothExceptionCode.UBERTOOTH_ERROR_UNKNOWN_ADVERTISING_CHANNEL.getCode());
                }
                ubertoothOne.setChannel(channel);
                ubertoothOne.btleSniffing((short)2);
            } else if (sniffMode == SniffMode.PROMISCUOUS) {
                ubertoothOne.btlePromisc();
            }
        }
    }

    // Info
    public boolean isInitialized() {
        return initialized;
    }
}
