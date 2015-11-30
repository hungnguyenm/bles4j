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

package edu.upenn.cis.precise.bles4j.bbtb;

import com.sun.jna.ptr.PointerByReference;

import edu.upenn.cis.precise.bles4j.bbtb.core.IBtbb;
import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class BlePacket {
    public interface LlidTypes {
        int RESERVED = 0;
        int LL_DATA_PDU_EMPTY_OR_L2CAP_CONTINUATION = 1;
        int LL_DATA_PDU_L2CAP_START = 2;
        int LL_CONTROL_PDU = 3;
    }

    public interface Opcodes {
        int LL_CONNECTION_UPDATE_REQ = 0;
        int LL_CHANNELL_MAP_REQ = 1;
        int LL_TERMINATE_IND = 2;
        int LL_ENC_REQ = 3;
        int LL_ENC_RSP = 4;
        int LL_START_ENC_REQ = 5;
        int LL_START_ENC_RSP = 6;
        int LL_UNKNOWN_RSP = 7;
        int LL_FEATURE_REQ = 8;
        int LL_FEATURE_RSP = 9;
        int LL_PAUSE_ENC_REQ = 10;
        int LL_PAUSE_ENC_RSP = 11;
        int LL_VERSION_IND = 12;
        int LL_REJECT_IND = 13;
        int LL_SLAVE_FEATURE_REQ = 14;
        int LL_CONNECTION_PARAM_REQ = 15;
        int LL_CONNECTION_PARAM_RSP = 16;
        int LL_REJECT_IND_EXT = 17;
        int LL_PING_REQ = 18;
        int LL_PING_RSP = 19;
        int RESERVED = 20;
    }

    public interface AdvertisingTypes {
        short ADV_IND = 0;
        short ADV_DIRECT_IND = 1;
        short ADV_NONCONN_IND = 2;
        short SCAN_REQ = 3;
        short SCAN_RSP = 4;
        short CONNECT_REQ = 5;
        short ADV_SCAN_IND = 6;
    }

    public int accessAddress;
    public boolean isAccessAddressValid;
    public int dataLength;
    public int channelIndex;
    public int channelFrequency;
    public boolean isDataPacket;
    // Below fields are only populated if isDataPacket is true
    public int opcode;
    // Below fields are only populated if isDataPacket is false
    public int advertisingType;
    public byte[] advertisingAddress;
    public boolean isAdvertisingTxAddressRandom;
    public byte[] advertisingData;

    public byte[] initiatingAddress;
    public boolean isAdvertisingRxAddressRandom;
    public byte[] newAccessAddress;
    public byte[] CRCInit;
    public byte winSize;
    public byte[] winOffset;
    public byte[] interval;
    public byte[] latency;
    public byte[] timeout;
    public int hop;
    public int sca;

    // Payload
    public byte[] data;
    public byte[] crc;

    /**
     * Packet constructor
     *
     * @param btbb        Libbtbb native library JNA loaded
     * @param usbPacketRx Packet from Ubertooth USB buffer
     */
    public BlePacket(IBtbb btbb, IUbertoothInterface.UsbPacketRx usbPacketRx) {
        // Call native parser first
        PointerByReference parsedPointer = new PointerByReference();
        btbb.lell_allocate_and_decode(usbPacketRx.data, (short) (usbPacketRx.channel + 2402),
                usbPacketRx.clk100ns, parsedPointer);
        IBtbb.LellPacket parsedPacket = new IBtbb.LellPacket(parsedPointer.getValue());

        // Start mapping
        this.accessAddress = parsedPacket.access_address;
        this.isAccessAddressValid = parsedPacket.flags.as_bits_access_address_ok != 0;
        this.dataLength = parsedPacket.length;
        this.channelIndex = parsedPacket.channel_idx;
        this.channelFrequency = parsedPacket.channel_k;
        this.isDataPacket = this.channelIndex < 37;

        if (this.isDataPacket) {
            int llid = parsedPacket.symbols[4] & 0x3;
            switch (llid) {
                case LlidTypes.LL_CONTROL_PDU:
                    opcode = parsedPacket.symbols[6];
                    break;
                default:
            }
        } else {
            // This is advertising packet
            this.advertisingType = parsedPacket.adv_type;
            switch (this.advertisingType) {
                case AdvertisingTypes.ADV_IND:
                    this.advertisingAddress = getBytesFromStream(parsedPacket.symbols, 6, 6);
                    this.isAdvertisingTxAddressRandom = parsedPacket.adv_tx_add != 0;
                    this.advertisingData = new byte[parsedPacket.length - 6];
                    System.arraycopy(parsedPacket.symbols, 12, this.advertisingData, 0, parsedPacket.length - 6);
                    break;
                case AdvertisingTypes.SCAN_REQ:
                    // TODO: implemented SCAN_REQ parser
                    break;
                case AdvertisingTypes.SCAN_RSP:
                    // TODO: implemented SCAN_RSP parser
                    break;
                case AdvertisingTypes.CONNECT_REQ:
                    this.initiatingAddress = getBytesFromStream(parsedPacket.symbols, 6, 6);
                    this.isAdvertisingTxAddressRandom = parsedPacket.adv_tx_add != 0;
                    this.advertisingAddress = getBytesFromStream(parsedPacket.symbols, 12, 6);
                    this.isAdvertisingRxAddressRandom = parsedPacket.adv_rx_add != 0;
                    this.newAccessAddress = getBytesFromStream(parsedPacket.symbols, 18, 4);
                    this.CRCInit = getBytesFromStream(parsedPacket.symbols, 22, 3);
                    this.winSize = parsedPacket.symbols[25];
                    this.winOffset = getBytesFromStream(parsedPacket.symbols, 26, 2);
                    this.interval = getBytesFromStream(parsedPacket.symbols, 28, 2);
                    this.latency = getBytesFromStream(parsedPacket.symbols, 30, 2);
                    this.timeout = getBytesFromStream(parsedPacket.symbols, 32, 2);
                    this.hop = parsedPacket.symbols[39] & 0x1f;
                    // this.sca = ...
                    break;
                default:
            }
        }

        this.data = new byte[parsedPacket.length];
        System.arraycopy(parsedPacket.symbols, 6, this.data, 0, parsedPacket.length);
        this.crc = new byte[3];
        System.arraycopy(parsedPacket.symbols, 6 + parsedPacket.length, this.crc, 0, 3);
    }

    public String getOpcode() {
        if (isDataPacket) {
            String[] Opcodes = {
                    "LL_CONNECTION_UPDATE_REQ",
                    "LL_CHANNEL_MAP_REQ",
                    "LL_TERMINATE_IND",
                    "LL_ENC_REQ",
                    "LL_ENC_RSP",
                    "LL_START_ENC_REQ",
                    "LL_START_ENC_RSP",
                    "LL_UNKNOWN_RSP",
                    "LL_FEATURE_REQ",
                    "LL_FEATURE_RSP",
                    "LL_PAUSE_ENC_REQ",
                    "LL_PAUSE_ENC_RSP",
                    "LL_VERSION_IND",
                    "LL_REJECT_IND",
                    "LL_SLAVE_FEATURE_REQ",
                    "LL_CONNECTION_PARAM_REQ",
                    "LL_CONNECTION_PARAM_RSP",
                    "LL_REJECT_IND_EXT",
                    "LL_PING_REQ",
                    "LL_PING_RSP",
                    "Reserved for Future Use"};
            if (opcode < Opcodes.length && opcode >= 0) {
                return Opcodes[opcode];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getAdvertisingType() {
        if (!isDataPacket) {
            String[] types = {
                    "ADV_IND",
                    "ADV_DIRECT_IND",
                    "ADV_NONCONN_IND",
                    "SCAN_REQ",
                    "SCAN_RSP",
                    "CONNECT_REQ",
                    "ADV_SCAN_IND"};
            if (advertisingType < types.length && advertisingType >= 0) {
                return types[advertisingType];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String printChannelIndex() {
        return Integer.toString(channelIndex);
    }

    public String printAdvertisingAddress() {
        return bytesToHex(advertisingAddress);
    }

    public String printInitiatingAddress() {
        return bytesToHex(initiatingAddress);
    }

    public String printNewAccessAddress() {
        return bytesToHex(newAccessAddress);
    }

    public String printCRCInit() {
        return bytesToHex(CRCInit);
    }

    public String printInterval() {
        return Double.toString(bytesToDouble(interval) * 1.25) + "ms";
    }

    public String printLatency() {
        return bytesToHex(latency);
    }

    public String printHop() {
        return Integer.toString(hop);
    }

    public String printTimeout() {
        return bytesToHex(timeout);
    }

    private byte[] getBytesFromStream(byte[] stream, int offset, int length) {
        byte[] addr = new byte[length];
        for (int i = 0; i < length; i++) {
            addr[i] = stream[offset + length - i - 1];
        }
        return addr;
    }

    @Override
    public String toString() {
        return "BlePacket{" +
                "accessAddress=" + getUnsignedString(accessAddress) +
                ", isAccessAddressValid=" + isAccessAddressValid +
                ", dataLength=" + dataLength +
                ", channelIndex=" + channelIndex +
                ", channelFrequency=" + channelFrequency +
                ", isDataPacket=" + isDataPacket +
                ", opcode=" + getOpcode() +
                ", advertisingType=" + getAdvertisingType() +
                ", advertisingAddress=" + bytesToHex(advertisingAddress) +
                ", isAdvertisingTxAddressRandom=" + isAdvertisingTxAddressRandom +
                ", advertisingData=" + bytesToHex(advertisingData) +
                ", initiatingAddress=" + bytesToHex(initiatingAddress) +
                ", isAdvertisingRxAddressRandom=" + isAdvertisingRxAddressRandom +
                ", newAccessAddress=" + bytesToHex(newAccessAddress) +
                ", CRCInit=" + bytesToHex(CRCInit) +
                ", winSize=" + winSize +
                ", winOffset=" + bytesToHex(winOffset) +
                ", interval=" + bytesToHex(interval) +
                ", latency=" + bytesToHex(latency) +
                ", timeout=" + bytesToHex(timeout) +
                ", hop=" + hop +
                ", sca=" + sca +
                ", data=" + bytesToHex(data) +
                ", crc=" + bytesToHex(crc) +
                '}';
    }

    private static String bytesToHex(byte[] bytes) {
        if (bytes != null) {
            char[] hexArray = "0123456789ABCDEF ".toCharArray();
            char[] hexChars = new char[bytes.length * 3];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 3] = hexArray[v >>> 4];
                hexChars[j * 3 + 1] = hexArray[v & 0x0F];
                hexChars[j * 3 + 2] = hexArray[16];
            }
            return new String(hexChars);
        } else {
            return "null";
        }
    }

    private static String getUnsignedString(int x) {
        return Long.toHexString(x & 0x00000000ffffffffL);
    }

    private static double bytesToDouble(byte[] x) {
        long value = 0;
        for (int i : x) {
            value = (value << 8) + (i & 0xff);
        }
        return value;
    }
}
