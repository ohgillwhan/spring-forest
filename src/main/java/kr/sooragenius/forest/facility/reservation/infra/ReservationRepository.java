package kr.sooragenius.forest.facility.reservation.infra;

import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
