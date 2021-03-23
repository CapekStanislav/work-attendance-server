package cz.stanislavcapek.workattendancerest.v1.employee.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.employee.exception.EmployeeAlreadyExistException;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * An instance of class {@code EmployeeEventHandler}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@RepositoryEventHandler
@AllArgsConstructor
public class EmployeeEventHandler {

    private final EmployeeRepository repository;

    @HandleBeforeCreate
    public void handleEmployeeCreate(Employee employee) {
        final boolean exists = repository.existsEmployeeByAssignedId(employee.getAssignedId());

        if (exists) {
            String message = String.format("Employee with assigned ID: %s already exists. " +
                    "Can not create new employee.", employee.getAssignedId());
            throw new EmployeeAlreadyExistException(message);
        }
    }

    @HandleBeforeSave
    public void handleEmployeeSave(Employee employee) {

        final Employee employeeFromDb = repository.findByAssignedId(employee.getAssignedId());

        if (employeeFromDb != null) {
            if (employee.getEmployeeId() != employeeFromDb.getEmployeeId()) {
                String message = String.format(
                        "Employee with same ID: %s already exist. Can not alter employee.", employee.getAssignedId()
                );
                throw new EmployeeAlreadyExistException(message);
            }
        }


    }

}
