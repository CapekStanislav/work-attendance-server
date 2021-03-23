package cz.stanislavcapek.workattendancerest.v1.appconfig;

import cz.stanislavcapek.workattendancerest.v1.holiday.Holiday;
import cz.stanislavcapek.workattendancerest.v1.holiday.HolidayLoaderFromUrlService;
import cz.stanislavcapek.workattendancerest.v1.holiday.HolidayRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * An instance of class {@code HolidaysInitializerApplicationListener}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class HolidaysInitializerApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private final HolidayLoaderFromUrlService loader;
    private final HolidayRepository repository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        final int thisYear = LocalDate.now().getYear();
        if (!repository.existsByDateYear(thisYear)) {
            try {
                final List<Holiday> holidays = loader.getHolidaysFromUrl(thisYear);
                repository.saveAll(holidays);
            } catch (IOException ignore) {
            }
        }
    }
}
