package edu.upenn.cis.precise.bles4j.ubertooth.core;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public interface IUbertooth extends Library {
    void print_version();

    void register_cleanup_handler(Pointer devh);

    Pointer ubertooth_start(int ubertooth_device);

    void ubertooth_stop(Pointer devh);

    int specan(Pointer devh, int xfer_size, int low_freq,
               int high_freq, int output_mode);

    int cmd_ping(Pointer devh);

//    int stream_rx_usb(Pointer devh, int xfer_size,
//                      rx_callback cb, void*cb_args);
//
//    int stream_rx_file(Pointer fp, rx_callback cb, Pointer cb_args);

    void rx_live(Pointer devh, Pointer pn, int timeout);

    void rx_file(Pointer fp, Pointer pn);

    void rx_dump(Pointer devh, int full);

    void rx_btle(Pointer devh);

    void rx_btle_file(Pointer fp);

    void cb_btle(Pointer args, Pointer rx, int bank);

    void cb_ego(Pointer args, Pointer rx, int bank);
}
