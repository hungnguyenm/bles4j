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
