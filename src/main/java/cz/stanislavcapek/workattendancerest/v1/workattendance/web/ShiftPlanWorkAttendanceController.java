package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.employee.web.EmployeeRepository;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.ShiftPlan;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.ShiftPlanTwelveHoursMapper;
import cz.stanislavcapek.workattendancerest.v1.workattendance.ShiftPlanWorkAttendanceService;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@BasePathAwareController
@AllArgsConstructor
public class ShiftPlanWorkAttendanceController {

    private final ShiftPlanTwelveHoursMapper mapper;
    private final WorkAttendanceRepository workAttendanceRepository;
    private final ShiftPlanWorkAttendanceService service;

    @PostMapping(path = "work-attendances/{id}/processTemplate")
    public ResponseEntity<?> processTemplate(
            @PathVariable("id")WorkAttendance workAttendance,
            @RequestParam("file") MultipartFile template,
            PersistentEntityResourceAssembler assembler
    ) throws IOException {

        final ShiftPlan plan = mapper.map(template.getInputStream());
        final WorkAttendance result = service.processTemplate(workAttendance,plan);

        return ResponseEntity.ok(assembler.toModel(result));
    }
}
