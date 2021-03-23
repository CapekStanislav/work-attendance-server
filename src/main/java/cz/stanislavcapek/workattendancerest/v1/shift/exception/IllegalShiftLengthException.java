package cz.stanislavcapek.workattendancerest.v1.shift.exception;

/**
 * An instance of class {@code IllegalShiftLengthException}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class IllegalShiftLengthException extends RuntimeException {

    public IllegalShiftLengthException(double length) {
        super("Shift length can not be more then 24 hours. Length" + length);
    }
}
