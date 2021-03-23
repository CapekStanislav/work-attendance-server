package cz.stanislavcapek.workattendancerest.v1.shiftplan.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.employee.web.EmployeeRepository;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.XlsxTemplateFactory;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * An instance of class {@code ShiftPlanTemplateController}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@BasePathAwareController
@RequiredArgsConstructor
public class ShiftPlanTemplateController {

    private static final String FILE_EXTENSION = ".xlsx";
    private static final String FILE_NAME = "Shift plan";

    private final EmployeeRepository employeeRepository;

    @GetMapping(path = ShiftPlanTemplateLinks.CURRENT_YEAR_TEMPLATE)
    public ResponseEntity<?> getTemplateThisYear() throws IOException {
        final int thisYear = LocalDate.now().getYear();
        return getTemplateByYearForAll(thisYear);
    }

    @GetMapping(path = ShiftPlanTemplateLinks.GIVEN_YEAR_TEMPLATE)
    public ResponseEntity<?> getTemplateByYearForAll(@PathVariable Integer year) throws IOException {

        final List<Employee> employees = employeeRepository.findAll();

        final XSSFWorkbook workbook = XlsxTemplateFactory.create(employees, year);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);

            String fileName = String.format("%s %s%s", FILE_NAME, year, FILE_EXTENSION);

            final byte[] fileAsByteArray = outputStream.toByteArray();

//            final ByteArrayResource resource = new ByteArrayResource(fileAsByteArray, fileName);

            final ByteArrayFile resource = new ByteArrayFile(fileAsByteArray, "fileName", ".xlsx");

            return ResponseEntity
                    .ok()
                    .body(resource);
        }
    }

    @Value
    private static class ByteArrayFile {
        byte[] byteArray;
        String name;
        String extension;
    }

}
