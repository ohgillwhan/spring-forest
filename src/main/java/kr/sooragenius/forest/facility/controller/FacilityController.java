package kr.sooragenius.forest.facility.controller;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.dto.FacilityDTO;
import kr.sooragenius.forest.facility.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping(value = {"", "/"})
    public FacilityDTO.Response createFacility(@RequestBody FacilityDTO.Request request) {
        return facilityService.saveFacility(request);
    }
}
