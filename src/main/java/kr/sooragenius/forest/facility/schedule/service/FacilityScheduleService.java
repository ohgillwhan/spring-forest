package kr.sooragenius.forest.facility.schedule.service;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.infra.FacilityRepository;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.facility.schedule.dto.FacilitySchedulDTO;
import kr.sooragenius.forest.facility.schedule.infra.FacilityScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityScheduleService {
    private final FacilityRepository facilityRepository;
    private final FacilityScheduleRepository facilityScheduleRepository;

    public FacilitySchedulDTO.Response saveFacilitySchedule(FacilitySchedulDTO.Request request) {
        Facility facility = facilityRepository.findById(request.getFacilityId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시설입니다."));

        FacilitySchedule facilitySchedule = new FacilitySchedule(request, facility);
        facilityScheduleRepository.save(facilitySchedule);

        return new FacilitySchedulDTO.Response(facilitySchedule);
    }

}
