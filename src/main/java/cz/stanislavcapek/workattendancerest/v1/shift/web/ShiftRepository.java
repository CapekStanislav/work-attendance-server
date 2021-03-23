package cz.stanislavcapek.workattendancerest.v1.shift.web;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * An instance of interface {@code ShiftRepository}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@RepositoryRestResource(exported = false)
public interface ShiftRepository extends JpaRepository<Shift, Long> {

}
