package cz.stanislavcapek.workattendancerest.v1.shift;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Instance rozhraní {@code ITovárnaNaSměny} představují továrnu na {@link Shift}.
 * Továrnu jde nastavit, aby vytvářela instance v určitém roce a měsíci. Není-li
 * továrna přenastavena použije se první měsíc aktuálního roku.
 *
 * @author Stanislav Čapek
 */
public interface ShiftFactory {
    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného datumu, která bude
     * mít defaultní začátek a délku.
     *
     * @param date date začátku směny
     * @return nová směna
     */
    Shift createShift(LocalDate date);

    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného datumu a typu směny.
     * Délka a záčátek směny se odvíjí od {@link ShiftTypeTwelveHours}.
     *
     * @param date                   date začátku směny
     * @param shiftTypeTwelveHours typ požadované směny
     * @return nová směna
     */
    Shift createShift(LocalDate date, ShiftTypeTwelveHours shiftTypeTwelveHours);


    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného začátku, konce. Typ
     * je odvozen dle začátku a konce směny
     *
     * @param start začátek směny
     * @param end   konec směny
     * @return nová směna
     */
    Shift createShift(LocalDateTime start, LocalDateTime end);

    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného začátku se zadanou
     * délkou v hodinách.
     *
     * @param date   začátek směny
     * @param length délka směny v hodinách
     * @return nová směna
     */
    Shift createShift(LocalDate date, double length);

    /**
     * Nastaví továrnu na nový měsíc. Pokud nebyl před voláním této metody
     * nastaven rok metodou {@link ShiftFactory#setYear(int)} použije se
     * aktuální rok.
     *
     * @param month nový měsíc (1-12)
     */
    void setMonth(int month);

    /**
     * Získá aktuálně nastavený měsíc
     *
     * @return číslo měsíce
     */
    int getMonth();

    /**
     * Nastaví továrnu na nový rok.
     *
     * @param year nový rok
     */
    void setYear(int year);

    /**
     * Získá aktuálně nastavený rok.
     *
     * @return nastavený rok
     */
    int getYear();

    /**
     * Nastaví továrnu na nové období, přičemž {@code nezáleží} na zadaném dnu.
     *
     * @param period nové období (měsíc a rok)
     */
    void setPeriod(LocalDate period);
}
