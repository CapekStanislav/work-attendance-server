package cz.stanislavcapek.workattendancerest.v1.shiftplan;

import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.shift.service.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.exception.InvalidFormatXslxExeption;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.exception.InvalidMonthNumberException;
import org.apache.poi.ss.usermodel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Třída představuje reprezentaci přesčasových směn konkrétního zaměstnance ve stanoveném měsíci.
 * Ty jsou načteny ze šablony, resp. z {@link ShiftPlan} z předem určené sekce.
 *
 * @author Stanislav Čapek
 */
class OvertimesByMonth {

    List<Shift> overtimes = new ArrayList<>();

    private final Sheet sheet;

    /**
     * Konstruktor
     *
     * @param workbook excelový plan směn
     * @param month    číslo měsíce v rozmezí 1-12
     * @param id       číslo zaměstnance
     * @throws InvalidFormatXslxExeption v případě, kdy se nepodaří nalézt
     *                                   v šabloně sekci s přesčasy
     * @throws IllegalArgumentException  měsíc je mimo rozmezí
     */
    OvertimesByMonth(Workbook workbook, int month, int id) {
        if (!Month.isValidMonth(month)) {
            throw new InvalidMonthNumberException(month);
        }

        month--;
        sheet = workbook.getSheetAt(month);
        String overtimesHeader = sheet.getRow(15).getCell(0).getStringCellValue();
        if (!overtimesHeader.equalsIgnoreCase("přesčasy")) {
            throw new InvalidFormatXslxExeption("Nepodařilo se nalézt v šabloně sekci s přesčasy.");
        }

        try {
            loadOvertimesById(id);
        } catch (Exception e) {
            throw new InvalidFormatXslxExeption("Chyba při čtení záznamu přesčasů.", e);
        }
    }

    /**
     * Vrací kopii seznamu přesčasů.
     *
     * @return seznam přečasů
     */
    List<Shift> getOvertimes() {
        return this.overtimes;
    }

    /**
     * Metoda projde sekci přesčasů v plánu služeb a pro zadané číslo
     * zaměstnance vyhledá a vytvoří seznam přesčasů.
     *
     * @param id číslo zaměstnance
     */
    private void loadOvertimesById(int id) {
        int indexOfRow = 17;
        Row entryRow = sheet.getRow(indexOfRow);
        ShiftFactory factory = new DefaultShiftFactory();

        while (entryRow != null) {
            final int datePos = 1;
            final int startTimePos = 2;
            final int endTimePos = 3;
            final int idPos = 4;
            final Cell entryDate = entryRow.getCell(datePos);
            final Cell entryStart = entryRow.getCell(startTimePos);
            final Cell entryEnd = entryRow.getCell(endTimePos);
            final Cell entryId = entryRow.getCell(idPos);

            // checking empty values - true skip the row
            if (entryDate == null || entryStart == null || entryEnd == null || entryId == null) {
                entryRow = sheet.getRow(++indexOfRow);
                continue;
            }

            final int idCellValue = (int) entryId.getNumericCellValue();
            if (id != idCellValue) {
                entryRow = sheet.getRow(++indexOfRow);
                continue;
            }

            final Calendar calendar = DateUtil.getJavaCalendar(entryDate.getNumericCellValue());
            final LocalTime startTime = parseTimeFromString(entryStart);
            final LocalTime endTime = parseTimeFromString(entryEnd);

            final LocalDate date = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            );


            factory.setPeriod(date);

            LocalDateTime start = LocalDateTime.of(date, startTime);
            LocalDateTime end = LocalDateTime.of(date, endTime);

            if (startTime.isAfter(endTime)) {
                end = end.plusDays(1);
            }

            final Shift shift = factory.createShift(start.toLocalDate(), ShiftTypeTwelveHours.OVERTIME);
            shift.setStart(start);
            shift.setEnd(end);
            this.overtimes.add(shift);

            entryRow = sheet.getRow(++indexOfRow);
        }
    }

    /**
     * Z buňky obsahujicí čas vytvoří pole [hodiny,minuty].
     *
     * @param entryTime buňka obsahující čas
     * @return časový údaj, {@link LocalTime#MIN} pokud se nepodaři extrahovat
     * čas z buňky
     */
    private LocalTime parseTimeFromString(Cell entryTime) {
        switch (entryTime.getCellType()) {
            case STRING:
                final String[] split = entryTime.getStringCellValue().split(":");
                final int hour = Integer.parseInt(split[0]);
                final int minute = Integer.parseInt(split[1]);
                return LocalTime.of(hour, minute);

            case NUMERIC:
                final Date date = entryTime.getDateCellValue();
                return LocalTime.of(date.getHours(), date.getMinutes());

            default:
                return LocalTime.MIN;
        }
    }

}
