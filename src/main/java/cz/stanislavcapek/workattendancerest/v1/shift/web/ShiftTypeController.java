package cz.stanislavcapek.workattendancerest.v1.shift.web;

import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * An instance of class {@code ShiftTypeController}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@BasePathAwareController
@AllArgsConstructor
public class ShiftTypeController {

    @GetMapping(path = "work-attendances/{id}/types")
    public @ResponseBody
    ResponseEntity<?> getAllShiftTypes() {
        return ResponseEntity.ok(ShiftTypeTwelveHours.values());
    }

}
