package edu.upenn.cis.precise.bles4j.develop;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class TestPcap4J {
    public static final int COUNT = 1000;
    private static final String PCAP_FILE_KEY
            = TestPcap4J.class.getName() + ".pcapFile";
    private static final String PCAP_FILE
            = System.getProperty(PCAP_FILE_KEY, "src/main/resources/ltk_exchange.pcap");

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(PCAP_FILE, TimestampPrecision.NANO);
        } catch (PcapNativeException e) {
            handle = Pcaps.openOffline(PCAP_FILE);
        }

        for (int i = 0; i < COUNT; i++) {
            try {
                Packet packet = handle.getNextPacketEx();
                System.out.println(handle.getTimestamp());
                System.out.println(packet);
            } catch (TimeoutException e) {
            } catch (EOFException e) {
                System.out.println("EOF");
                break;
            }
        }

        handle.close();
    }
}