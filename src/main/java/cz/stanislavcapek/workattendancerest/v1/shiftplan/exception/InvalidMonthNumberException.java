package cz.stanislavcapek.workattendancerest.v1.shiftplan.exception;

/**
 * An instance of class {@code InvalidMonthNumberException}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class InvalidMonthNumberException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE = "Neplatné číslo měsíce. Platné rozmezí je 1-12:";

    public InvalidMonthNumberException(int monthNum) {
        super(String.format("%s %d", EXCEPTION_MESSAGE, monthNum));
    }
}
