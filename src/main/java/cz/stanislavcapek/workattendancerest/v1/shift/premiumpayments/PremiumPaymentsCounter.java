package cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;

/**
 * Instance rozhraní {@code PremiumPaymentsCounter}
 *
 * @author Stanislav Čapek
 */
public interface PremiumPaymentsCounter {

    void calculate(Shift shift);
}
