package cz.stanislavcapek.workattendancerest.v1.workattendance;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.shift.DefaultShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.Shift;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftFactory;
import cz.stanislavcapek.workattendancerest.v1.shift.ShiftTypeTwelveHours;
import cz.stanislavcapek.workattendancerest.v1.workattendance.web.WorkAttendanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkAttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkAttendanceRepository repository;

    @Test
    void whenWorkAttendanceFindThenReturnWorktimeSumarization() throws Exception {

        final WorkAttendance workAttendance = new WorkAttendance();
        workAttendance.setWorkAttendanceId(1L);
        workAttendance.setPreviousMonth(10);
        workAttendance.setNextMonth(0);
        workAttendance.setMonth(1);
        workAttendance.setWeeklyWorkTime(40);
        workAttendance.setEmployee(new Employee(1, "Pepa", "Novák"));
        workAttendance.setYear(2020);
        workAttendance.setShifts(getShiftList());

        // mocking repository behaviour
        when(repository.findById(1L)).thenReturn(Optional.of(workAttendance));

        verify(repository,times(1)).findById(1l);

        mockMvc.perform(get("/work-attendances/{id}/work-time-sum", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenWorkAttendanceNotFoundThenReturnStatusNotFound() throws Exception {

        when(repository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/work-attendances/{id}/work-time-sum", 2L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private List<Shift> getShiftList() {
        final ShiftFactory factory = new DefaultShiftFactory();
        final LocalDate firstDate = LocalDate.of(2020, 1, 1);
        final ArrayList<Shift> list = new ArrayList<>();

        // 7 day shifts
        for (int i = 0; i < 7; i++) {
            list.add(
                    factory.createShift(firstDate.plusDays(i))
            );
        }

        // 4 holiday shifts
        for (int i = 7; i < 11; i++) {
            list.add(
                    factory.createShift(firstDate.plusDays(i), ShiftTypeTwelveHours.HOLIDAY)
            );
        }

        // 3 inability shifts
        for (int i = 11; i < 14; i++) {
            list.add(
                    factory.createShift(firstDate.plusDays(i), ShiftTypeTwelveHours.INABILITY)
            );
        }
        return list;
    }
}