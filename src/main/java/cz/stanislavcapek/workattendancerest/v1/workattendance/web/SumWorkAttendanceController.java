package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPaymentsSum;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPaymentsSumCounter;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeSum;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTimeSumCounter;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * An instance of class {@code SumWorkAttendanceController}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@BasePathAwareController
@AllArgsConstructor
public class SumWorkAttendanceController {

    private final WorkTimeSumCounter workTimeSumCounter;
    private final PremiumPaymentsSumCounter premiumPaymentsSumCounter;

    @GetMapping(path = "work-attendances/{id}/work-time-sum")
    public @ResponseBody
    ResponseEntity<?> getWorkTimeSum(@PathVariable("id") WorkAttendance workAttendance) {
        final WorkTimeSum workTimeSum = workTimeSumCounter.calculate(workAttendance);
        return ResponseEntity.ok(workTimeSum);
    }

    @GetMapping(path = "work-attendances/{id}/premium-payments-sum")
    public  @ResponseBody
    ResponseEntity<?> getPremiumPaymentsSum(@PathVariable("id") WorkAttendance workAttendance) {
        final PremiumPaymentsSum premiumPaymentsSum = premiumPaymentsSumCounter
                .calculate(workAttendance);
        return ResponseEntity.ok(premiumPaymentsSum);
    }
}
