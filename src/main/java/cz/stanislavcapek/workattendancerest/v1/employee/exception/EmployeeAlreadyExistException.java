package cz.stanislavcapek.workattendancerest.v1.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An instance of class {@code EmployeeAlreadyExistException}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmployeeAlreadyExistException extends RuntimeException {

    public EmployeeAlreadyExistException(String message) {
        super(message);
    }
}
