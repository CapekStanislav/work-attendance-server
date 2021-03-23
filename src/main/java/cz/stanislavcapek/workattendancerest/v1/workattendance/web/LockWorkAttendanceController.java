package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * An instance of class {@code LockWorkAttendanceController}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@BasePathAwareController
@AllArgsConstructor
public class LockWorkAttendanceController {

    private final WorkAttendanceRepository repository;

    @PostMapping(path = "work-attendances/{id}/lock")
    public @ResponseBody
    ResponseEntity<?> lockWorkAttendance(@PathVariable("id") WorkAttendance workAttendance,
                                         @RequestBody LockStatus status,
                                         PersistentEntityResourceAssembler assembler) {

        workAttendance.setLocked(status.isLocked());
        final WorkAttendance saved = repository.save(workAttendance);
        final PersistentEntityResource model = assembler.toFullResource(saved);
        return ResponseEntity.ok(model);
    }

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    private static class LockStatus {
        boolean locked;
    }
}
