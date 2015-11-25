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

package edu.upenn.cis.precise.bles4j.ubertooth;

import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface.*;
import edu.upenn.cis.precise.bles4j.ubertooth.devices.UbertoothOne;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothExceptionCode;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class BtleSniffer {
    public static int MAX_PACKET_STORE = 1000;        // Maximum number of unprocessed packets stored in queue
    public static int POLLING_INTERVAL = 50;           // Periodic time to check Ubertooth packet buffer

    public enum SniffMode {
        FOLLOW, PROMISCUOUS
    }

    private ConcurrentLinkedDeque<UsbPacketRx> packets;
    private AtomicBoolean queueFull;
    private AtomicBoolean running;
    private Thread sniffer;

    public BtleSniffer() {
        packets = new ConcurrentLinkedDeque<UsbPacketRx>();
        running = new AtomicBoolean(false);
        queueFull = new AtomicBoolean(false);
    }

    // TODO: expand simple start/stop functions
    public void start() {
        running.set(true);
        sniffer = new Thread(new Sniffer(packets, running, queueFull, SniffMode.FOLLOW, JamModes.JAM_NONE, 37));
        sniffer.start();
    }

    public void stop() {
        if (sniffer.isAlive()) {
            try {
                sniffer.interrupt();
                sniffer.join();
            } catch (InterruptedException e) {
                // Interrupted, consider sniffer is stopped anyway
                running.set(false);
            }
        }
    }

    public UsbPacketRx pollPacket() {
        return packets.poll();
    }

    // Info
    public boolean isRunning() {
        return running.get();
    }

    public boolean isQueueFull() {
        return queueFull.get();
    }
}

/**
 * Thread to directly initiate Ubertooth to sniff mode and keep polling available packets
 * to update in main thread.
 */
class Sniffer implements Runnable {

    private class PollResult {
        private boolean success;
        private int error;
        private UsbPacketRx packet;

        public PollResult() {
            success = false;
            error = 0;
            packet = new UsbPacketRx.ByReference();
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getError() {
            return error;
        }

        public void setError(int error) {
            this.error = error;
        }

        public UsbPacketRx getPacket() {
            return packet;
        }

        public void setPacket(UsbPacketRx packet) {
            this.packet = packet;
        }
    }

    private ConcurrentLinkedDeque<UsbPacketRx> packets;
    private AtomicBoolean queueFull;
    private AtomicBoolean running;
    private UbertoothOne ubertoothOne;
    private BtleSniffer.SniffMode sniffMode;
    private short jamMode;
    private int advIndex;

    public Sniffer(ConcurrentLinkedDeque<UsbPacketRx> packets, AtomicBoolean running, AtomicBoolean queueFull,
                   BtleSniffer.SniffMode sniffMode, short jamMode, int advIndex) {
        this.packets = packets;
        this.running = running;
        this.queueFull = queueFull;
        this.sniffMode = sniffMode;
        this.jamMode = jamMode;
        this.advIndex = advIndex;
    }

    public void run() {
        try {
            ubertoothOne = new UbertoothOne();

            if (ubertoothOne.isPingable()) {
                // 1. Configure Jam mode
                ubertoothOne.setJamMode(jamMode);

                // 2. Configure modulation to BTLE
                ubertoothOne.setModulation(Modulations.MOD_BT_LOW_ENERGY);

                // 3. Start sniffing
                if (sniffMode == BtleSniffer.SniffMode.FOLLOW) {
                    short channel = 0;
                    switch (advIndex) {
                        case 37:
                            channel = 2402;
                            break;
                        case 38:
                            channel = 2426;
                            break;
                        case 39:
                            channel = 2480;
                            break;
                        default:
                            throw new UbertoothException(
                                    UbertoothExceptionCode.UBERTOOTH_ERROR_UNKNOWN_ADVERTISING_CHANNEL.getCode());
                    }
                    ubertoothOne.setChannel(channel);
                    ubertoothOne.btleSniffing((short) 2);
                } else if (sniffMode == BtleSniffer.SniffMode.PROMISCUOUS) {
                    ubertoothOne.btlePromisc();
                }

                // Now keep monitoring Ubertooth for incoming packets
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(BtleSniffer.POLLING_INTERVAL);
                    PollResult result = poll();
                    if (result.isSuccess()) {
                        packets.add(result.getPacket());
                        if (packets.size() == BtleSniffer.MAX_PACKET_STORE) {
                            // Queue is full
                            queueFull.set(true);
                            throw new UbertoothException(
                                    UbertoothExceptionCode.UBERTOOTH_ERROR_UNPROCESSED_PACKET_STACK_OVERFLOW.getCode());
                        }
                    } else if (result.getError() != 0) {
                        throw new UbertoothException(result.error);
                    }
                }
            }
        } catch (UbertoothException e) {
            System.err.println("SNIFFER ERROR: " + e.getMessage());
            exit();
        } catch (InterruptedException e) {
            exit();
        }
    }

    private void exit() {
        running.set(false);
        if (ubertoothOne != null) {
            if (ubertoothOne.isPingable()) {
                ubertoothOne.stop();
                ubertoothOne.reset();
            }
        }
    }

    private PollResult poll() throws UbertoothException {
        PollResult result = new PollResult();

        try {
            UsbPacketRx.ByReference usbPacketRx = ubertoothOne.poll();
            if (usbPacketRx.isLePacket()) {
                result.setSuccess(true);
                result.setPacket(usbPacketRx);
            }
        } catch (UbertoothException e) {
            result.setSuccess(false);
            result.setError(e.getError());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(Integer.MIN_VALUE);
        }

        return result;
    }
}