package edu.upenn.cis.precise.bles4j.ubertooth.core;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

import edu.upenn.cis.precise.bles4j.ubertooth.core.IUbertoothInterface.*;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public interface IUbertoothControl extends Library {

    int cmd_ping(Pointer devh);

    int cmd_rx_syms(Pointer devh);

    int cmd_specan(Pointer devh, int low_freq, int high_freq);

    int cmd_led_specan(Pointer devh, int rssi_threshold);

    int cmd_set_usrled(Pointer devh, short state);

    int cmd_get_usrled(Pointer devh);

    int cmd_set_rxled(Pointer devh, short state);

    int cmd_get_rxled(Pointer devh);

    int cmd_set_txled(Pointer devh, short state);

    int cmd_get_txled(Pointer devh);

    int cmd_get_partnum(Pointer devh);

    int cmd_get_serial(Pointer devh, byte[] serial);

    int cmd_set_modulation(Pointer devh, int mod);

    int cmd_get_modulation(Pointer devh);

    int cmd_set_isp(Pointer devh);

    int cmd_reset(Pointer devh);

    int cmd_stop(Pointer devh);

    int cmd_set_paen(Pointer devh, short state);

    int cmd_set_hgm(Pointer devh, short state);

    int cmd_tx_test(Pointer devh);

    int cmd_flash(Pointer devh);

    int cmd_get_palevel(Pointer devh);

    int cmd_set_palevel(Pointer devh, short level);

    int cmd_get_channel(Pointer devh);

    int cmd_set_channel(Pointer devh, int channel);

    int cmd_get_rangeresult(Pointer devh, byte[] rr);

    int cmd_range_test(Pointer devh);

    int cmd_repeater(Pointer devh);

    void cmd_get_rev_num(Pointer devh, byte[] version, int len);

    void cmd_get_compile_info(Pointer devh, byte[] compile_info, int len);

    int cmd_get_board_id(Pointer devh);

    int cmd_set_squelch(Pointer devh, short level);

    int cmd_get_squelch(Pointer devh);

    int cmd_set_bdaddr(Pointer devh, int bdaddr);

    int cmd_set_syncword(Pointer devh, int syncword);

    int cmd_next_hop(Pointer devh, int clk);

    int cmd_start_hopping(Pointer devh, int clock_offset);

    int cmd_set_clock(Pointer devh, int clkn);

    int cmd_get_clock(Pointer devh);

    int cmd_set_afh_map(Pointer devh, Pointer afh_map);

    int cmd_clear_afh_map(Pointer devh);

    int cmd_btle_sniffing(Pointer devh, short num);

    int cmd_get_access_address(Pointer devh);

    int cmd_set_access_address(Pointer devh, int access_address);

    int cmd_do_something(byte[] devh, byte[] data, int len);

    int cmd_do_something_reply(Pointer devh, byte[] data, int len);

    int cmd_get_crc_verify(Pointer devh);

    int cmd_set_crc_verify(Pointer devh, boolean verify);

    int cmd_poll(Pointer devh, UsbPacketRx.ByReference p);

    int cmd_btle_promisc(Pointer devh);

    int cmd_read_register(Pointer devh, byte reg);

    int cmd_btle_slave(Pointer devh, byte[] mac_address);

    int cmd_btle_set_target(Pointer devh, byte[] mac_address);

    int cmd_set_jam_mode(Pointer devh, JamModes mode);

    int cmd_ego(Pointer devh, int mode);
}
