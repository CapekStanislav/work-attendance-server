package cz.stanislavcapek.workattendancerest.v1.shift;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.stanislavcapek.workattendancerest.v1.shift.premiumpayments.PremiumPayments;
import cz.stanislavcapek.workattendancerest.v1.shift.worktime.WorkTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours.TRAINING;

/**
 * An instance of class {@code Shift}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class Shift {

    @Id
    @GeneratedValue
    private Long shiftId;

    @NonNull
    private LocalDateTime start;

    @NonNull
    private LocalDateTime end;

    @OneToOne(cascade = CascadeType.ALL)
    @NonNull
    private WorkTime workTime;

    @OneToOne(cascade = CascadeType.ALL)
    @NonNull
    private PremiumPayments premiumPayments;

    @Enumerated
    @JsonProperty("shiftType")
    @NonNull
    private ShiftTypeTwelveHours shiftTypeTwelveHours;


    public Shift(@NonNull LocalDateTime start, @NonNull LocalDateTime end, @NonNull WorkTime workTime,
                 @NonNull PremiumPayments premiumPayments, @NonNull ShiftTypeTwelveHours shiftTypeTwelveHours) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The start of shift cannot be after the end.");
        }

        this.start = start;
        this.end = end;
        this.workTime = workTime;
        this.premiumPayments = premiumPayments;
        this.shiftTypeTwelveHours = shiftTypeTwelveHours;
        resolveDateAndTime(this.shiftTypeTwelveHours);
    }

    private void resolveDateAndTime(ShiftTypeTwelveHours type) {
        final LocalTime seven = LocalTime.of(7, 0);
        final LocalTime thirteen = LocalTime.of(13, 0);
        final LocalTime nineteen = LocalTime.of(19, 0);
        final LocalTime zero = LocalTime.of(0, 0);

        switch (type) {
            case NONE:
                this.setStart(start.with(zero));
                this.setEnd(start);
                break;
            case HALF_HOLIDAY:
            case HOLIDAY:
            case SICK_DAY:
            case HOME_CARE:
            case INABILITY:
            case DAY:
                this.setStart(start.with(seven));
                this.setEnd(start.with(nineteen));
                break;
            case NIGHT:
                this.setStart(start.with(nineteen));
                this.setEnd(start.plusDays(1).with(seven));
                break;
            case TRAINING:
                this.setStart(start.with(seven));
                this.setEnd(start.plusMinutes(((long) (TRAINING.getShiftLength() * 60))));
                break;
            case OVERTIME:
            case OTHER:
                // Start and End as same as it was set
                break;
        }
    }

    public void setShiftTypeTwelveHours(ShiftTypeTwelveHours shiftTypeTwelveHours) {
        resolveDateAndTime(shiftTypeTwelveHours);
        this.shiftTypeTwelveHours = shiftTypeTwelveHours;
    }
}
