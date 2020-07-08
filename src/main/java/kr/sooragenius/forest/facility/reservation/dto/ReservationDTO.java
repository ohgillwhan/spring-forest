package kr.sooragenius.forest.facility.reservation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationDTO {
    @Data
    public static class Request {
        private String memberId;
        private LocalDate beginDate;
        private LocalDate endDate;
        private int numberOfPeople;

        public long getBetweenDays() {
            return ChronoUnit.DAYS.between(beginDate, endDate);
        }
    }
}
