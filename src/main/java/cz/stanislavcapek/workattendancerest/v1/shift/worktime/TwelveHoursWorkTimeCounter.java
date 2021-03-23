package cz.stanislavcapek.workattendancerest.v1.shift.worktime;

import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * An instance of class {@code TwelveHoursShiftWorkingTimeCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
public class TwelveHoursWorkTimeCounter implements WorkTimeCounter {

    /**
     * @param shift směna pro kterou se pracovní doba počítá
     * @throws IllegalStateException pro neznámý typ směn {@link ShiftTypeTwelveHours}
     */
    @Override
    public void calculate(Shift shift) {
        final WorkTime workTime = shift.getWorkTime();
        final LocalDateTime start = shift.getStart();
        final LocalDateTime end = shift.getEnd();
        final ShiftTypeTwelveHours shiftTypeTwelveHours = shift.getShiftTypeTwelveHours();

        final Duration length = Duration.between(start, end).abs();
        final double inHours = length.toMinutes() / 60d;
        workTime.setLength(inHours);

        switch (shiftTypeTwelveHours) {
            case HOME_CARE:
            case INABILITY:
            case SICK_DAY:
                setWorkingTime(workTime, 0d, 12d, 0d);
                break;
            case HOLIDAY:
                setWorkingTime(workTime, 0d, 0d, 12d);
                break;
            case HALF_HOLIDAY:
                setWorkingTime(workTime, 6d, 0d, 6d);
                break;
            case NIGHT:
            case DAY:
            case TRAINING:
            case OVERTIME:
                setWorkingTime(workTime, inHours, 0d, 0d);
                break;
            case NONE:
                setWorkingTime(workTime, 0d, 0d, 0d);
                break;
            default:
                throw new IllegalStateException("Unknown shift type: " + shiftTypeTwelveHours);
        }

    }

    private void setWorkingTime(WorkTime workTime, double odprac, double neodprac, double dovol) {
        workTime.setWorkedOut(odprac);
        workTime.setNotWorkedOut(neodprac);
        workTime.setHoliday(dovol);
    }

}
