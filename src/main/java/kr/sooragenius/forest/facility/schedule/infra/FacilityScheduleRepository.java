package kr.sooragenius.forest.facility.schedule.infra;

import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityScheduleRepository extends JpaRepository<FacilitySchedule, Long> {
}
