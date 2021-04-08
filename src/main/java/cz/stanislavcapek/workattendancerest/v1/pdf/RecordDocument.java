package cz.stanislavcapek.workattendancerest.v1.pdf;

import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.model.WorkTimeFund;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPayments;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;

import java.time.LocalDate;

/**
 * An instance of class {@code RecordDocument}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class RecordDocument {

    private final WorkAttendance record;
    private final String[] columns = {"den", "začátek", "konec","typ", "odprac.", "noční", "svátek", "dovolená", "víkend", "neodpracováno"};

    public RecordDocument(WorkAttendance record) {
        this.record = record;
    }

    public String getName() {
        return record.getEmployee().getFullName();
    }

    public int getYear() {
        return record.getYear();
    }

    public String getMonth() {
        return Month.valueOf(record.getMonth()).getName();
    }

    /**
     * @param rowIndex 0 base index
     * @return date of shift
     */
    public LocalDate getDate(int rowIndex) {
        final int day = rowIndex + 1;
        return record.getShifts().get(day).getStart().toLocalDate();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex >= columns.length) {
            String err = String.format("Column index is out of range (0-%s): %s", columns.length, columnIndex);
            throw new IllegalArgumentException(err);
        }
        return columns[columnIndex];
    }

    public int getRowCount() {
        return Month.getNumberOfDays(record.getMonth(), record.getYear());
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        //"den", "začátek", "konec", "typ", "odprac.", "noční", "svátek", "dovolená", "víkend", "neodprac"
        Shift shift = record.getShifts().get(rowIndex);
        final WorkTime workTime = shift.getWorkTime();
        final PremiumPayments payments = shift.getPremiumPayments();

        switch (columnIndex) {
            case 0:
                return shift.getStart().getDayOfMonth();
            case 1:
                return shift.getStart().toLocalTime();
            case 2:
                return shift.getEnd().toLocalTime();
            case 3:
                return shift.getShiftTypeTwelveHours();
            case 4:
                return workTime.getWorkedOut();
            case 5:
                return payments.getNight();
            case 6:
                return payments.getHoliday();
            case 7:
                return workTime.getHoliday();
            case 8:
                return payments.getWeekend();
            case 9:
                return workTime.getNotWorkedOut();
            default:
                throw new IllegalArgumentException("Unexpected column index: " + columnIndex);

        }
    }

    public double getWorkTimeFund() {
        return WorkTimeFund.calculate(
                getDate(0),
                record.getWeeklyWorkTime()
        );
    }

    public double getLastMonthHours() {
        return record.getPreviousMonth();
    }

    public double getNextMonthHours() {
        return (getWorkedHours() + getLastMonthHours()) - getWorkTimeFund();
    }

    public double getWorkedHours() {
        return record.getShifts()
                .stream()
                .map(Shift::getWorkTime)
                .mapToDouble(WorkTime::getWorkedOut)
                .sum();
    }
}
