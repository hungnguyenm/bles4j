package edu.upenn.cis.precise.bles4j.develop;

import edu.upenn.cis.precise.bles4j.ubertooth.BtleSniffer;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class TestBtleSniffer {
    public static void main(String[] args) throws UbertoothException {
        BtleSniffer btleSniffer = new BtleSniffer();

        System.out.println("Initializing...");
        btleSniffer.start();
    }
}
