package edu.upenn.cis.precise.bles4j.ubertooth.exception;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public enum UbertoothExceptionCode {
    UBERTOOTH_SUCCESS(0),

    LIBUSB_ERROR_IO(-1),
    LIBUSB_ERROR_INVALID_PARAM(-2),
    LIBUSB_ERROR_ACCESS(-3),
    LIBUSB_ERROR_NO_DEVICE(-4),
    LIBUSB_ERROR_NOT_FOUND(-5),
    LIBUSB_ERROR_BUSY(-6),
    LIBUSB_ERROR_TIMEOUT(-7),
    LIBUSB_ERROR_OVERFLOW(-8),
    LIBUSB_ERROR_PIPE(-9),
    LIBUSB_ERROR_INTERRUPTED(-10),
    LIBUSB_ERROR_NO_MEM(-11),
    LIBUSB_ERROR_NOT_SUPPORTED(-12),
    UBERTOOTH_ERROR_NOT_INITIALIZED(-50),
    UBERTOOTH_ERROR_NO_DEVICE(-51),
    UBERTOOTH_ERROR_NOT_CONNECTED(-52),
    UBERTOOTH_ERROR_UNKNOWN_ADVERTISING_CHANNEL(-53),
    UBERTOOTH_ERROR_UNPROCESSED_PACKET_STACK_OVERFLOW(-54),
    LIBUSB_ERROR_OTHER(-99),
    UBERTOOTH_ERROR_UNKNOWN(Integer.MIN_VALUE);

    private int code;

    UbertoothExceptionCode(int code) {
        this.code = code;
    }

    public static UbertoothExceptionCode lookup(int code) {
        for (UbertoothExceptionCode exceptionCode : values()) {
            if (exceptionCode.code == code)
                return exceptionCode;
        }
        return UBERTOOTH_ERROR_UNKNOWN;
    }

    public int getCode() {
        return code;
    }
    }
