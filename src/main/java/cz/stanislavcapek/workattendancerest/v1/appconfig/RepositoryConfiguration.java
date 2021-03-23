package cz.stanislavcapek.workattendancerest.v1.appconfig;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.employee.web.EmployeeEventHandler;
import cz.stanislavcapek.workattendancerest.v1.employee.web.EmployeeRepository;
import cz.stanislavcapek.workattendancerest.v1.holiday.Holiday;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPaymentsCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.web.ShiftEventHandler;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeCounter;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.http.HttpMethod;

/**
 * An instance of class {@code EmployeeRepositoryConfiguration}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Configuration
public class RepositoryConfiguration {


    @Bean
    EmployeeEventHandler employeeEventHandler(EmployeeRepository repository) {
        return new EmployeeEventHandler(repository);
    }

    @Bean
    ShiftEventHandler shiftEventHandler(final PremiumPaymentsCounter paymentsCounter,
                                        final WorkTimeCounter timeCounter) {
        return new ShiftEventHandler(paymentsCounter, timeCounter);
    }

//    @Bean
//    WorkAttendanceEventHandler workAttendanceEventHandler(ShiftFactory factory, WorkTimeSumCounter counter,
//                                                          WorkAttendanceRepository repository,
//                                                          WorkTimeCounter timeCounter,
//                                                          PremiumPaymentsCounter paymentsCounter,
//                                                          WorkAttendanceUtilService utilService) {
//        return new WorkAttendanceEventHandler(factory, counter, repository, timeCounter, paymentsCounter,utilService);
//    }

    @Autowired
    public void configure(
            RepositoryRestConfiguration repositoryRestConfiguration) {

        final ExposureConfiguration config = repositoryRestConfiguration.getExposureConfiguration();

        config.forDomainType(WorkAttendance.class).disablePutForCreation();
        config.forDomainType(Shift.class).disablePutForCreation();
        config.forDomainType(Employee.class).disablePutForCreation();

        config.forDomainType(Holiday.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.POST))
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.PATCH))
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.PUT))
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.DELETE))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.DELETE))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.POST))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.PATCH))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(HttpMethod.PUT));


    }

}
