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

package edu.upenn.cis.precise.bles4j.demo.cis556;

import edu.upenn.cis.precise.bles4j.bbtb.BlePacket;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class Helper {
    static byte[] targetGattData = {(byte) 0x04, (byte) 0x00, (byte) 0x1b}; // GATT Attribute Protocol with Opcode
    static String targetGattString = new String(targetGattData);

    public static void printHrData(BlePacket blePacket) {
        if (blePacket.isDataPacket && blePacket.dataLength > 0) {
            // System.out.println(blePacket.toString());
            String data = new String(blePacket.data);
            int index = data.indexOf(targetGattString);
            if (index >= 0 && blePacket.dataLength >= index + 6) {
                // Attribute protocol - Handle value notification packet
                index += 5;
                int flag = blePacket.data[index];
                switch (flag) {
                    case 0x16:
                        System.out.println(printTime() +
                                " -- Channel: " + Integer.toString(blePacket.channelIndex) +
                                " -- HR: " + Integer.toString(blePacket.data[index + 1]) + " bpm " +
                                "-- RR interval: " +
                                bytesToString(new byte[]{blePacket.data[index + 3], blePacket.data[index + 2]}) +
                                "ms");
                        break;
                    case 0x06:
                        System.out.println(printTime() +
                                " -- Channel: " + Integer.toString(blePacket.channelIndex) +
                                " -- HR: " + Integer.toString(blePacket.data[index + 1]) + " bpm");
                        break;
                }
            }
        }
    }

    public static String printTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static String bytesToString(byte[] x) {
        long value = 0;
        for (int i : x) {
            value = (value << 8) + (i & 0xff);
        }
        return Long.toString(value);
    }
}
