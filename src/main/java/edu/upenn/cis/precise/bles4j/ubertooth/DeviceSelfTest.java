package edu.upenn.cis.precise.bles4j.ubertooth;

import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class DeviceSelfTest {
    static public void main(String argv[]) throws UbertoothException {
        UbertoothOne ubertoothOne = new UbertoothOne();

        // Start
        System.out.println("Ubertooth self-test...");

        // Print firmware version
        System.out.println("Firmware version: " + ubertoothOne.getFirmwareRevision());

        // Print serial number
        System.out.println("MCU Serial Number: " + ubertoothOne.getMcuSerialNumber());

        // Print part number
        System.out.println("MCU Part Number: " + ubertoothOne.getMcuPartNumber());

        // Cycle all LEDs
        System.out.println("Turn all LEDs ON");
        ubertoothOne.setUsrLed(1);
        ubertoothOne.setTxLed(1);
        ubertoothOne.setRxLed(1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        ubertoothOne.setUsrLed(0);
        ubertoothOne.setTxLed(0);
        ubertoothOne.setRxLed(0);
        System.out.println("Turn all LEDs OFF");

        // Clean
        System.out.println("Clean-up and reset...");
        ubertoothOne.cleanUp();
        ubertoothOne.reset();

        // Done
        System.out.println("Test completed!");
    }
}
