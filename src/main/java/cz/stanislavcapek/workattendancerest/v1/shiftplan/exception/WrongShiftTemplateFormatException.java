package cz.stanislavcapek.workattendancerest.v1.shiftplan.exception;

public class WrongShiftTemplateFormatException extends RuntimeException {
    public WrongShiftTemplateFormatException(String message) {
        super(message);
    }
}
