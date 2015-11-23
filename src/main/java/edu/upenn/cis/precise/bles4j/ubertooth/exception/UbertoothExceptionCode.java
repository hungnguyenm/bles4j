package edu.upenn.cis.precise.bles4j.ubertooth.exception;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public enum UbertoothExceptionCode {
    UBERTOOTH_SUCCESS(0),
    UBERTOOTH_ERROR_NOT_INITIALIZED(-1),
    UBERTOOTH_ERROR_NO_DEVICE(-2),
    UBERTOOTH_ERROR_NOT_CONNECTED(-3),
    UBERTOOTH_ERROR_OTHER(-99),
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