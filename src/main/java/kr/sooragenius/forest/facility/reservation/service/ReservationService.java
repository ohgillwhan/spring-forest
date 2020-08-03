package kr.sooragenius.forest.facility.reservation.service;

import com.sun.tools.corba.se.idl.constExpr.GreaterEqual;
import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.infra.FacilityRepository;
import kr.sooragenius.forest.facility.reservation.detail.domain.ReservationDetail;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.reservation.dto.ReservationDTO;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
import kr.sooragenius.forest.facility.reservation.infra.ReservationRedisRepository;
import kr.sooragenius.forest.facility.reservation.infra.ReservationRepository;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.member.domain.Member;
import kr.sooragenius.forest.member.infra.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;
    private final ReservationRedisRepository reservationRedisRepository;

    public ReservationDTO.Response reserve(ReservationDTO.Request request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Facility facility = facilityRepository.findById(request.getFacilityId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시설입니다."));

        validateReserve(request, facility);

        Reservation reservation = Reservation.reserveWithoutUpdateStatus(request, member, facility);

        List<LocalDate> collect = reservation.getReservationDetails().stream().map(item -> item.getFacilitySchedule().getDate()).collect(Collectors.toList());

        if(reservationRedisRepository.increaseAndCheckExists(facility.getId(), collect)) {
            reservation.updateReserveStatus(ReservationStatus.WAIT);
        }else {
            reservation.updateReserveStatusWithToCalc();
        }
        reservationRepository.save(reservation);

        return ReservationDTO.Response.of(reservation);
    }

    public ReservationDTO.Response cancel(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약정보 입니다."));

        reservation.cancel(LocalDateTime.now());

        return ReservationDTO.Response.of(reservation);
    }

    public void validateReserve(ReservationDTO.Request request, Facility facility){

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

    }
}
