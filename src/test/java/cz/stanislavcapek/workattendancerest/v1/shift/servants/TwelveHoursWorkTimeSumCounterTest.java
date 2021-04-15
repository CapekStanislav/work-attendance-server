package cz.stanislavcapek.workattendancerest.v1.shift.servants;

import cz.stanislavcapek.workattendancerest.v1.holiday.web.HolidayRepository;
import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.shift.service.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.*;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.service.TwelveHoursWorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.service.TwelveHoursWorkTimeSumCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.service.WorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.service.WorkTimeSumCounter;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@ExtendWith(MockitoExtension.class)
class TwelveHoursWorkTimeSumCounterTest {

    @Mock
    private HolidayRepository holidayRepository;

    public static final DefaultShiftFactory SHIFT_FACTORY = new DefaultShiftFactory();
    private final WorkTimeSumCounter sumCounter = new TwelveHoursWorkTimeSumCounter();
    private final WorkTimeCounter timeCounter = new TwelveHoursWorkTimeCounter();

    /**
     * For december 2020
     */
    @Test
    void calculateWorkAttendance() {
        final WorkAttendance workAttendance = getWorkAttendance();

        workAttendance.getShifts().forEach(timeCounter::calculate);

        final WorkTimeSum sum = sumCounter.calculate(workAttendance);
        assertEquals(30, sum.getWorkedOut());
        assertEquals(12, sum.getNotWorkedOut());
        assertEquals(30, sum.getHoliday()); // in term of free day
    }

    private WorkAttendance getWorkAttendance() {
        final WorkAttendance workAttendance = new WorkAttendance();
        workAttendance.setMonth(12);
        workAttendance.setYear(2020);
        workAttendance.setWeeklyWorkTime(37.5);
        workAttendance.setPreviousMonth(100d);
        workAttendance.setShifts(getShifts());
        return workAttendance;
    }

    private List<Shift> getShifts() {
        final int firstDay = 1;
        final Month december = Month.DECEMBER;
        final int year = 2020;
        final LocalDate firstDateOfMonth = LocalDate.of(year, december.getNumber(), firstDay);

        final int numberOfDays = Month.getNumberOfDays(december, year);
        List<Shift> shifts = new ArrayList<>();

        for (int i = 0; i < numberOfDays; i++) {
            int day = i + 1;
            shifts.add(
                    SHIFT_FACTORY.createShift(LocalDate.of(year, december.getNumber(), day), ShiftTypeTwelveHours.NONE)
            );
        }


        Shift firstFullHoliday = SHIFT_FACTORY.createShift(firstDateOfMonth, ShiftTypeTwelveHours.HOLIDAY);
        Shift secondFullHoliday = SHIFT_FACTORY.createShift(firstDateOfMonth.plusDays(1), ShiftTypeTwelveHours.HOLIDAY);
        Shift firstHalfHoliday = SHIFT_FACTORY.createShift(firstDateOfMonth.plusDays(2), ShiftTypeTwelveHours.HALF_HOLIDAY);
        Shift sickDay = SHIFT_FACTORY.createShift(firstDateOfMonth.plusDays(3), ShiftTypeTwelveHours.SICK_DAY);

        shifts.set(0, firstFullHoliday);
        shifts.set(1, secondFullHoliday);
        shifts.set(2, firstHalfHoliday);
        shifts.set(3, sickDay);

        final LocalDate beforeChristmasDate = firstDateOfMonth.withDayOfMonth(23);
        final Shift beforeChristmasNightShift = SHIFT_FACTORY
                .createShift(beforeChristmasDate, ShiftTypeTwelveHours.NIGHT);
        shifts.set(22, beforeChristmasNightShift);

        final LocalDate christmasDate = beforeChristmasDate.plusDays(1);
        final Shift christmasNightShift = SHIFT_FACTORY.createShift(christmasDate, ShiftTypeTwelveHours.NIGHT);
        shifts.set(23, christmasNightShift);

        return shifts;
    }
}