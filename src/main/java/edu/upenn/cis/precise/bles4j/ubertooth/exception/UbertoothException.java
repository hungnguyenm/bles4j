package edu.upenn.cis.precise.bles4j.ubertooth.exception;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class UbertoothException extends Exception {
    private int error;
    private UbertoothExceptionCode code;

    public UbertoothException(int error) {
        super();
        this.code = UbertoothExceptionCode.lookup(error);
        this.error = error;
    }

    @Override
    public String getMessage() {
        return code.toString();
    }

    public UbertoothExceptionCode getCode() {
        return code;
    }

    public int getError() {
        return error;
    }
}
