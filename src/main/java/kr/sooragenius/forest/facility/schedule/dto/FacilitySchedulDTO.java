package kr.sooragenius.forest.facility.schedule.dto;

import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.facility.schedule.enums.ScheduleType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FacilitySchedulDTO {
    @Data
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
        private DateTime beginTime;
        @NotNull @NotEmpty
        private DateTime endTime;
        @Data
        public class DateTime {
            @NotNull
            private Integer year;
            @NotNull
            private Integer month;
            @NotNull
            private Integer day;
            @NotNull
            private Integer hour;
            @NotNull
            private Integer minute;

            public LocalDateTime convertToLocalDateTime() {
                return LocalDateTime.of(year, month, day, hour, minute);
            }
        }
    }
    @Data
    public static class Response {
        private Long facilityId;
        private Long id;
        private String title;
        private String contents;
        private LocalDateTime beginTime;
        private LocalDateTime endTime;
        public Response(FacilitySchedule facilitySchedule) {
            this.facilityId = facilitySchedule.getFacility().getId();
            this.id = facilitySchedule.getId();
            this.title = facilitySchedule.getTitle();
            this.contents = facilitySchedule.getContents();
            this.beginTime = facilitySchedule.getBeginTime();
            this.endTime = facilitySchedule.getEndTime();
        }
    }
}
