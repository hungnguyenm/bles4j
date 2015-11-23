package edu.upenn.cis.precise.bles4j.develop;

import edu.upenn.cis.precise.bles4j.ubertooth.UbertoothOne;
import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

import java.io.IOException;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class TestUbertooth {
    static public void main(String argv[]) throws UbertoothException{
        UbertoothOne ubertoothOne = new UbertoothOne();

        System.out.print("Firmware version: " + ubertoothOne.getFirmwareRevision());
    }
}
