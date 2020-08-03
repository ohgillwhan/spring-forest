package kr.sooragenius.forest.facility.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.sooragenius.forest.facility.enums.Discount;
import kr.sooragenius.forest.facility.reservation.detail.dto.ReservationDetailDTO;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String memberId;
        private Long facilityId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate beginDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Discount discount;
        private int numberOfPeople;

        public long getBetweenDays() {
            return ChronoUnit.DAYS.between(beginDate, endDate);
        }
    }

    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private ReservationStatus status;
        private Discount discount;
        private int numberOfPeople;
        private int totalAmount;
        private int discountAmount;
        private int feeAmount;

        private List<ReservationDetailDTO.Response> reservationDetails;

        public static Response of(Reservation reservation) {
            Response response = Response.builder()
                    .id(reservation.getId())
                    .status(reservation.getStatus())
                    .discount(reservation.getDiscount())
                    .numberOfPeople(reservation.getNumberOfPeople())
                    .totalAmount(reservation.getTotalAmount())
                    .discountAmount(reservation.getDiscountAmount())
                    .feeAmount(reservation.getFeeAmount())
                    .reservationDetails(ReservationDetailDTO.Response.of(reservation.getReservationDetails()))
                    .build();

            return response;
        }
    }
}
