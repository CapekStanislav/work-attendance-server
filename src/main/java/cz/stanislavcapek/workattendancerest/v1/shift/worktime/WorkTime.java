package cz.stanislavcapek.workattendancerest.v1.shift.worktime;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * An instance of class {@code WorkTime}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@Entity
public class WorkTime {

    @Id
    @GeneratedValue
    private long workingTimeId;

    /**
     * Celková délka směny
     */
    private double length;
    /**
     * Odpracované hodiny ve smyslu fyzické přítomnosti na pracovišti.
     * Započitatelné do pracovní doby
     */
    private double workedOut;
    /**
     * Neodpracované hodiny ve smyslu hodin započitatelných do pracovní
     * doby, které nebyly fyzicky odpracované (např. neschopnost)
     */
    private double notWorkedOut;

    /**
     * Hodiny, které se považují za dovolenou
     */
    private double holiday;

}
