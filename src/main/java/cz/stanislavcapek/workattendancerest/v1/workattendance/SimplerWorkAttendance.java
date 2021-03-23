package cz.stanislavcapek.workattendancerest.v1.workattendance;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import org.springframework.data.rest.core.config.Projection;

/**
 * An instance of interface {@code SimplerWorkAttendance}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Projection(name = "SimpleView", types = WorkAttendance.class)
public interface SimplerWorkAttendance {

    Employee getEmployee();

    int getYear();

    int getMonth();

    double getMonthFund();

    double getPreviousMonth();

    double getNextMonth();

    boolean isLocked();

}
