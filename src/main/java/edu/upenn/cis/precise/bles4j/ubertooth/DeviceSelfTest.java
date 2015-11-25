package edu.upenn.cis.precise.bles4j.ubertooth;

import edu.upenn.cis.precise.bles4j.ubertooth.devices.UbertoothOne;
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

        // Print compile version
        System.out.println("Compile version: " + ubertoothOne.getCompileInfo());

        // Print serial number
        System.out.println("MCU Serial Number: " + ubertoothOne.getMcuSerialNumber());

        // Print part number
        System.out.println("MCU Part Number: " + ubertoothOne.getMcuPartNumber());

        // Cycle all LEDs
        System.out.println("Turn all LEDs ON");
        ubertoothOne.setUsrLed((short)1);
        ubertoothOne.setTxLed((short)1);
        ubertoothOne.setRxLed((short)1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        ubertoothOne.setUsrLed((short)0);
        ubertoothOne.setTxLed((short)0);
        ubertoothOne.setRxLed((short)0);
        System.out.println("Turn all LEDs OFF");

        // Clean
        System.out.println("Clean-up and reset...");
        ubertoothOne.cleanUp();
        ubertoothOne.resetThenReconnect();

        // Done
        System.out.println("Test completed!");
    }
}
