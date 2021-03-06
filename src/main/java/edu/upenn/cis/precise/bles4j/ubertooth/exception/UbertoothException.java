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
