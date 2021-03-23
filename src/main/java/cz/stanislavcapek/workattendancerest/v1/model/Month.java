package cz.stanislavcapek.workattendancerest.v1.model;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * An instance of enum {@code Month}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public enum Month {

    JANUARY("leden", 1),
    FEBRUARY("unor", 2),
    MARCH("březen", 3),
    APRIL("duben", 4),
    MAY("květen", 5),
    JUNE("červen", 6),
    JULY("červenec", 7),
    AUGUST("srpen", 8),
    SEPTEMBER("září", 9),
    OCTOBER("říjen", 10),
    NOVEMBER("listopad", 11),
    DECEMBER("prosinec", 12);

    private String name;
    private int number;

    Month(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public static Month valueOf(int number) {
        return Arrays.stream(values())
                .filter(month -> month.number == number)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static int getNumberOfDays(Month month, int year) {
        final int dayOfMonth = 1;
        final LocalDate date = LocalDate.of(year, month.number, dayOfMonth);
        return date.getMonth().length(date.isLeapYear());
    }

    public static int getNumberOfDays(int month, int year) {
        if (!isValidMonth(month)) {
            throw new IllegalArgumentException("Month number is out of range (1-12)");
        }
        return getNumberOfDays(Month.valueOf(month), year);
    }

    /**
     * Metoda validuje zadaný měsíc, který musí být v rozmezí 1 - 12
     *
     * @param month číslo měsíce
     * @return nachází se v požadovaném rozmezí
     */
    public static boolean isValidMonth(int month) {
        return month > 0 && month < 13;
    }
}
