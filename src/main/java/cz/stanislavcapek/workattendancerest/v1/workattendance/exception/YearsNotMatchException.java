package cz.stanislavcapek.workattendancerest.v1.workattendance.exception;

public class YearsNotMatchException extends RuntimeException {
    public YearsNotMatchException(String message) {
        super(message);
    }
}
