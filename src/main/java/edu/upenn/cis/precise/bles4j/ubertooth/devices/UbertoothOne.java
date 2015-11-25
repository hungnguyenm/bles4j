/*
 * This file is part of Bluetooth Low Energy Sniffer for Java (BLES4J).
 *
 *     BLES4J is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BLES4J is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BLES4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.upenn.cis.precise.bles4j.ubertooth.devices;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import edu.upenn.cis.precise.bles4j.ubertooth.core.*;
import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface.*;
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
        // Load dynamic library via JNA
        ubertooth = (IUbertooth) Native.loadLibrary("ubertooth", IUbertooth.class);
        ubertoothControl = (IUbertoothControl) Native.loadLibrary("ubertooth",
                IUbertoothControl.class);
        initialized = true;

        // Attempt to initialize Ubertooth
        device = ubertooth.ubertooth_start(-1);
        if (device != null) {
            connected = true;
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NO_DEVICE.getCode());
        }
    }

    // Device Control
    /**
     * Stop current Ubertooth operation
     */
    public void stop() {
        if (connected) {
            ubertoothControl.cmd_stop(device);
        }
    }

    /**
     * Full reset Ubertooth
     */
    public void reset() {
        if (connected) {
            connected = false;
            ubertoothControl.cmd_reset(device);
        }
    }

    /**
     * Full reset Ubertooth, wait for 5 seconds and attempt to init again.
     * @throws UbertoothException
     */
    public void resetThenReconnect() throws UbertoothException {
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

    /**
     * Clear all Ubertooth registers to default values
     */
    public void cleanUp() {
        if (connected) {
            ubertooth.register_cleanup_handler(device);
        }
    }

    /**
     * Retrieve one packet from Ubertooth USB buffer
     * @return USB packet data
     * @throws UbertoothException
     */
    public UsbPacketRx.ByReference poll() throws UbertoothException {
        UsbPacketRx.ByReference p = new UsbPacketRx.ByReference();
        if (connected) {
            int r = ubertoothControl.cmd_poll(device, p);
            if (r < 0) {
                throw new UbertoothException(r);
            } else {
                return p;
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }


    /**
     * Activate following connection mode
     * @param num Number of connections to follow
     * @throws UbertoothException
     */
    public void btleSniffing(short num) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_btle_sniffing(device, num);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    /**
     * Activate promiscuous mode ~ sniff all active connections
     * @throws UbertoothException
     */
    public void btlePromisc() throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_btle_promisc(device);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void analyzeSpectrum(short lowFrequency, short highFrequency) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_specan(device, lowFrequency, highFrequency);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void analyzeSpectrumLed(short rssi_threshold) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_led_specan(device, rssi_threshold);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void activateRepeater() throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_repeater(device);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    // Device set configuration
    /**
     * Set Tx LED state
     * @param state 0-off 1-on
     * @throws UbertoothException
     */
    public void setTxLed(short state) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_txled(device, state);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    /**
     * Set Rx LED state
     * @param state 0-off 1-on
     * @throws UbertoothException
     */
    public void setRxLed(short state) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_rxled(device, state);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    /**
     * Set USR LED state
     * @param state 0-off 1-on
     * @throws UbertoothException
     */
    public void setUsrLed(short state) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_usrled(device, state);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    /**
     * Set Modulation mode
     * @param mod 0-Bluetooth Basic Rate 1-Bluetooth LE 2-80211 FHSS
     * @throws UbertoothException
     */
    public void setModulation(short mod) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_modulation(device, mod);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setChannel(short channel) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_channel(device, channel);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setCrcVerify(boolean mode) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_crc_verify(device, mode);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    /**
     * Set Jam mode
     * @param mode 0-None 1-Once 2-Continuous
     * @throws UbertoothException
     */
    public void setJamMode(short mode) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_jam_mode(device, mode);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setClock(int clock) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_clock(device, clock);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setAccessAddress(int address) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_access_address(device, address);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setPaLevel(short level) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_palevel(device, level);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setSquelchLevel(short level) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_squelch(device, level);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setPaen(short paen) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_paen(device, paen);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void setHgm(short hgm) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_set_hgm(device, hgm);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    // Device get configuration
    // TODO: Implement get functions

    // Ubertooth Utilities
    public boolean isConnected() {
        return connected;
    }

    public String getBoardId() throws UbertoothException {
        if (connected) {
            switch (ubertoothControl.cmd_get_board_id(device)) {
                case 1:
                    return "Ubertooth Zero";
                case 2:
                    return "Ubertooth One";
                case 3:
                    return "ToorCon 13 Badge";
                default:
                    return "Unknown";
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
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

    public String getCompileInfo() throws UbertoothException {
        if (connected) {
            byte[] info = new byte[255];
            ubertoothControl.cmd_get_compile_info(device, info, 255);
            return Native.toString(info);
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

    public void activateIsp() throws UbertoothException {
        // ISP ~ In-System Programming mode
        if (connected) {
            int r = ubertoothControl.cmd_set_isp(device);
            if (r < 0) {
                throw new UbertoothException(r);
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }

    public void activateDfu() throws UbertoothException {
        // Flash Programming (DFU) mode
        if (connected) {
            int r = ubertoothControl.cmd_flash(device);
            if (r < 0) {
                throw new UbertoothException(r);
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

    public boolean isPingable() {
        return connected && ubertoothControl.cmd_ping(device) == 0;
    }

    public boolean passTxTest() {
        // Continuous transmit test
        return connected && ubertoothControl.cmd_tx_test(device) == 0;
    }

    public int readRegister(byte register) throws UbertoothException {
        if (connected) {
            int r = ubertoothControl.cmd_read_register(device, register);
            if (r < 0) {
                throw new UbertoothException(r);
            } else {
                return r;
            }
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_NOT_CONNECTED.getCode());
        }
    }
}