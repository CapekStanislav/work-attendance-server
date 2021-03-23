package cz.stanislavcapek.workattendancerest.v1.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Instance třídy {@code WorkTimeFund} slouží pro výpočet měsíčního fondu pracovního doby.
 * Doba je vypočtena na základě stanovené týdenní pracovní doby. Při výpočtu je svátek zahrnut
 * jako pracovní den.
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class WorkTimeFund {

    public enum TypeOfWeeklyWorkTime {
        STANDARD(40),
        MULTISHIFT_CONTINUOUS(37.5),
        WORSENED_CONDITIONS(37.5),
        TWOSHIFT(38.75);

        private final double fund;

        TypeOfWeeklyWorkTime(double fund) {
            this.fund = fund;
        }

        public double getFund() {
            return fund;
        }
    }

    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc pro 7.5 hodinové směny (37.5h / 5 pracovních dnů).
     *
     * @param date obdobi (rok a měsíc)
     * @return pracovní fond na měsíc
     */
    public static double calculate(LocalDate date) {
        return calculate(date, TypeOfWeeklyWorkTime.MULTISHIFT_CONTINUOUS);
    }


    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc dle zadaného druhu.
     *
     * @param date období (rok a měsíc)
     * @param type týdenní fond týdenní pracovní doby
     * @return pracovní fond na měsíc
     */
    public static double calculate(LocalDate date, TypeOfWeeklyWorkTime type) {
        return calculate(date,type.fund);

    }

    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc dle zadané délky týdenní
     * pracovní doby.
     * @param date období (rok a měsíc)
     * @param weeklyWorkTime týdenní pracovní doba
     * @return pracovní fond na měsíc
     */
    public static double calculate(LocalDate date, double weeklyWorkTime) {
        double fund;
        int weekend = 0;
        int numOfDaysInMonth = date.lengthOfMonth();
        LocalDate tempDate = date.withDayOfMonth(1);
        final double shiftLength = weeklyWorkTime / 5;

        for (int i = 0; i < numOfDaysInMonth; i++) {
            if (tempDate.getDayOfWeek() == DayOfWeek.SATURDAY || tempDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekend++;
            }
            tempDate = tempDate.plusDays(1);
        }
        fund = (numOfDaysInMonth - weekend) * shiftLength;

        return fund;
    }
}
