package cz.stanislavcapek.workattendancerest.v1.workattendance.service;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.employee.web.EmployeeRepository;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.ShiftPlan;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import cz.stanislavcapek.workattendancerest.v1.workattendance.exception.YearsNotMatchException;
import cz.stanislavcapek.workattendancerest.v1.workattendance.web.WorkAttendanceEventHandler;
import cz.stanislavcapek.workattendancerest.v1.workattendance.web.WorkAttendanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Stanislav Čapek
 */
@Service
@AllArgsConstructor
public class ShiftPlanWorkAttendanceService {

    private final WorkAttendanceRepository workAttendanceRepository;
    private final WorkAttendanceEventHandler eventHandler;
    private final EmployeeRepository employeeRepository;


    public WorkAttendance processTemplate(WorkAttendance workAttendance, ShiftPlan plan) {

        //year validation -> Years don't match exception
        if (workAttendance.getYear() != plan.getYear()){
            throw new YearsNotMatchException("Years of work attendance and shift plan template don't match.");
        }

        // if work attendance is locked
        if (workAttendance.isLocked()) {
            String msg = String.format(
                    "Fail to overwrite the work attendance %s %d. The work attendance is marked as locked",
                    java.time.Month.of(workAttendance.getMonth()),
                    workAttendance.getYear()
            );
            throw new IllegalStateException(msg);
        }

        final WorkAttendance attendanceByEmployee = plan.getWorkAttendanceByEmployee(
                workAttendance.getMonth(),
                workAttendance.getEmployee().getAssignedId()
        );

        // prepare for update
        final Employee employee = employeeRepository.findByAssignedId(attendanceByEmployee.getEmployee().getAssignedId());
        attendanceByEmployee.setEmployee(employee);
        attendanceByEmployee.setWorkAttendanceId(workAttendance.getWorkAttendanceId());


        // Before save validation
        eventHandler.handleSaving(attendanceByEmployee);

        return workAttendanceRepository.save(attendanceByEmployee);
    }
}
