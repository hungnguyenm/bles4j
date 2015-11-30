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

package edu.upenn.cis.precise.bles4j;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.upenn.cis.precise.bles4j.ubertooth.UbertoothOne;
import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface;
import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface.*;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothExceptionCode;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class BtleSniffer {
    public static int MAX_PACKET_STORE = 1000;        // Maximum number of unprocessed packets stored in queue
    public static int POLLING_INTERVAL = 10;          // Periodic time to check Ubertooth packet buffer

    public enum SniffMode {
        FOLLOW, PROMISCUOUS
    }

    /**
     * Sniffer write mode:
     * - STACK: Add raw packet data to shared dequeue variable
     * - PIPE: Decode to pcap format and write to PCAP pipe file
     */
    public enum WriteMode {
        STACK, PIPE
    }

    private ConcurrentLinkedDeque<UsbPacketRx> packets;
    private AtomicBoolean queueFull;        // Thread interrupt hard stop
    private AtomicBoolean running;          // Thread interlock
    private Thread sniffer;

    public BtleSniffer() {
        packets = new ConcurrentLinkedDeque<UsbPacketRx>();
        running = new AtomicBoolean(false);
        queueFull = new AtomicBoolean(false);
    }

    // TODO: expand simple start/stop functions
    /**
     * Initiate Ubertooth in following mode
     *
     * @param writeMode Write-back mode from USB buffer
     */
    public void startFollow(WriteMode writeMode) {
        running.set(true);
        sniffer = new Thread(new Sniffer(packets, running, queueFull, writeMode,
                SniffMode.FOLLOW, (short)0, JamModes.JAM_NONE, 37));
        sniffer.start();
    }

    /**
     * Initiate Ubertooth in promiscuous mode
     *
     * @param writeMode Write-back mode from USB buffer
     * @param channel   Channel frequency to sniff on
     */
    public void startPromiscuous(WriteMode writeMode, short channel) {
        running.set(true);
        sniffer = new Thread(new Sniffer(packets, running, queueFull, writeMode,
                SniffMode.PROMISCUOUS, channel, JamModes.JAM_NONE, 37));
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
    private BtleSniffer.WriteMode writeMode;
    private BtleSniffer.SniffMode sniffMode;
    private short channel;
    private short jamMode;
    private int advIndex;
    private IUbertoothInterface.BtleOptions btleOptions;

    public Sniffer(ConcurrentLinkedDeque<UsbPacketRx> packets, AtomicBoolean running, AtomicBoolean queueFull,
                   BtleSniffer.WriteMode writeMode, BtleSniffer.SniffMode sniffMode,
                   short channel, short jamMode, int advIndex) {
        this.packets = packets;
        this.running = running;
        this.queueFull = queueFull;
        this.sniffMode = sniffMode;
        this.channel = channel;
        this.jamMode = jamMode;
        this.advIndex = advIndex;
        initializeExport(writeMode);
    }

    public void run() {
        try {
            ubertoothOne = new UbertoothOne();

            if (ubertoothOne.isPingable()) {
                // 1. Configure Jam mode
                ubertoothOne.setJamMode(jamMode);

                // 2. Configure modulation to BTLE
                ubertoothOne.setModulation(Modulations.MOD_BT_LOW_ENERGY);

                // 3. If it is promiscuous mode then configure channel and squelch
                if (sniffMode == BtleSniffer.SniffMode.PROMISCUOUS) {
                    ubertoothOne.setChannel(channel);
                }

                // 4. Start sniffing
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
                        export(result.getPacket());
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

    /**
     * Prepare for writing packets if needed
     */
    private void initializeExport(BtleSniffer.WriteMode writeMode) {
        this.writeMode = writeMode;

        if (writeMode == BtleSniffer.WriteMode.PIPE) {
            this.btleOptions = new BtleOptions();
            btleOptions.allowed_access_address_errors = 32;
        }
    }

    /**
     * Write captured packet to target set by Write mode
     */
    private void export(UsbPacketRx packet) throws UbertoothException {
        if (writeMode == BtleSniffer.WriteMode.STACK) {
            // Add to stack
            packets.add(packet);
            if (packets.size() == BtleSniffer.MAX_PACKET_STORE) {
                // Queue is full
                queueFull.set(true);
                throw new UbertoothException(
                        UbertoothExceptionCode.UBERTOOTH_ERROR_UNPROCESSED_PACKET_STACK_OVERFLOW.getCode());
            }
        } else if (writeMode == BtleSniffer.WriteMode.PIPE) {
            // Decode to pcap format and write to pipe file
            // TODO: implement pipe
        } else {
            throw new UbertoothException(UbertoothExceptionCode.UBERTOOTH_ERROR_EXPORT_NOT_INITIALIZED.getCode());
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
            if (usbPacketRx.hasData()) {
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