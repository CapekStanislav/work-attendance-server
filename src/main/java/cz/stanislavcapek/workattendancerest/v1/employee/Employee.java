package cz.stanislavcapek.workattendancerest.v1.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * An instance of class {@code Employee}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long employeeId;

    @NonNull
    private int assignedId;

    @NonNull
    private String firstName;

    @EqualsAndHashCode.Exclude
    @NonNull
    private String lastName;

    public void setAssignedId(int assignedId) {
        throw new IllegalStateException("Assigned ID can not be changed!");
    }

    @JsonIgnore
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    @JsonIgnore
    public String getAbbreviation() {

        return ((firstName.isEmpty() ? "" : firstName.charAt(0)) +
                (lastName.isEmpty() ? "" : lastName.substring(0, 2)))
                .toUpperCase();
    }
}
