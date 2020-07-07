package kr.sooragenius.forest.facility.infra;

import kr.sooragenius.forest.facility.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
