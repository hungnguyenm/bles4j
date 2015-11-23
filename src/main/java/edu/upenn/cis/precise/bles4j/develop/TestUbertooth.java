package edu.upenn.cis.precise.bles4j.develop;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class TestUbertooth {
    public interface Ubertooth extends Library {
        void print_version();
        Object ubertooth_start(int ubertooth_device);
    }

    public interface UbertoothControl extends Library {

    }

    static public void main(String argv[]) {
        Ubertooth ubertooth = (Ubertooth)Native.loadLibrary("ubertooth", Ubertooth.class);
        UbertoothControl ubertoothControl = (UbertoothControl)Native.loadLibrary("ubertooth",
                UbertoothControl.class);

        // Print libubertooth version
        ubertooth.print_version();

        // Init Ubertooth device
        Object devUbertooth = ubertooth.ubertooth_start(-1);
    }
}
