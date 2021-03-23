package cz.stanislavcapek.workattendancerest.v1.employee.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * An instance of interface {@code EmployeeRepository}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByAssignedId(int assignedId);

    boolean existsEmployeeByAssignedId(int assignedId);

}
