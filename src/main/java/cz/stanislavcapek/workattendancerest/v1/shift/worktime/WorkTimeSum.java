package cz.stanislavcapek.workattendancerest.v1.shift.worktime;

import lombok.Value;

/**
 * An instance of class {@code WorkTimeSum}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Value(staticConstructor = "of")
public class WorkTimeSum {
    double workedOut;
    double holiday;
    double notWorkedOut;
}
