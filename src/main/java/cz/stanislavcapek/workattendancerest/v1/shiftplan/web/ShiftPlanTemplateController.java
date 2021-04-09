package cz.stanislavcapek.workattendancerest.v1.shiftplan.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.employee.web.EmployeeRepository;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.ShiftPlan;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.ShiftPlanTwelveHoursMapper;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.XlsxTemplateFactory;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static cz.stanislavcapek.workattendancerest.v1.shiftplan.web.ShiftPlanTemplateLinks.*;

/**
 * An instance of class {@code ShiftPlanTemplateController}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class ShiftPlanTemplateController {

    private static final String FILE_EXTENSION = ".xlsx";
    private static final String FILE_NAME = "Plan_smen";

    private final EmployeeRepository employeeRepository;

    @GetMapping(path = API_PATH + CURRENT_YEAR_TEMPLATE)
    public ResponseEntity<?> getTemplateThisYear() throws IOException {
        final int thisYear = LocalDate.now().getYear();
        return getTemplateByYearForAll(thisYear);
    }

    @GetMapping(path = API_PATH + GIVEN_YEAR_TEMPLATE)
    public ResponseEntity<?> getTemplateByYearForAll(@PathVariable Integer year) throws IOException {

        final List<Employee> employees = employeeRepository.findAll();

        final XSSFWorkbook workbook = XlsxTemplateFactory.create(employees, year);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);

            String fileName = String.format("%s_%s%s", FILE_NAME, year, FILE_EXTENSION);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileName)
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(outputStream.toByteArray());
        }
    }
}
