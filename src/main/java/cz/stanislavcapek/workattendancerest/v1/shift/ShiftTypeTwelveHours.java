package cz.stanislavcapek.workattendancerest.v1.shift;


import com.fasterxml.jackson.annotation.JsonValue;

public enum ShiftTypeTwelveHours {
    /**
     * Určuje že se jedná o denní směnu, přičemž směna je odpracovaná v rozmezí
     * od půlnoci do 23:59:59 stejného dne.
     */
    DAY(12, "denní"),

    /**
     * Určuje, že se jedná o noční směnu, přičemž směna je odpracovaná v rozmezí
     * dvou dnů, tedy přes půlnoc.
     */
    NIGHT(12, "noční"),

    /**
     * Určuje, že se jedná o dovolenou, přičemž se předpokládá, že dovolená je
     * započítána v jeden den a její délka se rovná denní službě.
     */
    HOLIDAY(12, "dovolená"),

    /**
     * Určuje, že se jedná o polovinu dovolené, přičemž se předpokládá, že dovolená
     * je započítána v jeden den a její délka se rovná polovině denní službě.
     */
    HALF_HOLIDAY(6, "1/2 dovolené"),

    /**
     * Určuje, že se jedná o zdravotní službu, příčemž délka se odvíjí od
     * nastavení zaměstnavatele.
     */
    SICK_DAY(12, "zdravotní volno"),

    /**
     * Určuje, že se jedná o pracovní neschopnost, přičemž se předpokládá, že
     * je započítána jako denní služba.
     */
    INABILITY(12, "neschopnost"),

    /**
     * Určuje, že se jedná o ošetřování, přičemž délka se odvíjí od
     * nastavení zaměstnavatele.
     */
    HOME_CARE(12, "ošetřování"),

    /**
     * Určuje, že se jedná o školení započítané do pracovní doby.
     */
    TRAINING(7.5, "školení"),

    /**
     * Určuje, že se jedná přesčasovou směnu.
     */
    OVERTIME(12, "přesčas"),

    /**
     * Určuje, že se jedná o den, kdy neproběhla směna a její délka je {@code 0}.
     */
    NONE(0, "žádná"),

    /**
     * Určuje, že se jedná jinou směnu, než je uvedené ve výčtu.
     */
    OTHER(0,"jiná");

    private final double shiftLength;
    @JsonValue
    private final String name;

    ShiftTypeTwelveHours(double shiftLength, String name) {
        this.shiftLength = shiftLength;
        this.name = name;
    }

    /**
     * Vrátí buď {@link ShiftTypeTwelveHours#DAY} nebo {@link ShiftTypeTwelveHours#NIGHT} podle doby
     * odpracování směny
     *
     * @param shift odpracovaná směna
     * @return {@link ShiftTypeTwelveHours#DAY} nebo {@link ShiftTypeTwelveHours#NIGHT}
     */
    public static ShiftTypeTwelveHours resolveType(Shift shift) {
        if (shift.getStart().toLocalDate().isBefore(shift.getEnd().toLocalDate())) {
            return NIGHT;
        } else {
            return DAY;
        }
    }

    public double getShiftLength() {
        return shiftLength;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
