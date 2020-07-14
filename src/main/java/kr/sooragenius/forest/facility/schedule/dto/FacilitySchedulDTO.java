package kr.sooragenius.forest.facility.schedule.dto;

import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.facility.schedule.enums.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacilitySchedulDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull
        private Long facilityId;
        @NotNull @NotEmpty
        private String title;
        @NotNull @NotEmpty
        private String contents;
        @NotNull
        private ScheduleType scheduleType;
        @NotNull @NotEmpty
        private DateTime date;
        @Data
        @NoArgsConstructor
        public static class DateTime {
            @NotNull
            private Integer year;
            @NotNull
            private Integer month;
            @NotNull
            private Integer day;

            public DateTime(@NotNull Integer year, @NotNull Integer month, @NotNull Integer day) {
                this.year = year;
                this.month = month;
                this.day = day;
            }


            public LocalDate convertToLocalDate() {
                return LocalDate.of(year, month, day);
            }
        }
    }
    @Data
    public static class Response {
        private Long facilityId;
        private Long id;
        private String title;
        private String contents;
        private LocalDate date;
        public Response(FacilitySchedule facilitySchedule) {
            this.facilityId = facilitySchedule.getFacility().getId();
            this.id = facilitySchedule.getId();
            this.title = facilitySchedule.getTitle();
            this.contents = facilitySchedule.getContents();
            this.date = facilitySchedule.getDate();
        }
    }
}
