package kr.sooragenius.forest.facility.domain;

import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacilityTest {

    @Test
    void getSchedulesBetweenDate() {
        List<FacilitySchedule> schedules = new ArrayList<>();
        for(int i = 1; i<=15; i++) {
            schedules.add(FacilitySchedule.builder().date(LocalDate.of(2010, 01, i)).build());
        }

        Facility facility = Facility.builder()
                .facilitySchedules(schedules)
                .build();

        LocalDate beginDate = LocalDate.of(2010,01,02);
        LocalDate endDate = LocalDate.of(2010,01,07);


        List<FacilitySchedule> schedulesBetweenDate = facility.getSchedulesBetweenDate(beginDate, endDate);

        assertEquals(15, facility.getFacilitySchedules().size());
        assertEquals(5, schedulesBetweenDate.size());

        int index = 2;
        for(FacilitySchedule facilitySchedule : schedulesBetweenDate) {
            assertEquals(index, facilitySchedule.getDate().getDayOfMonth());

            index++;
        }
    }
}