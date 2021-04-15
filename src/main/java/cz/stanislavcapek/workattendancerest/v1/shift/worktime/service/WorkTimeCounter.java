package cz.stanislavcapek.workattendancerest.v1.shift.worktime.service;


import cz.stanislavcapek.workattendancerest.v1.shift.Shift;

/**
 * An instance of interface {@code WorkTimeCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface WorkTimeCounter {
    void calculate(Shift shift);
}
