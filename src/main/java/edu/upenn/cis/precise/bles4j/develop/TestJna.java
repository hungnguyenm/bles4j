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

package edu.upenn.cis.precise.bles4j.develop;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class TestJna {
    public interface CTest extends Library {
        int add(int x, int y);
    }

    static public void main(String argv[]) {
        CTest ctest = (CTest) Native.loadLibrary("testjna", CTest.class);

        long q = 0;

        for (int y = 0; y < 2000; y++) {
            for (int x = 0; x < 1000; x++) {
                q += ctest.add(x, y);
                q %= 998;
            }
        }

        System.out.println("Total: " + q);
    }
}
