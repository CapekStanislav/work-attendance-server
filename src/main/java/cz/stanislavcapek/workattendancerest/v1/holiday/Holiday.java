package cz.stanislavcapek.workattendancerest.v1.holiday;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * Instance třídy {@code Holiday}
 *
 * @author Stanislav Čapek
 */
@Value
@Entity
@NoArgsConstructor(force = true)
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long holidayId;

    @NonNull
    LocalDate date;

    @NonNull
    String name;

    public Holiday(@NonNull LocalDate date, @NonNull String name) {
        this.holidayId = -1L;
        this.date = date;
        this.name = name;
    }
}
