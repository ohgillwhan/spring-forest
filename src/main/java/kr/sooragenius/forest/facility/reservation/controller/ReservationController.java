package kr.sooragenius.forest.facility.reservation.controller;

import kr.sooragenius.forest.facility.reservation.dto.ReservationDTO;
import kr.sooragenius.forest.facility.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Random;

@RestController
@RequestMapping("/facility/{facilityId}/reservation")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping(value = {"", "/"})
    public ReservationDTO.Response reserve(@RequestBody ReservationDTO.Request request, @PathVariable Long facilityId) {
        request.setFacilityId(facilityId);

        ReservationDTO.Response reserve = reservationService.reserve(request);
        return reserve;
    }
}
