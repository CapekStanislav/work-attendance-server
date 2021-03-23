package cz.stanislavcapek.workattendancerest.v1.appconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.FormattingConversionService;

import java.time.format.DateTimeFormatter;

/**
 * An instance of class {@code DateTimeFormatConfiguration}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */

@Configuration
public class DateTimeFormatConfiguration {

    private final DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();

    public DateTimeFormatConfiguration() {
        registrar.setDateFormatter(DateTimeFormatter.ISO_LOCAL_DATE);
        registrar.setDateTimeFormatter(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        registrar.setTimeFormatter(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Autowired
    public void configure(final @Qualifier("defaultConversionService") FormattingConversionService conversionService) {
        registrar.registerFormatters(conversionService);
    }
}
