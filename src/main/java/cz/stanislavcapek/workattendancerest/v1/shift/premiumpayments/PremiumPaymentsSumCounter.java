package cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments;

import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;

/**
 * An instance of interface {@code PremiumPaymentsSumCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface PremiumPaymentsSumCounter {

    PremiumPaymentsSum calculate(WorkAttendance workAttendance);

}
