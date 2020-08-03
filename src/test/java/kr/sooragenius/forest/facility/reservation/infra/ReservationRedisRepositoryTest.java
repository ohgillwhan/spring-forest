package kr.sooragenius.forest.facility.reservation.infra;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReservationRedisRepositoryTest {
    private final ReservationRedisRepository reservationRedisRepository;

    @Test
    void save() {
//        ReservationRedisDTO.Save save = ReservationRedisDTO.Save.builder().facilityId(1L).siteId("www").localDate(LocalDate.now()).build();
//        reservationRedisRepository.save(save);
    }
}