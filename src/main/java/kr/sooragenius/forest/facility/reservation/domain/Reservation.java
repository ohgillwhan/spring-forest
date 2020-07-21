package kr.sooragenius.forest.facility.reservation.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.enums.Discount;
import kr.sooragenius.forest.facility.reservation.dto.ReservationDTO;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
import kr.sooragenius.forest.facility.reservation.detail.domain.ReservationDetail;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.member.domain.Member;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
public class Reservation {
    @Id @GeneratedValue
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", referencedColumnName = "facility_id")
    private Facility facility;


    @OneToMany(mappedBy = "reservation", cascade = CascadeType.PERSIST)
    private List<ReservationDetail> reservationDetails;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    private Discount discount;

    private int numberOfPeople;

    private int totalAmount;
    private int discountAmount;
    private int feeAmount;
    
    private int refundAmount;

    public static Reservation reserve(ReservationDTO.Request request, Member member, Facility facility) {
        List<FacilitySchedule> schedule = facility.getSchedulesBetweenDate(request.getBeginDate(), request.getEndDate());

        if(schedule.isEmpty()) {
            throw new RuntimeException("예약이 가능한 날짜가 없습니다.");
        }else if(schedule.size() != request.getBetweenDays()) {
            throw new RuntimeException("예약이 불가능한 날짜가 존재합니다.");
        }else if(facility.hasNotDiscount(request.getDiscount())) {
            throw new RuntimeException("예약이 불가능한 할인정책 입니다.");
        }

        long notReservableCount = schedule.stream().filter(FacilitySchedule::isNotReservable).count();
        if(notReservableCount > 0L) {
            throw new RuntimeException("예약이 불가능한 날짜가 존재합니다.");
        }

        Reservation reservation = new Reservation();

        reservation.member = member;
        reservation.facility = facility;
        reservation.numberOfPeople = request.getNumberOfPeople();
        reservation.discount = request.getDiscount();
        reservation.reservationDetails = ReservationDetail.of(reservation, schedule);
        reservation.calcAmountByReservationDetails();
        reservation.updateReserveStatus();

        return reservation;
    }
    public Optional<Reservation> cancel(LocalDateTime cancelDateTime) {
        ReservationStatus beforeStatus = getStatus();
        updateCancelStatus();
        calcRefundAmount(cancelDateTime);

        Optional<Reservation> nextReservation = Optional.empty();
        if(beforeStatus == ReservationStatus.COMPLETE) {
            nextReservation = getFacility().getWaitToCompleteReserveByCancelReserve(this);
            if (nextReservation.isPresent()) {
                nextReservation.get().updateCompleteStatus();
            }
        }

        return nextReservation;
    }

    private void updateCompleteStatus() {
        this.status = ReservationStatus.COMPLETE;
    }

    private void calcRefundAmount(LocalDateTime cancelDateTime) {
        long betweenCancelDateAndMinOfReserveDate = getBetweenCancelDateAndMinOfReserveDate(cancelDateTime);
        this.refundAmount = getReservationDetails().stream().map(item -> item.calcRefundAmount(betweenCancelDateAndMinOfReserveDate)).reduce(0, Integer::sum);
    }
    public long getBetweenCancelDateAndMinOfReserveDate(LocalDateTime cancelDateTime) {
        LocalDate min = getBeginDate();
        return ChronoUnit.DAYS.between(cancelDateTime.toLocalDate(), min);
    }
    private void updateReserveStatus() {
        this.status = ReservationStatus.COMPLETE;

        for(ReservationDetail detail : getReservationDetails()) {
            FacilitySchedule facilitySchedule = detail.getFacilitySchedule();
            if(facilitySchedule.hasCompleteStatusOfReservations(detail)) {
                this.status = ReservationStatus.WAIT;
                break;
            }
        }
    }
    private void updateCancelStatus() {
        this.status = ReservationStatus.CANCEL;
    }

    private void calcAmountByReservationDetails() {
        this.totalAmount = getReservationDetails().stream().map(ReservationDetail::getTotalAmount).reduce(0, (x,y) -> x + y);
        this.discountAmount = getReservationDetails().stream().map(ReservationDetail::getDiscountAmount).reduce(0, (x,y) -> x + y);
        this.feeAmount = getReservationDetails().stream().map(ReservationDetail::getFeeAmount).reduce(0, (x,y) -> x + y);
    }

    public boolean isReservableStatus() {
        return true;
    }

    public boolean isNotReservableStatus() {
        return !isReservableStatus();
    }

    public LocalDate getBeginDate() {
        LocalDate localDate = reservationDetails.stream().map(item -> item.getFacilitySchedule().getDate()).min(LocalDate::compareTo).orElseThrow(() -> new RuntimeException("예약된 날짜가 없습니다."));
        return localDate;
    }
    public LocalDate getEndDate() {
        LocalDate localDate = reservationDetails.stream().map(item -> item.getFacilitySchedule().getDate()).max(LocalDate::compareTo).orElseThrow(() -> new RuntimeException("예약된 날짜가 없습니다."));
        return localDate;
    }
}
