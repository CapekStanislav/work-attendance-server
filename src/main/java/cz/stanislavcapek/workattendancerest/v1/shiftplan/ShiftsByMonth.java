package cz.stanislavcapek.workattendancerest.v1.shiftplan;


import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.shift.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída představuje reprezentaci směn konkrétního zaměstnance v předem stanoveném měsíci. Předpokládá se, že před
 * vytvořením instance této třídy bude zkontrolován, zda šablona obsahuje daného zaměstnance.
 *
 * @author Stanislav Čapek
 */
class ShiftsByMonth {

    private final int month;
    private final LocalDate period;
    private final int numDaysInMonth;
    private final List<String> wholeRow;
    private final int fundCell;
    private final int planCell;
    private final int lastMonthCell;
    private final Employee employee;
    private final int year;
    private List<Shift> shifts;

    /**
     * Konstruktor
     *
     * @param wholeRowByEmployee řádek strážníka
     * @param month              měsíc
     * @param employee           zaměstnanec
     * @param year               rok
     */
    ShiftsByMonth(List<String> wholeRowByEmployee, int month, Employee employee, int year) {
        this.wholeRow = wholeRowByEmployee;
        this.month = month;
        this.employee = employee;
        this.year = year;
        final int firstDay = 1;
        this.period = LocalDate.of(year, this.month, firstDay);

        int size = wholeRowByEmployee.size();
        for (int i = wholeRowByEmployee.size() - 1; i >= 0; i--) {
            if (wholeRowByEmployee.get(i).equalsIgnoreCase("")) {
                size--;
            } else {
                break;
            }
        }

        this.fundCell = size - 5;
        this.planCell = fundCell + 1;
        int diffCell = planCell + 1;
        this.lastMonthCell = diffCell + 1;
        numDaysInMonth = period.getMonth().length(period.isLeapYear());
        getListOfShifts();

    }

    /**
     * Kopírovací konstruktor
     *
     * @param toCopy instance této třídy ke kopírování
     */
    ShiftsByMonth(ShiftsByMonth toCopy) {
        this.month = toCopy.month;
        this.employee = toCopy.employee;
        this.year = toCopy.year;
        final LocalDate tempPeriod = toCopy.period;
        this.period = LocalDate.of(tempPeriod.getYear(), tempPeriod.getMonth(), tempPeriod.getDayOfMonth());
        this.numDaysInMonth = toCopy.numDaysInMonth;
        this.wholeRow = new ArrayList<>(toCopy.wholeRow);
        this.fundCell = toCopy.fundCell;
        this.planCell = toCopy.planCell;
        this.lastMonthCell = toCopy.lastMonthCell;
        this.shifts = new ArrayList<>(toCopy.shifts);
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<Shift> getShifts() {
        return this.shifts;
    }

    /**
     * Metoda vrátí hodnotu odpovídající fondu pracovní doby, tedy co musel zaměstnanec odpracovat, na daný měsíc.
     * Předpokládá se, že šablona plánu služeb obsahuje daného zaměstnance a tedy veškeré potřebné údaje.
     *
     * @return hodnotu představující fond pracovní doby
     */
    public double getWorkingTimeFund() {
        String temp = wholeRow.get(fundCell);
        return stringToDouble(temp);
    }

    /**
     * Vrátí seznam {@link Shift} odpracovaných v daném měsíci. Seznam je číslovaný od 0. Jeho velikost je
     * rovna počtu dnům v měsíci, pro který je seznam tvořen.
     *
     * @return seznam odpracovaných služeb
     */
    List<Shift> getListOfShifts() {
        if (shifts == null) {
            createListOfShifts();
        }
        return shifts;
    }

    /**
     * Metoda vrací hodnotu přecházejících hodin z minulého měsíce do aktuálního měsíce.
     *
     * @return převod z minulého měsíce
     */
    public double getFromLastMonth() {
        String temp = wholeRow.get(lastMonthCell);
        return stringToDouble(temp);
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    // private methods

    /**
     * Pomocná metoda, která vytvoří a naplní seznam směn dle druhu,
     * případně dle zadaných hodin.
     */
    private void createListOfShifts() {

        shifts = new ArrayList<>();
//        setting up the factory to correct period
        LocalDate start = LocalDate.of(this.year, this.month, 1);
        ShiftFactory factory = new DefaultShiftFactory();
        factory.setPeriod(start);

        for (int i = 3; i < numDaysInMonth + 3; i++) {
            String shiftType = wholeRow.get(i);
            if (shiftType != null) {
                ShiftTypeTwelveHours type = parseTypeOfShift(shiftType);
                if (type != null) {
                    shifts.add(factory.createShift(start, type));
                } else {
                    try {
                        double length = Double.parseDouble(shiftType);
                        shifts.add(factory.createShift(
                                start,
                                length));
                    } catch (NumberFormatException e) {
                        shifts.add(factory.createShift(start, ShiftTypeTwelveHours.NONE));
                    }
                }
            }
            start = start.plusDays(1);
        }
    }

    /**
     * Validuje zadaný den, dle měsíce.
     *
     * @param day zadaný den
     * @throws IllegalArgumentException pokud je den mimo měsíc
     */
    private void isValidDay(int day) throws IllegalArgumentException {
        if (day > shifts.size()) {
            throw new IllegalArgumentException("Zadaný den [" + day + "] se nenachází v tomto měsíci.");
        }
    }

    /**
     * Pomocná metoda namapuje zkratky ze šablony na {@link ShiftTypeTwelveHours}.
     *
     * @param type textová reprezentace druhu směny z plánu služeb
     * @return vrací {@code null} pokud neodpovídá výčtu
     */
    private ShiftTypeTwelveHours parseTypeOfShift(String type) {
        ShiftTypeTwelveHours shiftType;
        switch (type.toLowerCase()) {
            case "d":
                shiftType = ShiftTypeTwelveHours.DAY;
                break;
            case "n":
                shiftType = ShiftTypeTwelveHours.NIGHT;
                break;
            case "řd":
                shiftType = ShiftTypeTwelveHours.HOLIDAY;
                break;
            case "pd":
                shiftType = ShiftTypeTwelveHours.HALF_HOLIDAY;
                break;
            case "zv":
                shiftType = ShiftTypeTwelveHours.SICK_DAY;
                break;
            case "pn":
                shiftType = ShiftTypeTwelveHours.INABILITY;
                break;
            default:
                shiftType = null;
        }
        return shiftType;
    }

    /**
     * Převede text představující číslo např. 172,5 na hodnotu dat. typu double 172.5
     *
     * @param number textová reprezentace desetinného čísla
     * @return převedený text na double
     */
    private double stringToDouble(String number) throws NumberFormatException {
        double convertedDouble;
        if (number.contains(",")) {
            number = number.replace(",", ".");
        }
        try {
            convertedDouble = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Předaný string nelze převést na desetinné číslo");
        }
        return convertedDouble;
    }
}
