package cz.stanislavcapek.workattendancerest.v1.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkTimeFundTest {


    public static final LocalDate DATE = LocalDate.of(2020, 1, 1);

    @Test
    void whenDateMiddleOfMonth() {
        // holiday included
        final LocalDate dateInMiddleOfMonth = DATE.withDayOfMonth(DATE.lengthOfMonth() / 2);

        final double fund = WorkTimeFund.calculate(dateInMiddleOfMonth);
        assertEquals(172.5,fund);
    }

    @Test
    void calculateFor37_5Hours() {
        // holiday included
        final double fund = WorkTimeFund.calculate(DATE, 37.5);
        assertEquals(172.5,fund);
    }

    @Test
    void calculateFor38_75Hours() {
        // holiday included
        final double fund = WorkTimeFund.calculate(DATE, 38.75);
        assertEquals(178.25,fund);
    }

    @Test
    void calculateFor40Hours() {
        // holiday included
        final double fund = WorkTimeFund.calculate(DATE, 40d);
        assertEquals(184,fund);
    }

    @Test
    void calculateFor10Hours() {
        // holiday included
        final double fund = WorkTimeFund.calculate(DATE, 10d);
        assertEquals(46,fund);

    }
}