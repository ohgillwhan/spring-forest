package kr.sooragenius.forest.facility.reservation.detail.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.facility.schedule.enums.ScheduleType;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
public class ReservationDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_schedule_Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private FacilitySchedule facilitySchedule;

    private int totalAmount;
    private int discountAmount;
    private int feeAmount;
    private int refundAmount;

    public static ReservationDetail of(Reservation reservation, FacilitySchedule facilitySchedule) {
        ReservationDetail reservationDetail = new ReservationDetail();

        reservationDetail.reservation = reservation;
        reservationDetail.facilitySchedule = facilitySchedule;

        reservationDetail.calcAmounts();

        return reservationDetail;
    }

    public static List<ReservationDetail> of(Reservation reservation, List<FacilitySchedule> facilitySchedules) {
        return facilitySchedules.stream().map(item -> of(reservation, item)).collect(Collectors.toList());
    }


    private void calcAmounts() {
        if(facilitySchedule.isNotReservableStatus()) {
            throw new RuntimeException(facilitySchedule.getDate()+" 휴관일입니다.");
        }

        this.totalAmount = getFacilitySchedule().getReserveAmount();
        this.discountAmount = getTotalAmount() / 100 * getReservation().getDiscount().getPercent();
        this.feeAmount = getTotalAmount() - getDiscountAmount();
    }

    public int calcRefundAmount(long betweenCancelDateAndMinOfReserveDate) {
        if(betweenCancelDateAndMinOfReserveDate == 1L) {
            refundAmount = feeAmount / 100 * 40;
        }else if(betweenCancelDateAndMinOfReserveDate <= 7L) {
            refundAmount = feeAmount / 100 * 60;
        }else if(betweenCancelDateAndMinOfReserveDate <= 14L) {
            refundAmount = feeAmount / 100 * 80;
        }else {
            refundAmount = feeAmount;
        }

        return refundAmount;
    }
}
