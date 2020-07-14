package kr.sooragenius.forest.facility.reservation.detail.dto;

import kr.sooragenius.forest.facility.reservation.detail.domain.ReservationDetail;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationDetailDTO {
    @Data
    public static class Response {
        private Long id;
        private int totalAmount;
        private int discountAmount;
        private int feeAmount;

        public static Response of(ReservationDetail reservationDetail) {
            Response response = new Response();

            response.id = reservationDetail.getId();
            response.totalAmount = reservationDetail.getTotalAmount();
            response.discountAmount = reservationDetail.getDiscountAmount();
            response.feeAmount = reservationDetail.getFeeAmount();

            return response;
        }

        public static List<Response> of(List<ReservationDetail> reservationDetails) {
            return reservationDetails.stream().map(Response::of).collect(Collectors.toList());
        }
    }
}
