package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.workattendance.SimplerWorkAttendance;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * An instance of interface {@code WorkAttendanceRepository}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@RepositoryRestResource(
        collectionResourceRel = "work-attendances",
        itemResourceRel = "work-attendance",
        path = "work-attendances",
        excerptProjection = SimplerWorkAttendance.class
)
public interface WorkAttendanceRepository extends JpaRepository<WorkAttendance, Long> {

    List<WorkAttendance> findByYearAndMonth(int year, int month);

    WorkAttendance findByYearAndMonthAndEmployee(int year, int month, Employee employee);

    boolean existsByYearAndMonthAndEmployee(int year, int month, Employee employee);
}