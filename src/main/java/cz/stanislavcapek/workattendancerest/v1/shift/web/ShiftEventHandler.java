package cz.stanislavcapek.workattendancerest.v1.shift.web;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPayments;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPaymentsCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeCounter;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.time.LocalDateTime;

/**
 * An instance of class {@code ShiftEventHandler}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@RepositoryEventHandler
@AllArgsConstructor
public class ShiftEventHandler {

    private final PremiumPaymentsCounter paymentsCounter;
    private final WorkTimeCounter timeCounter;

    @HandleBeforeCreate
    public void handleShiftCreation(Shift shift) {
        prepare(shift);
        calculateShiftProperties(shift);
    }

    @HandleBeforeSave
    public void handleShiftSaving(Shift shift) {
        prepare(shift);
        calculateShiftProperties(shift);
    }

    private void prepare(Shift shift) {
        final LocalDateTime start = shift.getStart();
        LocalDateTime end = shift.getEnd();

        if (start.isAfter(end)) {
            end = start
                    .toLocalDate()
                    .plusDays(1)
                    .atTime(end.toLocalTime());
            shift.setEnd(end);
        }

        if (shift.getShiftTypeTwelveHours() == null) {
            ShiftTypeTwelveHours type = ShiftTypeTwelveHours.resolveType(shift);
            shift.setShiftTypeTwelveHours(type);
        }
        shift.setWorkTime(new WorkTime());
        shift.setPremiumPayments(new PremiumPayments());
    }

    private void calculateShiftProperties(Shift shift) {
        timeCounter.calculate(shift);
        paymentsCounter.calculate(shift);
    }
}
