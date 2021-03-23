package cz.stanislavcapek.workattendancerest.v1.workattendance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An instance of class {@code InvalidPropertiesStateException}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPropertiesStateException extends RuntimeException {

    public InvalidPropertiesStateException(String message) {
        super(message);
    }
}
