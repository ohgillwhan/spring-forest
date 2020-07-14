package kr.sooragenius.forest.facility.reservation.service;

import com.sun.tools.corba.se.idl.constExpr.GreaterEqual;
import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.infra.FacilityRepository;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.reservation.dto.ReservationDTO;
import kr.sooragenius.forest.facility.reservation.infra.ReservationRepository;
import kr.sooragenius.forest.member.domain.Member;
import kr.sooragenius.forest.member.infra.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;

    public ReservationDTO.Response reserve(ReservationDTO.Request request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Facility facility = facilityRepository.findById(request.getFacilityId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시설입니다."));

        Reservation reservation = Reservation.reserve(request, member, facility);

        reservationRepository.save(reservation);

        return ReservationDTO.Response.of(reservation);
    }
}
