package cz.stanislavcapek.workattendancerest.v1.shift;

import cz.stanislavcapek.workattendancerest.v1.shift.exception.IllegalShiftLengthException;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPayments;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Instance třídy {@code TovárnaNaSměny}
 *
 * @author Stanislav Čapek
 */
@EqualsAndHashCode
@Service
public class DefaultShiftFactory implements ShiftFactory {


    private static final LocalTime SEVEN_HOUR = LocalTime.of(7, 0);
    private static final LocalTime TWELVE_HOUR = LocalTime.of(19, 0);

    private LocalDate date;

    /**
     * Defaultní konstruktor továrny, který nastaví továrnu na 1.1. aktuálního roku
     */
    public DefaultShiftFactory() {
        this(LocalDate.now().withMonth(1).withDayOfMonth(1));
    }

    /**
     * Konstruktor, který nastaví hned při vzniku nastaví na požadované datum.
     * Nezáleží na dnu v měsíci. Důležítý je rok a měsíc.
     *
     * @param date požadované datum ({@code období})
     */
    private DefaultShiftFactory(LocalDate date) {
        this.date = date.withDayOfMonth(1);
    }


    /**
     * Nastaví továrnu na nový měsíc. Pokud nebyl před voláním této metody
     * nastaven rok metodou {@link ShiftFactory#setYear(int)} použije se
     * aktuální rok.
     *
     * @param month nový měsíc (1-12)
     */
    @Override
    public void setMonth(int month) {
        this.date = date.withMonth(month);
    }

    /**
     * Získá aktuálně nastavený měsíc
     *
     * @return číslo měsíce (1-12)
     */
    @Override
    public int getMonth() {
        return date.getMonthValue();
    }

    /**
     * Nastaví továrnu na nový rok.
     *
     * @param year nový rok
     */
    @Override
    public void setYear(int year) {
        this.date = LocalDate.of(year, 1, 1);
    }

    /**
     * Získá aktuálně nastavený rok.
     *
     * @return nastavený rok
     */
    @Override
    public int getYear() {
        return date.getYear();
    }

    /**
     * Nastaví továrnu na nové období, přičemž {@code nezáleží} na zadaném dnu.
     *
     * @param period nové období (měsíc a rok)
     */
    @Override
    public void setPeriod(LocalDate period) {
        date = period;
    }


    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného datumu, která bude
     * mít defaultní začátek a délku.
     *
     * @param date datum začátku směny
     * @return nová směna
     */
    @Override
    public Shift createShift(LocalDate date) {
        return createShift(date, ShiftTypeTwelveHours.DAY);
    }

    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného datumu a typu směny.
     * Délka a záčátek směny se odvíjí od {@link ShiftTypeTwelveHours}.
     *
     * @param date                 datum začátku směny
     * @param shiftTypeTwelveHours typ požadované směny
     * @return nová směna
     */
    @Override
    public Shift createShift(LocalDate date, ShiftTypeTwelveHours shiftTypeTwelveHours) {
//        LocalDateTime start;
//        LocalDateTime end;
//        switch (shiftTypeTwelveHours) {
//            case DAY:
//            case HOLIDAY:
//            case SICK_DAY:
//            case INABILITY:
//            case HOME_CARE:
//            case HALF_HOLIDAY:
//            case OVERTIME:
//                start = LocalDateTime.of(date, SEVEN_HOUR);
//                end = start.plusHours(12);
//                break;
//            case NIGHT:
//                start = LocalDateTime.of(date, TWELVE_HOUR);
//                end = start.plusHours(12);
//                break;
//            case TRAINING:
//                start = LocalDateTime.of(date, SEVEN_HOUR);
//                end = start.plusHours(7).plusMinutes(30);
//                break;
//
//            case NONE:
//                final LocalDateTime startAndEnd = LocalDateTime.of(date, LocalTime.MIN);
//                final Shift shift = new Shift(
//                        startAndEnd,
//                        startAndEnd,
//                        new WorkTime(),
//                        new PremiumPayments(),
//                        ShiftTypeTwelveHours.NONE
//                );
//                return shift;
//            default:
//                throw new RuntimeException("Chyba při vytváření směny. Neznámý typ směny.");
//        }
        LocalDateTime start = date.atStartOfDay();

        return new Shift(
                start,
                start,
                new WorkTime(),
                new PremiumPayments(),
                shiftTypeTwelveHours
        );
    }

    @Override
    public Shift createShift(LocalDateTime start, LocalDateTime end) {
        final long length = Duration.between(start, end).toHours();
        checkShiftLength(length);
        final ShiftTypeTwelveHours type = resolveShiftType(start, end);

        final Shift shift = new Shift(
                start,
                end,
                new WorkTime(),
                new PremiumPayments(),
                type
        );

        shift.setStart(start);
        shift.setEnd(end);
        return shift;
    }

    @Override
    public Shift createShift(LocalDate date, double length) {
        checkShiftLength(length);

        LocalDateTime start = date.atTime(SEVEN_HOUR);
        int hours = (int) length;
        int minutes = (int) ((length - hours) * 60);
        final LocalDateTime end = start.plusHours(hours).plusMinutes(minutes);
        return createShift(start, end);
    }

    private ShiftTypeTwelveHours resolveShiftType(LocalDateTime start, LocalDateTime end) {
        if (start.toLocalDate().isBefore(end.toLocalDate())) {
            return ShiftTypeTwelveHours.DAY;
        } else {
            return ShiftTypeTwelveHours.NIGHT;
        }
    }

    private void checkShiftLength(double length) {
        if (length >= 24) {
            throw new IllegalShiftLengthException(length);
        }
    }


}
