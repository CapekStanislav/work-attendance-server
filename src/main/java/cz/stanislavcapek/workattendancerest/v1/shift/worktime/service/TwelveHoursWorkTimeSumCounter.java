package cz.stanislavcapek.workattendancerest.v1.shift.worktime.service;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeSum;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

/**
 * An instance of class {@code TwelveHoursWorkTimeSumCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class TwelveHoursWorkTimeSumCounter implements WorkTimeSumCounter {

    @Override
    public WorkTimeSum calculate(WorkAttendance workAttendance) {
        final List<Shift> shifts = workAttendance.getShifts();

        double workedOut = getSum(shifts, WorkTime::getWorkedOut);
        double notWorkedOut = getSum(shifts, WorkTime::getNotWorkedOut);
        double holiday = getSum(shifts, WorkTime::getHoliday);

       return WorkTimeSum.of(
                workedOut,
                holiday,
                notWorkedOut
        );
    }

    private double getSum(List<Shift> shifts, Function<? super WorkTime, Double> reference) {
        return shifts.stream()
                .map(Shift::getWorkTime)
                .mapToDouble(reference::apply)
                .sum();

    }
}
