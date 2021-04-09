package cz.stanislavcapek.workattendancerest.v1.shift.servants;

import cz.stanislavcapek.workattendancerest.v1.holiday.web.HolidayRepository;
import cz.stanislavcapek.workattendancerest.v1.shift.service.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.*;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.TwelveHoursWorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwelveHoursPremiumPaymentsSumCounterTest {

    private static final ShiftFactory FACTORY = new DefaultShiftFactory();

    private static final LocalDate DECEMBER_24 = LocalDate.of(2020, 12, 24);
    private static final LocalDate DECEMBER_25 = LocalDate.of(2020, 12, 25);
    private static final LocalDate DECEMBER_26 = LocalDate.of(2020, 12, 26);

    @Mock
    private HolidayRepository holidayRepository;

    private final PremiumPaymentsSumCounter sumCounter = new TwelveHoursPremiumPaymentsSumCounter();
    private final WorkTimeCounter timeCounter = new TwelveHoursWorkTimeCounter();


    /**
     * For december 2020
     */
    @Test
    void calculateWorkAttendance() {
        final WorkAttendance workAttendance = getWorkAttendance();

        final PremiumPaymentsCounter premiumPaymentsCounter = new TwelveHoursPremiumPaymentsCounter(holidayRepository);

        workAttendance.getShifts().forEach(timeCounter::calculate);

        when(holidayRepository.existsByDate(any()))
                .then((Answer<Boolean>) invocationOnMock -> {
                    final LocalDate date = invocationOnMock.getArgument(0, LocalDate.class);
                    return date.equals(DECEMBER_24) || date.equals(DECEMBER_25) || date.equals(DECEMBER_26);
                });

        workAttendance.getShifts().forEach(premiumPaymentsCounter::calculate);

        verify(holidayRepository, atLeast(4)).existsByDate(any());

        final PremiumPaymentsSum premiumPaymentsSum = sumCounter.calculate(workAttendance);

        assertAll(() -> {
            assertEquals(16, premiumPaymentsSum.getNight());
            assertEquals(19, premiumPaymentsSum.getWeekend());
            assertEquals(29, premiumPaymentsSum.getHoliday());
            assertEquals(12, premiumPaymentsSum.getOvertime());
        });

    }

    private WorkAttendance getWorkAttendance() {

        final WorkAttendance workAttendance = new WorkAttendance();
        final int month = 12;
        final int year = 2020;
        workAttendance.setMonth(month);
        workAttendance.setYear(year);
        workAttendance.setWeeklyWorkTime(37.5);
        workAttendance.setPreviousMonth(100d);
        workAttendance.setShifts(getShifts(year, month));
        return workAttendance;
    }

    private List<Shift> getShifts(int year, int month) {

        List<Shift> shifts = new ArrayList<>();

        final Shift december23Shift = FACTORY
                .createShift(LocalDate.of(year, month, 23), ShiftTypeTwelveHours.OVERTIME);
        shifts.add(december23Shift);

        final Shift december24Shift = FACTORY
                .createShift(LocalDate.of(year, month, 24), ShiftTypeTwelveHours.DAY);
        shifts.add(december24Shift);

        final Shift december25Shift = FACTORY
                .createShift(LocalDate.of(year, month, 25), ShiftTypeTwelveHours.NIGHT);
        shifts.add(december25Shift);

        final Shift december26Shift = FACTORY
                .createShift(LocalDate.of(year, month, 26), ShiftTypeTwelveHours.NIGHT);
        shifts.add(december26Shift);

        return shifts;
    }
}