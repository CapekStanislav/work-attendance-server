package cz.stanislavcapek.workattendancerest.v1.workattendance;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.model.WorkTimeFund;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * An instance of class {@code WorkAttendance}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
@Component
public class WorkAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long workAttendanceId;

    @OneToOne
    private Employee employee;

    /**
     * Month value in range 1 - 12
     */
    private int month;

    private int year;

    private double weeklyWorkTime;

    private double monthFund;

    private double previousMonth;

    private double nextMonth = 0;

    private boolean locked = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Shift> shifts;

    public WorkAttendance(long workAttendanceId, Employee employee, int month,
                          int year, double weeklyWorkTime,
                          double previousMonth, double nextMonth, boolean locked,
                          List<Shift> shifts) {
        this.workAttendanceId = workAttendanceId;
        this.employee = employee;
        this.month = month;
        this.year = year;
        this.previousMonth = previousMonth;
        this.nextMonth = nextMonth;
        this.locked = locked;
        this.shifts = shifts;
        setWeeklyWorkTime(weeklyWorkTime);
    }

    public void setWeeklyWorkTime(double weeklyWorkTime) {
        final LocalDate thisMonthDate = LocalDate.of(year, month, 1);
        this.monthFund = WorkTimeFund.calculate(thisMonthDate, weeklyWorkTime);
        this.weeklyWorkTime = weeklyWorkTime;
    }
}
