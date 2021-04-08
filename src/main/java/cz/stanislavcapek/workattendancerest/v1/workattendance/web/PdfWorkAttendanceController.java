package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.pdf.RecordDocument;
import cz.stanislavcapek.workattendancerest.v1.pdf.WorkingTimeRecordPdfFactory;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@RestController
@AllArgsConstructor
public class PdfWorkAttendanceController {
    private static final String BASE_PATH = "api/v1/";
    private WorkAttendanceRepository repository;

    @GetMapping(path = BASE_PATH + "work-attendances/{id}/generate")
    public @ResponseBody
    ResponseEntity<?> getWorkTimeSum(@PathVariable("id") WorkAttendance workAttendance) throws IOException {

        final PDDocument document = WorkingTimeRecordPdfFactory.createRecordPDDocument(new RecordDocument(workAttendance));
        String extension = ".pdf";
        final Employee employee = workAttendance.getEmployee();
        String fileName = String.format(
                "Evidence_%s_%s_%d-%d%s",
                employee.getFirstName(),
                employee.getLastName(),
                workAttendance.getYear(),
                workAttendance.getMonth(),
                extension
        );

        byte[] resource;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try {
                document.save(outputStream);
                resource = outputStream.toByteArray();
            } finally {
                document.close();
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,  "filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
