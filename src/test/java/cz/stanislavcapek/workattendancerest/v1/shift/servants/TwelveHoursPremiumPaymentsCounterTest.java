package cz.stanislavcapek.workattendancerest.v1.shift.servants;

import cz.stanislavcapek.workattendancerest.v1.holiday.web.HolidayRepository;
import cz.stanislavcapek.workattendancerest.v1.shift.service.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPayments;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPaymentsCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.TwelveHoursPremiumPaymentsCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.TwelveHoursWorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeCounter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwelveHoursPremiumPaymentsCounterTest {

    private static final ShiftFactory FACTORY = new DefaultShiftFactory();
    public static final WorkTimeCounter TIME_COUNTER = new TwelveHoursWorkTimeCounter();
    private static final LocalDate DATE_WEEK = LocalDate.of(2020, 1, 6);
    private static final LocalDate DATE_HOLIDAY_IN_WEEK = LocalDate.of(2020, 1, 1);
    public static final LocalDate DATE_FRIDAY = LocalDate.of(2020, 1, 3);
    public static final LocalDate DATE_SATURDAY = LocalDate.of(2020, 1, 4);
    public static final LocalDate DATE_SUNDAY = LocalDate.of(2020, 1, 5);


    @Mock
    private HolidayRepository holidayRepository;

    @Test
    void whenDayShiftInWeek() {
        final Shift shift = FACTORY.createShift(DATE_WEEK);

        when(holidayRepository.existsByDate(any())).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(0, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenDayShiftInHoliday() {
        final Shift shift = FACTORY.createShift(DATE_HOLIDAY_IN_WEEK);

        when(holidayRepository.existsByDate(any())).thenReturn(true);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(0, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(12, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenDayShiftInWeekend() {
        final Shift shift = FACTORY.createShift(DATE_SATURDAY);

        when(holidayRepository.existsByDate(any())).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(0, premiumPayments.getNight());
            assertEquals(12, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenNightShiftInWeek() {
        final Shift shift = FACTORY.createShift(DATE_WEEK, ShiftTypeTwelveHours.NIGHT);

        when(holidayRepository.existsByDate(any())).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenNightShiftInHoliday() {
        final Shift shift = FACTORY.createShift(DATE_HOLIDAY_IN_WEEK, ShiftTypeTwelveHours.NIGHT);

        when(holidayRepository.existsByDate(DATE_HOLIDAY_IN_WEEK)).thenReturn(true);
        when(holidayRepository.existsByDate(DATE_HOLIDAY_IN_WEEK.plusDays(1))).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(5, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenNightShiftToHoliday() {
        final Shift shift = FACTORY.createShift(DATE_HOLIDAY_IN_WEEK.minusDays(1), ShiftTypeTwelveHours.NIGHT);

        when(holidayRepository.existsByDate(DATE_HOLIDAY_IN_WEEK.minusDays(1))).thenReturn(false);
        when(holidayRepository.existsByDate(DATE_HOLIDAY_IN_WEEK)).thenReturn(true);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(7, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenNightShiftInFriday() {
        final Shift shift = FACTORY.createShift(DATE_FRIDAY, ShiftTypeTwelveHours.NIGHT);

        when(holidayRepository.existsByDate(DATE_FRIDAY)).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(7, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenNightShiftInSaturday() {
        final Shift shift = FACTORY.createShift(DATE_SATURDAY, ShiftTypeTwelveHours.NIGHT);

        when(holidayRepository.existsByDate(DATE_SATURDAY)).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(12, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenNightShiftInSunday() {
        final Shift shift = FACTORY.createShift(DATE_SUNDAY, ShiftTypeTwelveHours.NIGHT);

        when(holidayRepository.existsByDate(DATE_SUNDAY)).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(5, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenShiftFrom2230To300InFriday() {
        final LocalDateTime start = DATE_FRIDAY.atTime(22, 30);
        final LocalDateTime end = DATE_SATURDAY.atTime(3, 0);
        final Shift shift = FACTORY.createShift(start, end);

        when(holidayRepository.existsByDate(any())).thenReturn(false);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(4.5, premiumPayments.getNight());
            assertEquals(3, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void when23HourShiftFromHoliday() {
        final Shift shift = FACTORY.createShift(DATE_HOLIDAY_IN_WEEK, 23d);

        when(holidayRepository.existsByDate(DATE_HOLIDAY_IN_WEEK)).thenReturn(true);

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(8, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(17, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }

    @Test
    void whenShiftTypeIsNoneThenZeroPremiumPayments() {
        final Shift shift = FACTORY.createShift(
                DATE_SATURDAY,
                ShiftTypeTwelveHours.NONE
        );

        PremiumPaymentsCounter paymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        TIME_COUNTER.calculate(shift);
        paymentsCounter.calculate(shift);
        assertAll(() -> {
            final PremiumPayments premiumPayments = shift.getPremiumPayments();
            assertEquals(0, premiumPayments.getNight());
            assertEquals(0, premiumPayments.getWeekend());
            assertEquals(0, premiumPayments.getHoliday());
            assertEquals(0, premiumPayments.getOvertime());
        });
    }
}