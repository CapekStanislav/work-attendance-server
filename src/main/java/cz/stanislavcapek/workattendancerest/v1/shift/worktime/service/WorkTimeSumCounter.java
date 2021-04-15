package cz.stanislavcapek.workattendancerest.v1.shift.worktime.service;

import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeSum;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;

/**
 * An instance of interface {@code WorkTimeSumCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface WorkTimeSumCounter {

    /**
     * Vypočítá sumariziaci v pracovní docházce pro všechny položky v
     * {@link WorkTime}.
     *
     * @param workAttendance pracovní docházka
     * @return Sumarzizace za měsíc
     */
    WorkTimeSum calculate(WorkAttendance workAttendance);

}
