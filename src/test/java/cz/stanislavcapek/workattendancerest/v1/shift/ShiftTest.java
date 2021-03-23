package cz.stanislavcapek.workattendancerest.v1.shift;

import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPayments;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours.DAY;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShiftTest {

    public static final LocalDate FIRST_DATE_OF_MONTH = LocalDate.of(2020, 12, 1);

    @Test
    void whenStartIsAfterEndThenException() {
        final LocalDateTime start = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(7, 0));
        final LocalDateTime end = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(2, 0));
        assertThrows(IllegalArgumentException.class, () ->
                new Shift(
                start,
                end,
                new WorkTime(),
                new PremiumPayments(),
                DAY
        ));
    }

    @Test
    void whenStartIsNullThenException() {
        final LocalDateTime end = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(2, 0));
        assertThrows(NullPointerException.class, () -> {
            new Shift(
                    null,
                    end,
                    new WorkTime(),
                    new PremiumPayments(),
                    DAY
            );
        });
    }

    @Test
    void whenEndIsNullThenException() {
        final LocalDateTime start = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(7, 0));
        assertThrows(NullPointerException.class, () -> {
            new Shift(
                    start,
                    null,
                    new WorkTime(),
                    new PremiumPayments(),
                    DAY
            );
        });
    }

    @Test
    void whenWorkingTimeIsNullThenException() {
        final LocalDateTime start = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(7, 0));
        final LocalDateTime end = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(19, 0));
        assertThrows(NullPointerException.class, () ->
                new Shift(
                        start,
                        end,
                        null,
                        new PremiumPayments(),
                        DAY
                ));

    }

    @Test
    void whenPremiumPaymentsIsNullThenException() {
        final LocalDateTime start = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(7, 0));
        final LocalDateTime end = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(19, 0));
        assertThrows(NullPointerException.class, () ->
                new Shift(
                        start,
                        end,
                        new WorkTime(),
                        null,
                        DAY
                ));
    }

    @Test
    void whenShiftTypeIsNullThenException() {
        final LocalDateTime start = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(7, 0));
        final LocalDateTime end = LocalDateTime.of(FIRST_DATE_OF_MONTH, LocalTime.of(19, 0));
        assertThrows(NullPointerException.class, () ->
                new Shift(
                        start,
                        end,
                        new WorkTime(),
                        new PremiumPayments(),
                        null
                ));
    }
}