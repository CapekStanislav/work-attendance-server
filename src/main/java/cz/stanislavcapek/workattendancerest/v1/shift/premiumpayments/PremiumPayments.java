package cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * An instance of class {@code PremiumPayments}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@Entity
public class PremiumPayments {

    @Id
    @GeneratedValue
    private long premiumPaymentsId;
    private double night;
    private double weekend;
    private double holiday;
    private double overtime;

}
