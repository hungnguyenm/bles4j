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

package edu.upenn.cis.precise.bles4j.ubertooth.devices;

import edu.upenn.cis.precise.bles4j.ubertooth.exception.UbertoothException;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class UbertoothOneSelfTest {
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
        ubertoothOne.setUsrLed((short) 1);
        ubertoothOne.setTxLed((short) 1);
        ubertoothOne.setRxLed((short) 1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        ubertoothOne.setUsrLed((short) 0);
        ubertoothOne.setTxLed((short) 0);
        ubertoothOne.setRxLed((short) 0);
        System.out.println("Turn all LEDs OFF");

        // Clean
        System.out.println("Clean-up and reset...");
        ubertoothOne.cleanUp();
        ubertoothOne.resetThenReconnect();

        // Done
        System.out.println("Test completed!");
    }
}