package cz.stanislavcapek.workattendancerest.v1.shift.servants;

import cz.stanislavcapek.workattendancerest.v1.shift.service.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.TwelveHoursWorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TwelveHoursWorkTimeCounterTest {

    public static final DefaultShiftFactory FACTORY = new DefaultShiftFactory();
    public static final TwelveHoursWorkTimeCounter TIME_COUNTER = new TwelveHoursWorkTimeCounter();
    public static final LocalDate FIRST_DATE_OF_MONTH = LocalDate.of(2020,12,1);

    @Test
    void calculateDayShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.DAY);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(12, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateNightShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.NIGHT);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(12, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateHolidayShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.HOLIDAY);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(0, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(12,workTime.getHoliday());
        });
    }

    @Test
    void calculateHalfHolidayShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.HALF_HOLIDAY);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(6, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(6,workTime.getHoliday());
        });
    }

    @Test
    void calculateSickDayShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.SICK_DAY);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(0, workTime.getWorkedOut());
            assertEquals(12, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateHomeCareShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.HOME_CARE);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(0, workTime.getWorkedOut());
            assertEquals(12, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateTrainingShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.TRAINING);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        System.out.println("shiftDay = " + shiftDay);
        assertAll(() -> {
            assertEquals(7.5, workTime.getLength());
            assertEquals(7.5, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateInabilityShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.INABILITY);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(12, workTime.getLength());
            assertEquals(0, workTime.getWorkedOut());
            assertEquals(12, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateNoneShift() {
        final Shift shiftDay = FACTORY.createShift(FIRST_DATE_OF_MONTH, ShiftTypeTwelveHours.NONE);
        TIME_COUNTER.calculate(shiftDay);
        final WorkTime workTime = shiftDay.getWorkTime();

        assertAll(() -> {
            assertEquals(0, workTime.getLength());
            assertEquals(0, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }

    @Test
    void calculateCustomShift() {
        final Shift shift = FACTORY.createShift(FIRST_DATE_OF_MONTH, 3.25);
        TIME_COUNTER.calculate(shift);
        final WorkTime workTime = shift.getWorkTime();

        assertAll(() -> {
            assertEquals(3.25, workTime.getLength());
            assertEquals(3.25, workTime.getWorkedOut());
            assertEquals(0, workTime.getNotWorkedOut());
            assertEquals(0,workTime.getHoliday());
        });
    }
}