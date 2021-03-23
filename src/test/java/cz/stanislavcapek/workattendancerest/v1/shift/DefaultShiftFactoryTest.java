package cz.stanislavcapek.workattendancerest.v1.shift;

import cz.stanislavcapek.workattendancerest.v1.shift.exception.IllegalShiftLengthException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefaultShiftFactoryTest {

    public static final LocalDate CHRISTMAS_DATE = LocalDate.of(2020, 12, 24);

    @Test
    void whenSetMonthThenEqualMonth() {
        final ShiftFactory factory = new DefaultShiftFactory();
        factory.setMonth(5);
        assertEquals(5, factory.getMonth());
    }

    @Test
    void whenSetYearThenEqualYear() {
        final ShiftFactory factory = new DefaultShiftFactory();
        factory.setYear(2020);
        assertEquals(2020, factory.getYear());
    }

    @Test
    void whenSetPeriodThenEqualYearAndMonth() {
        final ShiftFactory factory = new DefaultShiftFactory();
        factory.setPeriod(LocalDate.of(2020, 4, 1));
        assertAll(() -> {
            assertEquals(2020, factory.getYear());
            assertEquals(4, factory.getMonth());
        });
    }

    @Test
    void whenShiftLengthEqualOrMoreThen24HourThenException() {
        final ShiftFactory factory = new DefaultShiftFactory();
        // 36 hours
        final LocalDateTime start = LocalDate.of(2020, 1, 1).atTime(7, 0);
        final LocalDateTime end = LocalDate.of(2020, 1, 2).atTime(19, 0);

        assertThrows(IllegalShiftLengthException.class, () -> factory.createShift(start, end));

        assertThrows(IllegalShiftLengthException.class, () -> factory.createShift(start.toLocalDate(), 36d));
    }

    @Test
    void createShiftByDate() {
        final ShiftFactory factory = new DefaultShiftFactory();
        final Shift shift = factory.createShift(CHRISTMAS_DATE);

        assertAll(() -> {
            assertEquals(CHRISTMAS_DATE.atTime(7, 0), shift.getStart());
            assertEquals(CHRISTMAS_DATE.atTime(19, 0), shift.getEnd());
        });
    }

    @Test
    void createShiftByDateAndShiftType() {
        final ShiftFactory factory = new DefaultShiftFactory();
        final Shift shift = factory.createShift(CHRISTMAS_DATE, ShiftTypeTwelveHours.DAY);
        assertAll(() -> {
            assertEquals(CHRISTMAS_DATE.atTime(7, 0), shift.getStart());
            assertEquals(CHRISTMAS_DATE.atTime(19, 0), shift.getEnd());
        });
    }

    @Test
    void createShiftByStartEndShiftType() {
        final ShiftFactory factory = new DefaultShiftFactory();

        final LocalDateTime start = LocalDate.of(2020, 1, 1).atTime(7, 0);
        final LocalDateTime end = LocalDate.of(2020, 1, 2).atTime(5, 0);
        final Shift shift = factory.createShift(start, end);
        assertAll(() -> {
            assertEquals(start, shift.getStart());
            assertEquals(end, shift.getEnd());
        });
    }

    @Test
    void createShiftByDateAndLength() {
        final ShiftFactory factory = new DefaultShiftFactory();
        final Shift shift = factory.createShift(CHRISTMAS_DATE, 20d);
        assertAll(()-> {
            assertEquals(CHRISTMAS_DATE.atTime(7,0),shift.getStart());
            assertEquals(CHRISTMAS_DATE.plusDays(1).atTime(3,0),shift.getEnd());
        });

    }

    // TODO: 04.01.2021 test other creation methods
}