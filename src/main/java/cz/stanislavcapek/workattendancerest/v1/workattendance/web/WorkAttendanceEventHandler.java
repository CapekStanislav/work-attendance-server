package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import cz.stanislavcapek.workattendancerest.v1.workattendance.exception.InvalidPropertiesStateException;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * An instance of class {@code WorkAttendanceEventHandler}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@RepositoryEventHandler
@AllArgsConstructor
@Component
public class WorkAttendanceEventHandler {

    private final ShiftFactory factory;
    private final WorkAttendanceRepository repository;
    private final WorkAttendanceUtilService service;

    @HandleBeforeCreate
    public void handleCreation(WorkAttendance workAttendance) {
        checkOneMonthAheadCondition(workAttendance);
        checkOneMonthBackCondition(workAttendance);
        checkDuplicationCondition(workAttendance);
        checkValidState(workAttendance);
        prepare(workAttendance);
    }

    @HandleBeforeSave
    public void handleSaving(WorkAttendance workAttendance) {
        if (workAttendance.isLocked()) {
            String msg = String.format(
                    "Fail to save the work attendance %s %d. The work attendance is marked as locked",
                    java.time.Month.of(workAttendance.getMonth()),
                    workAttendance.getYear()
            );
            throw new IllegalStateException(msg);
        }
        service.setValidHoursInMonth(workAttendance);
    }

    private void checkDuplicationCondition(WorkAttendance workAttendance) {
        final boolean exists = repository.existsByYearAndMonthAndEmployee(
                workAttendance.getYear(),
                workAttendance.getMonth(),
                workAttendance.getEmployee()
        );

        if (exists) {
            final String msg = String.format(
                    "Fail to create new instance of WorkAttendance( %s %d). Same instance already exists.",
                    java.time.Month.of(workAttendance.getMonth()),
                    workAttendance.getYear()
            );
            throw new IllegalStateException(msg);
        }
    }

    private void checkOneMonthAheadCondition(WorkAttendance workAttendance) {
        final LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        final LocalDate workAttendanceDate = LocalDate.of(workAttendance.getYear(), workAttendance.getMonth(), 1);
        if (currentMonth.until(workAttendanceDate).toTotalMonths() > 1) {
            String msg = String.format(
                    "Fail to create new instance ( %s %d). Cannot create new instance more than one month ahead!",
                    workAttendanceDate.getMonth(),
                    workAttendanceDate.getYear()
            );
            throw new IllegalStateException(msg);
        }
    }

    private void checkOneMonthBackCondition(WorkAttendance workAttendance) {
        final LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        final LocalDate workAttendanceDate = LocalDate.of(workAttendance.getYear(), workAttendance.getMonth(), 1);
        if (Math.abs(currentMonth.until(workAttendanceDate).toTotalMonths()) > 1) {
            String msg = String.format(
                    "Fail to create new instance ( %s %d). Cannot create new instance more than one month back!",
                    workAttendanceDate.getMonth(),
                    workAttendanceDate.getYear()
            );
            throw new IllegalStateException(msg);
        }
    }

    /**
     * in the current spring-data-rest version Validator not working
     */
    private void checkValidState(WorkAttendance workAttendance) {

        if (workAttendance.getWeeklyWorkTime() < 0) {
            throw new InvalidPropertiesStateException("Weekly work time has to be equal or greater than 0.");
        }

        final Employee employee = workAttendance.getEmployee();
        if (employee == null) {
            throw new InvalidPropertiesStateException("Employee can not be NULL");
        }

        final int month = workAttendance.getMonth();
        if (!Month.isValidMonth(month)) {
            throw new InvalidPropertiesStateException("Month is out of range (1-12): " + month);
        }

    }

    /**
     * preparing the new instance
     *
     * @param workAttendance
     */
    private void prepare(WorkAttendance workAttendance) {
        workAttendance.setShifts(new ArrayList<>());
        fillWithDefaultShift(workAttendance);
    }


    /**
     * filling new instance with default shifts
     *
     * @param workAttendance
     */
    private void fillWithDefaultShift(WorkAttendance workAttendance) {
        final int monthNumber = workAttendance.getMonth();
        final int year = workAttendance.getYear();
        final int numberOfDays = Month.getNumberOfDays(monthNumber, year);

        List<Shift> shifts = workAttendance.getShifts();
        shifts.clear();

        for (int i = 0; i < numberOfDays; i++) {
            int day = i + 1;
            final LocalDate date = LocalDate.of(year, monthNumber, day);
            shifts.add(factory.createShift(date, ShiftTypeTwelveHours.NONE));
        }
    }
}
