package cz.stanislavcapek.workattendancerest.v1.shiftplan;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.model.WorkTimeFund;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.shiftplan.exception.InvalidMonthNumberException;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

/**
 * An instance of class {@code ShiftPlan} represents xlsx template file for planning shifts.
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@EqualsAndHashCode
@ToString
public class ShiftPlan {

    @ToString.Exclude
    private final Workbook workbook;
    private final WorkTimeFund.TypeOfWeeklyWorkTime typeOfWeeklyWorkTime;
    @ToString.Exclude
    private final Map<Integer, Map<Integer, WorkAttendance>> shiftsInYear = new TreeMap<>();
    private final int year;
    private Set<Integer> employeeIds;

    /**
     * Map {@code <Měsíc, Map<ID,WorkAttendance>> }
     */

    public ShiftPlan(Workbook workbook, WorkTimeFund.TypeOfWeeklyWorkTime typeOfWeeklyWorkTime) {
        this.workbook = workbook;
        this.typeOfWeeklyWorkTime = typeOfWeeklyWorkTime;
        this.year = getYear();
        loadAllPlan();
    }

    /**
     * Metoda vrátí číslo roku v šabloně.
     *
     * @return číslo roku v šabloně, {@code nenalezeno} = 0
     */
    public int getYear() {
        Cell yearCell = workbook.getSheetAt(0).getRow(0).getCell(0);
        if (yearCell != null) {
            if (yearCell.getCellType() == NUMERIC) {
                return (int) yearCell.getNumericCellValue();
            }
        }
        return 0;
    }

    /**
     * @param monthNum měsíc
     * @param id       id zaměstnance
     * @return evidence směn
     * @throws IllegalArgumentException pokud hodnota měsíce není v rozmezí 1-12
     * @throws IllegalArgumentException pokud se id zaměstnance nenachází v daném měsíci
     */
    public WorkAttendance getWorkAttendanceByEmployee(int monthNum, int id) {
        if (!isValidMonth(monthNum)) {
            throw new InvalidMonthNumberException(monthNum);
        }
        if (!isEmployee(id, monthNum)) {
            final String s = String.format("Zaměstnanec s id %d se nenachází v zadaném měsíci.", id);
            throw new IllegalArgumentException(s);
        }
        return shiftsInYear.get(monthNum).get(id);
    }

    /**
     * @param monthNum měsíc
     * @return Mapu {@code <ID, WorkAttendance>}
     * @throws IllegalArgumentException pokud hodnota měsíce není v rozmezí 1-12
     */
    public Map<Integer, WorkAttendance> getWorkAttendancesByMonth(int monthNum) {
        if (!isValidMonth(monthNum)) {
            throw new InvalidMonthNumberException(monthNum);
        }
        return shiftsInYear.get(monthNum);
    }

    /**
     * @param monthNum měsíc
     * @param id       zaměstnance
     * @return seznam přesčasů
     */
    public WorkAttendance getWorkAttendanceOvertime(int monthNum, int id) {
        if (!isValidMonth(monthNum)) {
            throw new InvalidMonthNumberException(monthNum);
        }
        if (!isEmployee(id, monthNum)) {
            final String s = String.format("Zaměstnanec s id %d se nenachází v zadaném měsíci.", id);
            throw new IllegalArgumentException(s);
        }

        List<Shift> overtimesByMonth;
        try {
            overtimesByMonth = new OvertimesByMonth(workbook, monthNum, id).getOvertimes();
        } catch (Exception e) {
            overtimesByMonth = new ArrayList<>();
        }
        return convertToRecord(overtimesByMonth, getYear(), monthNum, id);
    }

    /**
     * Zjistí, jestli se zaměstnanec nachází v celém plánu
     * směn.
     *
     * @param id označení zaměstnance
     * @return {@code true} zaměstnanec se nachází v plánu
     * {@code false} zaměstnanec se nenachází v plánu
     */
    public boolean isEmployee(int id) {
        return employeeIds.contains(id);
    }

    /**
     * Zjistí, jestli se zaměstnanec nachází v konkrétním měsíci.
     *
     * @param id       označení zaměstnance
     * @param monthNum měsíc ve kterém hledáme (1-12)
     * @return {@code true} zaměstnanec se nachází v měsíci
     * {@code false} zaměstnanec se nenachází v měsíci
     * @throws IllegalArgumentException měsíc je mimo požadovaný rozsah
     */
    public boolean isEmployee(int id, int monthNum) throws IllegalArgumentException {
        if (!isValidMonth(monthNum)) {
            throw new InvalidMonthNumberException(monthNum);
        }
        return shiftsInYear.get(monthNum).containsKey(id);
    }

    /**
     * Pomocná metoda pro vytvoření zaměstnance dle jeho ID
     *
     * @param id zaměstnance
     * @return nová instance zaměstnance
     */
    public Employee getEmployee(int id) {
        final String name = getEmployeeName(id);
        final String[] split = name.split(" ");

        return new Employee(
                id,
                split[0],
                split[1]
        );
    }

    public Set<Integer> getEmployeeIds() {
        return employeeIds;
    }

    /**
     * Metoda validuje zadaný měsíc, který musí být v rozmezí 1 - 12
     *
     * @param num číslo měsíce
     * @return nachází se v požadovaném rozmezí
     */
    private boolean isValidMonth(int num) {
        return Month.isValidMonth(num);
    }

    /**
     * Vytvoří za jednotlivé měsíce v roce směny pro jednotlivé zaměstnance.
     * 1 měsíc -> (id, směny) * počet strážníků
     */
    private void loadAllPlan() {
        employeeIds = new TreeSet<>();
        for (int i = 1; i <= 12; i++) {
            int[] ids = getEmployeeIdByMonth(i);
            Map<Integer, WorkAttendance> byMonth = new TreeMap<>();
            final int numOfEmployees = getNumberOfEmployees(i);
            for (int j = 0; j < numOfEmployees; j++) {
                int id = ids[j];
                employeeIds.add(id);
                ShiftsByMonth shiftsByMonth = new ShiftsByMonth(
                        getWholeRowByEmployee(id, i),
                        i,
                        getEmployee(id),
                        this.year
                );
                WorkAttendance workAttendance = convertToRecord(shiftsByMonth);
                byMonth.put(id, workAttendance);
            }
            shiftsInYear.put(i, byMonth);
        }
    }

    private WorkAttendance convertToRecord(ShiftsByMonth shiftsByMonth) {
        // have to be recalculate
        final int nextMonth = 0;

        final WorkAttendance workAttendance = new WorkAttendance();
        workAttendance.setEmployee(shiftsByMonth.getEmployee());
        workAttendance.setYear(shiftsByMonth.getYear());
        workAttendance.setMonth(shiftsByMonth.getMonth());

        workAttendance.setLocked(false);
        workAttendance.setWeeklyWorkTime(WorkTimeFund.TypeOfWeeklyWorkTime.MULTISHIFT_CONTINUOUS.getFund());

        workAttendance.setMonthFund(shiftsByMonth.getWorkingTimeFund());
        workAttendance.setPreviousMonth(shiftsByMonth.getFromLastMonth());
        workAttendance.setNextMonth(nextMonth);
        workAttendance.setShifts(shiftsByMonth.getShifts());

        return workAttendance;
    }

    private WorkAttendance convertToRecord(List<Shift> overtimes, int year, int month, int id) {

        // FIXME: 18.01.2021 suppressed error
        return null;
//        return new WorkAttendance(
//                employee,
//                month,
//                year,
//                typeOfWeeklyWorkTime,
//                lastMonth,
//                overtimes
//        );
    }

    /**
     * Získá ID u zaměstnanců vygenerovaných v šabloně v zadaném měsíci.
     *
     * @param month měsíc ve kterém hledáme
     * @return seznam ID vygenerovaných strážníků
     */
    private int[] getEmployeeIdByMonth(int month) {
        final int numberOfEmployees = getNumberOfEmployees(month);
        final int[] ids = new int[numberOfEmployees];
        month = month - 1;

        Sheet sheet = workbook.getSheetAt(month);
        for (int i = 0; i < numberOfEmployees; i++) {
            Row row = sheet.getRow(2 + i);
            Cell cell = row.getCell(2);
            ids[i] = (int) cell.getNumericCellValue();
        }
        return ids;
    }

    /**
     * Pomocná metoda, která vrací počet vygenerovaných zaměstnanců v šabloně
     * v daný měsíc.
     *
     * @param month 1-12
     * @return počet zaměstnanců
     */
    private int getNumberOfEmployees(int month) {
        month = month - 1;

        Sheet sheet = workbook.getSheetAt(month);

        int count = 0;
        int rowIndex = 2;
        final Row sheetRow = sheet.getRow(rowIndex);
        Cell cell = null;
        if (sheetRow != null) {
            cell = sheetRow.getCell(2);
        }

        while (cell != null && cell.getNumericCellValue() > 0) {
            count++;
            rowIndex++;
            final Row row = sheet.getRow(rowIndex);
            if (row != null) {
                cell = row.getCell(2);
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * Vrátí celý řádek, dle zadaného ID zaměstnance a měsíce (1-12).
     *
     * @param id    číslo zaměstnance
     * @param month měsíc (list) ve kterém hledám (1-12)
     * @return pole s textovou reprezentací celého řádku
     * @throws IllegalArgumentException měsíc je mimo rozmezí
     */
    private List<String> getWholeRowByEmployee(int id, int month) throws IllegalArgumentException {
        List<String> wholeRow;

        if (!isValidMonth(month)) {
            throw new InvalidMonthNumberException(month);
        }

        int employeeRow = getEmployeeRow(id);

        Row row = workbook.getSheetAt(month - 1).getRow(employeeRow);
        DataFormatter formatter = new DataFormatter();

        List<String> finalWholeRow = new ArrayList<>();
        row.forEach(cell -> {
            if (cell.getCellType() == FORMULA) {
                if (cell.getCachedFormulaResultType() == NUMERIC) {
                    finalWholeRow.add(cell.getNumericCellValue() + "");
                }
            } else {
                String cellValue = formatter.formatCellValue(cell);
                finalWholeRow.add(cellValue);
            }
        });
        wholeRow = finalWholeRow;

        return wholeRow;
    }

    /**
     * Vrátí číslo řádku (0 based) ve kterém se nachází zaměstnanec, vyhledaný dle ID.
     *
     * @param id číslo zaměstnance, kterého hledám
     * @return celé číslo řádku, nenalezeno = 0
     */
    private int getEmployeeRow(int id) {
        final int[] i = {0};
        final int[] employeeRow = {0};

        Sheet sheet = workbook.getSheetAt(0);
        sheet.forEach(row -> {
            XSSFCell cell = (XSSFCell) row.getCell(2);
            if (cell != null) {
                if (cell.getCellType() == NUMERIC) {
                    if ((int) cell.getNumericCellValue() == id) {
                        employeeRow[0] = i[0];
                    }
                }
            }
            i[0]++;
        });
        return employeeRow[0];
    }

    /**
     * Získá jméno zaměstnance vygenerovaného v šabloně
     *
     * @param id číslo zaměstnance
     * @return String jméno zaměstnance,  prázdný String, pokud není nalezen strážník s odpovídajícím služebním
     * číslem
     */
    private String getEmployeeName(int id) {
        String name = "";
        int row = getEmployeeRow(id);

        if (row != 0) {
            Cell cell = workbook.getSheetAt(0).getRow(row).getCell(0);
            if (cell != null) {
                name = cell.getStringCellValue();
            }

        }
        return name;
    }


}
