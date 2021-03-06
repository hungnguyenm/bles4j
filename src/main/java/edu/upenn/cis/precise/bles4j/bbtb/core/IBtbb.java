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

package edu.upenn.cis.precise.bles4j.bbtb.core;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public interface IBtbb extends Library {
    //region Configuration
    int MAX_LE_SYMBOLS = 64;
    int LE_ADV_AA = 0x8e89bed6;

    class LellPacket extends Structure {
        public static class ByReference extends LellPacket implements Structure.ByReference {
        }

        public static class ByValue extends LellPacket implements Structure.ByValue {
        }

        // Raw un-whitened bytes of packet including access address
        public byte[] symbols = new byte[MAX_LE_SYMBOLS];
        public int access_address;

        // Channel index
        public byte channel_idx;
        public byte channel_k;

        // Number of symbols
        public int length;
        public int clk100ns;

        // Advertising packet header info
        public byte adv_type;
        public int adv_tx_add;
        public int adv_rx_add;

        public int access_address_offenses;
        public int refcount;

        // Flags
        public Flag flags;

        public LellPacket() {
            super();
        }

        public LellPacket(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("symbols", "access_address", "channel_idx", "channel_k",
                    "length", "clk100ns", "adv_type", "adv_tx_add", "adv_rx_add",
                    "access_address_offenses", "refcount", "flags");
        }
    }

    class Flag extends Union {
        public byte as_bits_access_address_ok;
        public int as_word;
    }
    //endregion

    //region Main functions
    /**
     * Decode stream data to LE Packet
     * @param stream Stream data in bytes
     * @param phys_channel Physical channel
     * @param clk100ns Clock
     * @param pkt Decoded LE Packet
     */
    void lell_allocate_and_decode(byte[] stream, short phys_channel, int clk100ns, PointerByReference pkt);

    int lell_packet_is_data(PointerByReference pkt);

    int lell_get_access_address(PointerByReference pkt);

    int lell_get_access_address_offenses(PointerByReference pkt);

    int lell_get_channel_index(PointerByReference pkt);

    int lell_get_channel_k(PointerByReference pkt);
    //endregion

    //region Helper functions
    /**
     * Helper function for filtering bogus packets on data channels based on
     * Access Address statistics
     * @param aa Access Address
     * @return ?
     */
    int aa_data_channel_offenses(int aa);

    /**
     * Map channel index from physical channel
     * @param phys_channel Physical channel
     * @return Channel index
     */
    byte le_channel_index(short phys_channel);

    /**
     * Console output packet details
     * @param pkt LE Packet
     */
    void lell_print(PointerByReference pkt);
    //endregion
}
