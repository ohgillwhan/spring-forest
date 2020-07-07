package kr.sooragenius.forest.facility.service;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.dto.FacilityDTO;
import kr.sooragenius.forest.facility.infra.FacilityRepository;
import kr.sooragenius.forest.site.domain.Site;
import kr.sooragenius.forest.site.infra.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final SiteRepository siteRepository;

    public FacilityDTO.Response createFacility(FacilityDTO.Request request) {
        final Site site = siteRepository.findById(request.getSiteId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Site입니다."));

        Facility facility = new Facility(request, site);

        facilityRepository.save(facility);

        return new FacilityDTO.Response(facility);
    }

}
