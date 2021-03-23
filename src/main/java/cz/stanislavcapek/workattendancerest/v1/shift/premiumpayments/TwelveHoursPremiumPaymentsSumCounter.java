package cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

/**
 * An instance of class {@code TwelveHoursPremiumPaymentsSumCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class TwelveHoursPremiumPaymentsSumCounter implements PremiumPaymentsSumCounter {

    @Override
    public PremiumPaymentsSum calculate(WorkAttendance workAttendance) {
        final List<Shift> shifts = workAttendance.getShifts();

        double night = getSum(shifts, PremiumPayments::getNight);
        double weekend = getSum(shifts, PremiumPayments::getWeekend);
        double holiday = getSum(shifts, PremiumPayments::getHoliday);
        double overtime = getSum(shifts, PremiumPayments::getOvertime);

        return PremiumPaymentsSum.of(
                night,
                weekend,
                holiday,
                overtime
        );

    }

    private double getSum(List<Shift> shifts, Function<? super PremiumPayments, Double> reference) {
        return shifts.stream()
                .map(Shift::getPremiumPayments)
                .mapToDouble(reference::apply)
                .sum();
    }
}
