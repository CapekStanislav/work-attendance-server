package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPaymentsCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeSum;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeSumCounter;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

/**
 * An instance of class {@code WorkAttendanceUtilService}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class WorkAttendanceUtilService {

    private WorkAttendanceRepository repository;
    private WorkTimeSumCounter counter;
    private WorkTimeCounter timeCounter;
    private PremiumPaymentsCounter paymentsCounter;

    /**
     * To check existence of previous month and set corresponding hours
     * to the instance passed as argument.
     *
     * @param workAttendance
     */
    void setPreviousMonthHours(WorkAttendance workAttendance) {
        final LocalDate period = LocalDate
                .of(workAttendance.getYear(), workAttendance.getMonth(), 1)
                .minusMonths(1);

        final WorkAttendance previousMonthWorkAtt = repository
                .findByYearAndMonthAndEmployee(period.getYear(), period.getMonthValue(), workAttendance.getEmployee());
        final double previousMonth = previousMonthWorkAtt != null ? previousMonthWorkAtt.getNextMonth() : 0d;
        workAttendance.setPreviousMonth(previousMonth);
    }


    /**
     * To check existence of next month and set corresponding hours
     * to it.
     *
     * @param currentMonth
     */
    void setNextMonthHours(WorkAttendance currentMonth) {
        final LocalDate period = LocalDate
                .of(currentMonth.getYear(), currentMonth.getMonth(), 1)
                .plusMonths(1);
        final WorkAttendance nextMonth = repository
                .findByYearAndMonthAndEmployee(period.getYear(), period.getMonthValue(), currentMonth.getEmployee());
        if (nextMonth != null) {
            nextMonth.setPreviousMonth(currentMonth.getNextMonth());
            nextMonth.setNextMonth(getNextMonthHours(currentMonth));
        }
    }

    /**
     * To check the existence of next month and set corresponding hours.
     * If the current month is January or July then previous hours are set
     * to {@code 0} due to the balancing period.
     *
     * @param currentMonth
     */
    void setValidHoursInMonth(WorkAttendance currentMonth) {

        final WorkAttendance previousMonth = getPreviousMonth(currentMonth);

        double hoursFromPrevMonth = previousMonth != null ? previousMonth.getNextMonth() : 0d;

        final int month = currentMonth.getMonth();
        if (isBalancingPeriod(month)) {
            hoursFromPrevMonth = 0d;
        }

        currentMonth.setPreviousMonth(hoursFromPrevMonth);

        recalculateAllShifts(currentMonth);

        currentMonth.setNextMonth(getNextMonthHours(currentMonth));

    }

    private double getNextMonthHours(WorkAttendance currentMonth) {
        final double totalWorkedOut = getTotalWorkedOutHours(currentMonth);
        return totalWorkedOut - currentMonth.getMonthFund();
    }

    private double getTotalWorkedOutHours(WorkAttendance currentMonth) {
        final WorkTimeSum sum = counter.calculate(currentMonth);
        return sum.getWorkedOut() + sum.getHoliday() +
                sum.getNotWorkedOut() + currentMonth.getPreviousMonth();
    }

    private boolean isBalancingPeriod(int month) {
        return month == Month.JULY.getValue() || month == Month.JANUARY.getValue();
    }

    /**
     * To recalculate all shift in given work attendance
     *
     * @param workAttendance
     */
    public void recalculateAllShifts(WorkAttendance workAttendance) {
        final List<Shift> shifts = workAttendance.getShifts();
        shifts.forEach(this::calculateShift);
    }

    /**
     * To calculate work time and premium payments in given
     * shift.
     *
     * @param shift
     */
    public void calculateShift(Shift shift) {
        final LocalDateTime start = shift.getStart();
        final LocalDateTime end = shift.getEnd();

        if (start.toLocalTime().isAfter(end.toLocalTime())) {
            shift.setEnd(start.plusDays(1).with(end.toLocalTime()));
        }
        timeCounter.calculate(shift);
        paymentsCounter.calculate(shift);
    }


    private WorkAttendance getPreviousMonth(WorkAttendance currentMonth) {
        final LocalDate period = LocalDate
                .of(currentMonth.getYear(), currentMonth.getMonth(), 1)
                .minusMonths(1);
        final WorkAttendance previousMonthWorkAtt = repository
                .findByYearAndMonthAndEmployee(period.getYear(), period.getMonthValue(), currentMonth.getEmployee());
        return previousMonthWorkAtt;
    }

}
