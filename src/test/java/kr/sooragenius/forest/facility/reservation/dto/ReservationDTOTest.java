package kr.sooragenius.forest.facility.reservation.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDTOTest {
    @Test
    public void getBetweenDays() {
        ReservationDTO.Request request = new ReservationDTO.Request();
        request.setBeginDate(LocalDate.of(2010,01,01));
        request.setEndDate(LocalDate.of(2010,01,15));

        // 1,2,3,4,5,6,7,8,9,10
        // 11,12,13,14
        assertEquals(14L, request.getBetweenDays());
    }
}