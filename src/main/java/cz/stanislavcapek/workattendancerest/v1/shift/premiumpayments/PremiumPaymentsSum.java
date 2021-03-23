package cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments;

import lombok.Value;

/**
 * An instance of class {@code PremiumPaymentsSum}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Value(staticConstructor = "of")
public class PremiumPaymentsSum {

    double night;
    double weekend;
    double holiday;
    double overtime;

}
