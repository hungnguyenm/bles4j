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

import com.sun.jna.Native;

import edu.upenn.cis.precise.bles4j.BtleSniffer;
import edu.upenn.cis.precise.bles4j.bbtb.BlePacket;
import edu.upenn.cis.precise.bles4j.bbtb.core.IBtbb;
import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class RunInFollowMode implements Runnable {
    static BufferedReader keyin;
    static int quit = 0;

    public void run() {
        // Key-pressed interrupt
        String msg = null;
        while (true) {
            try {
                msg = keyin.readLine();
            } catch (Exception e) {
                System.err.println("ERROR in keyboard monitor thread: " + e.getMessage());
            }

            if (msg.equals("q")) {
                quit = 1;
                break;
            }
        }
    }

    public static void main(String[] args) throws UbertoothException {
        keyin = new BufferedReader(new InputStreamReader(System.in));
        BtleSniffer btleSniffer = new BtleSniffer();
        boolean isFollowing = false;
        byte[] targetAddressMask = {(byte) 0x00, (byte) 0x22, (byte) 0xd0};     // Polar H7 manufacturer mask
        // byte[] targetAddressMask = {(byte)0xc0, (byte)0x4b, (byte)0x72};     // Fitbit manufacturer mask

        System.out.println("==== BLES4J DEMO WITH POLAR H7 SENSOR ====");
        System.out.println("Configurations:");
        System.out.println(" - Filtered to target Polar devices (00:22:D0:xx:xx:xx)");
        System.out.println(" - Following mode: expect connect_req packet from master device");
        System.out.println(" - Press q then Enter to exit\n");
        // Load dynamic library via JNA
        System.out.println("Initializing...");
        IBtbb btbb = (IBtbb) Native.loadLibrary("btbb", IBtbb.class);
        // Start sniffing
        System.out.println("Start sniffing...");
        btleSniffer.startFollow(BtleSniffer.WriteMode.STACK);

        // Setup interrupt
        Thread keyPressedMonitor = new Thread(new RunInFollowMode());
        keyPressedMonitor.start();

        try {
            while (btleSniffer.isRunning()) {
                IUbertoothInterface.UsbPacketRx packet = btleSniffer.pollPacket();
                if (packet != null) {
                    BlePacket blePacket = new BlePacket(btbb, packet);
                    if (!isFollowing) {
                        if (!blePacket.isDataPacket) {
                            switch (blePacket.advertisingType) {
                                case BlePacket.AdvertisingTypes.ADV_IND:
                                    System.out.print("\r" + Helper.printTime() + " -- Advertising packet found from " +
                                            blePacket.printAdvertisingAddress() + "on channel " + blePacket.printChannelIndex());
                                    break;
                                case BlePacket.AdvertisingTypes.CONNECT_REQ:
                                    if (Arrays.equals(targetAddressMask,
                                            Arrays.copyOfRange(blePacket.advertisingAddress, 0, 3))) {
                                        System.out.println("\r" + Helper.printTime() + " -- Connection Request packet found:");
                                        System.out.println(" + Initiator Address: " + blePacket.printInitiatingAddress());
                                        System.out.println(" + Advertising Address: " + blePacket.printAdvertisingAddress());
                                        System.out.println(" + Access Address: " + blePacket.printNewAccessAddress());
                                        System.out.println(" + CRC Init: " + blePacket.printCRCInit());
                                        System.out.println(" + Hop Interval: " + blePacket.printInterval());
                                        System.out.println(" + Hop Increment: " + blePacket.printHop() + "\n");

                                        System.out.println("Start following...");
                                        isFollowing = true;
                                    }
                                    break;
                                default:
                            }
                        }
                    } else {
                        Helper.printHrData(blePacket);
                    }
                }

                // Handle quit interruption
                if (quit == 1) {
                    btleSniffer.stop();
                }
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        System.out.println("Sniffer stopped! Exiting...");
    }
}