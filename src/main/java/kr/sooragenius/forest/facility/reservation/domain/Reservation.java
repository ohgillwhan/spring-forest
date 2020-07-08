package kr.sooragenius.forest.facility.reservation.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.reservation.dto.ReservationDTO;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.member.domain.Member;
import lombok.Getter;

import javax.jnlp.PrintService;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class Reservation {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    private FacilitySchedule facilitySchedule;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private int numberOfPeople;

    private int amount;
    private int discount;
    private int fee;

    public static Reservation reserve(ReservationDTO.Request request, Member member, Facility facility) {
        List<FacilitySchedule> schedule = facility.getSchedulesBetweenDate(request.getBeginDate(), request.getEndDate());

        if(schedule.isEmpty()) {
            throw new RuntimeException("예약이 가능한 날짜가 없습니다.");
        }else if(schedule.size() != request.getBetweenDays()) {
            throw new RuntimeException("예약이 불가능한 날짜가 존재합니다.");
        }

        long notReservableCount = schedule.stream().filter(FacilitySchedule::isNotReservable).count();
        if(notReservableCount > 0L) {
            throw new RuntimeException("예약이 불가능한 날짜가 존재합니다.");
        }

        Reservation reservation = new Reservation();
        reservation.member = member;
        reservation.facility = facility;

        // TODO 금액계산 및 facilitySchedule을 N:M관계로 풀어나가야함.
//        reservation.facilitySchedule = ..; // M:M 관계로 변경해야함..
//        reservation.amount = schedule.stream().map(FacilitySchedule::getReserveAmount).reduce(0, (integer, integer2) -> integer + integer2);
//        reservation.discount = reservation.calcDiscount();
//        reservation.fee = reservation.calcFee();

        return null;
    }

    public boolean isReservableStatus() {
        return true;
    }

    public boolean isNotReservableStatus() {
        return !isReservableStatus();
    }
}
