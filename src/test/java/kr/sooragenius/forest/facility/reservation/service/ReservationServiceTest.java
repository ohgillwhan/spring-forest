package kr.sooragenius.forest.facility.reservation.service;

import kr.sooragenius.forest.facility.infra.FacilityRepository;
import kr.sooragenius.forest.member.domain.Member;
import kr.sooragenius.forest.member.infra.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ReservationServiceTest {
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private FacilityRepository facilityRepository;

    @Test
    void reserve() {
        given(memberRepository.findById("sooragenius")).willReturn(Optional.of(new Member()));

        System.out.println(memberRepository.findById("sooragenius"));
    }
}