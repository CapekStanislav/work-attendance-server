package cz.stanislavcapek.workattendancerest.v1.pdf;

import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.model.WorkTimeFund;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;

import javax.swing.table.TableModel;
import java.time.LocalDate;

/**
 * An instance of class {@code RecordDocument}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class RecordDocument {

    private final TableModel model;
    private final WorkAttendance record;

    public <T extends TableModel> RecordDocument(WorkAttendance record, T model) {
        this.record = record;
        this.model = model;
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
        return model.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
        return model.getColumnName(columnIndex);
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.getValueAt(rowIndex, columnIndex);
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
