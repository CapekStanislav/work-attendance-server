package cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments;

import cz.stanislavcapek.workattendancerest.v1.holiday.web.HolidayRepository;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeCounter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

/**
 * Instance třídy {@code TwelveHoursPremiumPaymentsCounter} vypočítá přílatky směny
 * na základě (předem vypočítané) odpracovaných hodin.
 *
 * @author Stanislav Čapek
 * @see WorkTimeCounter
 */
@Service
@AllArgsConstructor
public class TwelveHoursPremiumPaymentsCounter implements PremiumPaymentsCounter {

    private HolidayRepository holidayRepository;

    @Override
    public void calculate(Shift shift) {
        final PremiumPayments premiumPayments = shift.getPremiumPayments();
        premiumPayments.setNight(getNightHours(shift));
        premiumPayments.setWeekend(getWeekendHours(shift));
        premiumPayments.setHoliday(getHolidayHours(shift));
        premiumPayments.setOvertime(getOverTimeHours(shift));
    }

    private double getOverTimeHours(Shift shift) {
        if (shift.getShiftTypeTwelveHours() == ShiftTypeTwelveHours.OVERTIME) {
            return shift.getWorkTime().getWorkedOut();
        }
        return 0;
    }

    private double getNightHours(Shift shift) {
        if (shift.getWorkTime().getWorkedOut() == 0) {
            return 0d;
        }

        final LocalDateTime start = shift.getStart();
        LocalDateTime end = shift.getEnd();
        Duration duration = Duration.ZERO;

        if (start.isAfter(end)) {
            throw new IllegalStateException("Začátek směny nemůže být po konci směny");
        }

        LocalDateTime temp = start;
        while (temp.compareTo(end) < 0) {
            final int hour = temp.getHour();
            if (hour >= 22 || hour < 6) {
                duration = duration.plusMinutes(1);
            }

            temp = temp.plusMinutes(1);
        }

        return duration.toMinutes() / 60d;
    }

    /**
     * Metoda vrací počet odpracovaných hodin o víkendu (sobota, neděle).
     *
     * @param shift
     * @return desetinné číslo představující délku služby v hodinách
     */
    private double getWeekendHours(Shift shift) {
        LocalDateTime start = shift.getStart();
        LocalDateTime end = shift.getEnd();

        double hours = 0.0;
        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            int comparison = start.toLocalDate().compareTo(end.toLocalDate());
            Duration toMidnight = Duration.between(start, LocalDateTime.of(start.toLocalDate(), LocalTime.MAX));
            Duration afterMidnight = Duration.between(LocalDateTime.of(end.toLocalDate(), LocalTime.MIDNIGHT), end);
            switch (day) {
                case FRIDAY:
                    if (comparison < 0) {
                        hours += (afterMidnight.toMinutes()) / 60.0;
                    }
                    break;
                case SATURDAY:
                    hours += shift.getWorkTime().getWorkedOut();
                    break;
                case SUNDAY:
                    if (comparison < 0) {
                        hours += (toMidnight.toMinutes() + 1) / 60.0;
                    } else {
                        hours += shift.getWorkTime().getWorkedOut();
                    }
                    break;
            }
        }
        return hours;
    }

    private double getHolidayHours(Shift shift) {
        final LocalDateTime start = shift.getStart();
        final LocalDateTime end = shift.getEnd();

        final LocalDate startLocalDate = start.toLocalDate();
        final LocalDate endLocalDate = end.toLocalDate();
        if (startLocalDate.isEqual(endLocalDate)) {
            // is over day
            if (isHoliday(startLocalDate)) {
                return shift.getWorkTime().getWorkedOut();
            }
        } else {
            // is over night
            Duration count = Duration.ZERO;
            final LocalTime midnightTo = LocalTime.MAX;
            final LocalTime midnightFrom = LocalTime.MIN;
            if (isHoliday(startLocalDate)) {
                final Duration durToMidnight = Duration.between(
                        start,
                        LocalDateTime.of(startLocalDate, midnightTo)
                );
                count = count.plus(durToMidnight).plusNanos(1);
            }
            if (isHoliday(endLocalDate)) {
                final Duration durFromMidnight = Duration.between(
                        LocalDateTime.of(endLocalDate, midnightFrom),
                        end
                );
                count = count.plus(durFromMidnight);
            }
            return count.toMinutes() / 60d;
        }
        return 0;
    }

    private boolean isHoliday(LocalDate day) {
        return holidayRepository.existsByDate(day);
    }
}
