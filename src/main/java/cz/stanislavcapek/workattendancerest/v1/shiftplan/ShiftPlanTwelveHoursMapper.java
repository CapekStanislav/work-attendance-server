package cz.stanislavcapek.workattendancerest.v1.shiftplan;

import cz.stanislavcapek.workattendancerest.v1.model.WorkTimeFund;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.exception.WrongShiftTemplateFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Stanislav Čapek
 */
@Service
public class ShiftPlanTwelveHoursMapper {

    public ShiftPlan map(InputStream inputStream) {
        Workbook workbook;
        try {
            workbook = XSSFWorkbookFactory.create(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrongShiftTemplateFormatException("There is some problem in shift plan template.");
        }

        return new ShiftPlan(workbook, WorkTimeFund.TypeOfWeeklyWorkTime.MULTISHIFT_CONTINUOUS);
    };
}
