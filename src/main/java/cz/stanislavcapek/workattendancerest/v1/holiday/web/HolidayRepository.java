package cz.stanislavcapek.workattendancerest.v1.holiday.web;

import cz.stanislavcapek.workattendancerest.v1.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

/**
 * An instance of interface {@code HolidayRepository}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Holiday findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);

    @Query(value = "select case when exists (select * from holiday where year(date) = ?1) then true else false end",
            nativeQuery = true)
    boolean existsByDateYear(int year);

}