package edu.upenn.cis.precise.bles4j.ubertooth.core;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public interface IUbertoothInterface {
    interface UbertoothUsbCommands {
        int UBERTOOTH_PING = 0;
        int UBERTOOTH_RX_SYMBOLS = 1;
        int UBERTOOTH_TX_SYMBOLS = 2;
        int UBERTOOTH_GET_USRLED = 3;
        int UBERTOOTH_SET_USRLED = 4;
        int UBERTOOTH_GET_RXLED = 5;
        int UBERTOOTH_SET_RXLED = 6;
        int UBERTOOTH_GET_TXLED = 7;
        int UBERTOOTH_SET_TXLED = 8;
        int UBERTOOTH_GET_1V8 = 9;
        int UBERTOOTH_SET_1V8 = 10;
        int UBERTOOTH_GET_CHANNEL = 11;
        int UBERTOOTH_SET_CHANNEL = 12;
        int UBERTOOTH_RESET = 13;
        int UBERTOOTH_GET_SERIAL = 14;
        int UBERTOOTH_GET_PARTNUM = 15;
        int UBERTOOTH_GET_PAEN = 16;
        int UBERTOOTH_SET_PAEN = 17;
        int UBERTOOTH_GET_HGM = 18;
        int UBERTOOTH_SET_HGM = 19;
        int UBERTOOTH_TX_TEST = 20;
        int UBERTOOTH_STOP = 21;
        int UBERTOOTH_GET_MOD = 22;
        int UBERTOOTH_SET_MOD = 23;
        int UBERTOOTH_SET_ISP = 24;
        int UBERTOOTH_FLASH = 25;
        int BOOTLOADER_FLASH = 26;
        int UBERTOOTH_SPECAN = 27;
        int UBERTOOTH_GET_PALEVEL = 28;
        int UBERTOOTH_SET_PALEVEL = 29;
        int UBERTOOTH_REPEATER = 30;
        int UBERTOOTH_RANGE_TEST = 31;
        int UBERTOOTH_RANGE_CHECK = 32;
        int UBERTOOTH_GET_REV_NUM = 33;
        int UBERTOOTH_LED_SPECAN = 34;
        int UBERTOOTH_GET_BOARD_ID = 35;
        int UBERTOOTH_SET_SQUELCH = 36;
        int UBERTOOTH_GET_SQUELCH = 37;
        int UBERTOOTH_SET_BDADDR = 38;
        int UBERTOOTH_START_HOPPING = 39;
        int UBERTOOTH_SET_CLOCK = 40;
        int UBERTOOTH_GET_CLOCK = 41;
        int UBERTOOTH_BTLE_SNIFFING = 42;
        int UBERTOOTH_GET_ACCESS_ADDRESS = 43;
        int UBERTOOTH_SET_ACCESS_ADDRESS = 44;
        int UBERTOOTH_DO_SOMETHING = 45;
        int UBERTOOTH_DO_SOMETHING_REPLY = 46;
        int UBERTOOTH_GET_CRC_VERIFY = 47;
        int UBERTOOTH_SET_CRC_VERIFY = 48;
        int UBERTOOTH_POLL = 49;
        int UBERTOOTH_BTLE_PROMISC = 50;
        int UBERTOOTH_SET_AFHMAP = 51;
        int UBERTOOTH_CLEAR_AFHMAP = 52;
        int UBERTOOTH_READ_REGISTER = 53;
        int UBERTOOTH_BTLE_SLAVE = 54;
        int UBERTOOTH_GET_COMPILE_INFO = 55;
        int UBERTOOTH_BTLE_SET_TARGET = 56;
        int UBERTOOTH_BTLE_PHY = 57;
        int UBERTOOTH_WRITE_REGISTER = 58;
        int UBERTOOTH_JAM_MODE = 59;
        int UBERTOOTH_EGO = 60;
    }

    interface JamModes {
        int JAM_NONE = 0;
        int JAM_ONCE = 1;
        int JAM_CONTINUOUS = 2;
    }

    interface Modulations {
        int MOD_BT_BASIC_RATE = 0;
        int MOD_BT_LOW_ENERGY = 1;
        int MOD_80211_FHSS = 2;
    }

    interface UsbPktTypes {
        int BR_PACKET = 0;
        int LE_PACKET = 1;
        int MESSAGE = 2;
        int KEEP_ALIVE = 3;
        int SPECAN = 4;
        int LE_PROMISC = 5;
        int EGO_PACKET = 6;
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
    }
}