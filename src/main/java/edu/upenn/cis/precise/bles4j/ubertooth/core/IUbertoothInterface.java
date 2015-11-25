package edu.upenn.cis.precise.bles4j.ubertooth.core;

import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public interface IUbertoothInterface {
    interface UbertoothUsbCommands {
        short UBERTOOTH_PING = 0;
        short UBERTOOTH_RX_SYMBOLS = 1;
        short UBERTOOTH_TX_SYMBOLS = 2;
        short UBERTOOTH_GET_USRLED = 3;
        short UBERTOOTH_SET_USRLED = 4;
        short UBERTOOTH_GET_RXLED = 5;
        short UBERTOOTH_SET_RXLED = 6;
        short UBERTOOTH_GET_TXLED = 7;
        short UBERTOOTH_SET_TXLED = 8;
        short UBERTOOTH_GET_1V8 = 9;
        short UBERTOOTH_SET_1V8 = 10;
        short UBERTOOTH_GET_CHANNEL = 11;
        short UBERTOOTH_SET_CHANNEL = 12;
        short UBERTOOTH_RESET = 13;
        short UBERTOOTH_GET_SERIAL = 14;
        short UBERTOOTH_GET_PARTNUM = 15;
        short UBERTOOTH_GET_PAEN = 16;
        short UBERTOOTH_SET_PAEN = 17;
        short UBERTOOTH_GET_HGM = 18;
        short UBERTOOTH_SET_HGM = 19;
        short UBERTOOTH_TX_TEST = 20;
        short UBERTOOTH_STOP = 21;
        short UBERTOOTH_GET_MOD = 22;
        short UBERTOOTH_SET_MOD = 23;
        short UBERTOOTH_SET_ISP = 24;
        short UBERTOOTH_FLASH = 25;
        short BOOTLOADER_FLASH = 26;
        short UBERTOOTH_SPECAN = 27;
        short UBERTOOTH_GET_PALEVEL = 28;
        short UBERTOOTH_SET_PALEVEL = 29;
        short UBERTOOTH_REPEATER = 30;
        short UBERTOOTH_RANGE_TEST = 31;
        short UBERTOOTH_RANGE_CHECK = 32;
        short UBERTOOTH_GET_REV_NUM = 33;
        short UBERTOOTH_LED_SPECAN = 34;
        short UBERTOOTH_GET_BOARD_ID = 35;
        short UBERTOOTH_SET_SQUELCH = 36;
        short UBERTOOTH_GET_SQUELCH = 37;
        short UBERTOOTH_SET_BDADDR = 38;
        short UBERTOOTH_START_HOPPING = 39;
        short UBERTOOTH_SET_CLOCK = 40;
        short UBERTOOTH_GET_CLOCK = 41;
        short UBERTOOTH_BTLE_SNIFFING = 42;
        short UBERTOOTH_GET_ACCESS_ADDRESS = 43;
        short UBERTOOTH_SET_ACCESS_ADDRESS = 44;
        short UBERTOOTH_DO_SOMETHING = 45;
        short UBERTOOTH_DO_SOMETHING_REPLY = 46;
        short UBERTOOTH_GET_CRC_VERIFY = 47;
        short UBERTOOTH_SET_CRC_VERIFY = 48;
        short UBERTOOTH_POLL = 49;
        short UBERTOOTH_BTLE_PROMISC = 50;
        short UBERTOOTH_SET_AFHMAP = 51;
        short UBERTOOTH_CLEAR_AFHMAP = 52;
        short UBERTOOTH_READ_REGISTER = 53;
        short UBERTOOTH_BTLE_SLAVE = 54;
        short UBERTOOTH_GET_COMPILE_INFO = 55;
        short UBERTOOTH_BTLE_SET_TARGET = 56;
        short UBERTOOTH_BTLE_PHY = 57;
        short UBERTOOTH_WRITE_REGISTER = 58;
        short UBERTOOTH_JAM_MODE = 59;
        short UBERTOOTH_EGO = 60;
    }

    interface JamModes {
        short JAM_NONE = 0;
        short JAM_ONCE = 1;
        short JAM_CONTINUOUS = 2;
    }

    interface Modulations {
        short MOD_BT_BASIC_RATE = 0;
        short MOD_BT_LOW_ENERGY = 1;
        short MOD_80211_FHSS = 2;
    }

    interface UsbPktTypes {
        short BR_PACKET = 0;
        short LE_PACKET = 1;
        short MESSAGE = 2;
        short KEEP_ALIVE = 3;
        short SPECAN = 4;
        short LE_PROMISC = 5;
        short EGO_PACKET = 6;
    }

    class UsbPacketRx extends Structure {
        public static class ByReference extends UsbPacketRx implements Structure.ByReference {
        }

        public static class ByValue extends UsbPacketRx implements Structure.ByValue {
        }

        public byte pkt_type;
        public byte status;
        public byte channel;
        public byte clkn_high;
        public int clk100ns;
        public byte rssi_max;
        public byte rssi_min;
        public byte rssi_avg;
        public byte rssi_count;
        public byte[] reserved = new byte[2];
        public byte[] data = new byte[50];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("pkt_type", "status", "channel", "clkn_high", "clk100ns",
                    "rssi_max", "rssi_min", "rssi_avg", "rssi_count", "reserved", "data");
        }

        public boolean isLePacket() {
            return pkt_type == UsbPktTypes.LE_PACKET;
        }

        public String printData() {
            return bytesToHex(data);
        }

        private static String bytesToHex(byte[] bytes) {
            char[] hexArray = "0123456789ABCDEF ".toCharArray();
            char[] hexChars = new char[bytes.length * 3];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 3] = hexArray[v >>> 4];
                hexChars[j * 3 + 1] = hexArray[v & 0x0F];
                hexChars[j * 3 + 2] = hexArray[16];
            }
            return new String(hexChars);
        }
    }

    class BroadcastAddress extends Structure {
        public static class ByReference extends BroadcastAddress implements Structure.ByReference {
        }

        public static class ByValue extends BroadcastAddress implements Structure.ByValue {
        }

        public long address;
        public long access_code;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("address", "access_code");
        }
    }

    class RangeTestResult extends Structure {
        public static class ByReference extends RangeTestResult implements Structure.ByReference {
        }

        public static class ByValue extends RangeTestResult implements Structure.ByValue {
        }

        public byte valid;
        public byte request_pa;
        public byte request_num;
        public byte reply_pa;
        public byte reply_num;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("valid", "request_pa", "request_num", "reply_pa", "reply_num");
        }

        public int length() {
            return 64;
        }
    }
}