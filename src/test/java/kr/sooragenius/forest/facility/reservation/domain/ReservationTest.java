package kr.sooragenius.forest.facility.reservation.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.dto.FacilityDTO;
import kr.sooragenius.forest.facility.enums.Discount;
import kr.sooragenius.forest.facility.enums.FacilityType;
import kr.sooragenius.forest.facility.infra.FacilityRepository;
import kr.sooragenius.forest.facility.reservation.dto.ReservationDTO;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
import kr.sooragenius.forest.facility.reservation.infra.ReservationRepository;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.facility.schedule.dto.FacilitySchedulDTO;
import kr.sooragenius.forest.facility.schedule.enums.ScheduleType;
import kr.sooragenius.forest.facility.schedule.infra.FacilityScheduleRepository;
import kr.sooragenius.forest.site.domain.Site;
import kr.sooragenius.forest.site.dto.SiteDTO;
import kr.sooragenius.forest.site.infra.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReservationTest {
    private final SiteRepository siteRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityScheduleRepository facilityScheduleRepository;
    private final ReservationRepository reservationRepository;
    private final EntityManager entityManager;
    private Site site;
    private Facility facility;
    private List<FacilitySchedule> facilitySchedules;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        site = siteRepository.save(Site.of(createSite()));
        facility = facilityRepository.save(Facility.of(createFacility(), site));
        facilitySchedules = facilityScheduleRepository.saveAll(createFacilitySchedules(facility).stream().map(item -> FacilitySchedule.of(item, facility)).collect(Collectors.toList()));

        entityManager.flush();
        entityManager.clear();
    }
    @Test
    @Transactional
    void success() {
        ReservationDTO.Request success = new ReservationDTO.Request();
        success.setMemberId("..");
        success.setBeginDate(LocalDate.of(2020, 01, 01));
        success.setEndDate(LocalDate.of(2020, 01, 07));
        success.setDiscount(Discount.HAS_ELECTION_PAPER);
        success.setNumberOfPeople(1);

        Reservation completeReserve = saveAndFlush(Reservation.reserve(success,null, facilityRepository.findById(facility.getId()).get()));

        assertAll(
                () -> assertEquals(ReservationStatus.COMPLETE, completeReserve.getStatus()),
                () -> assertEquals(6, completeReserve.getReservationDetails().size()),
                () -> assertEquals(10000, completeReserve.getTotalAmount()),
                () -> assertEquals(10000 / 100 * success.getDiscount().getPercent(), completeReserve.getDiscountAmount() ),
                () -> assertEquals(10000 / 100 * (100 - success.getDiscount().getPercent()), completeReserve.getFeeAmount() ),
                () -> {
                    for(int i = 1; i<=6; i++) {
                        assertEquals(i, completeReserve.getReservationDetails().get(i - 1).getFacilitySchedule().getDate().getDayOfMonth());
                    }
                }
        );
    }
    @Test
    @Transactional
    void waitTest() {
        ReservationDTO.Request success = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 02))
                .endDate(LocalDate.of(2020, 01, 05))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();

        ReservationDTO.Request waitBeforeSuccess = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 01))
                .endDate(LocalDate.of(2020, 01, 03))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();

        ReservationDTO.Request waitBetweenSuccess = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 03))
                .endDate(LocalDate.of(2020, 01, 04))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();

        ReservationDTO.Request waitAfterSuccess = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 04))
                .endDate(LocalDate.of(2020, 01, 06))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();


        ReservationDTO.Request completeAfterSuccess = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 05))
                .endDate(LocalDate.of(2020, 01, 07))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();

        Reservation completeReserve = saveAndFlush(Reservation.reserve(success,null, facilityRepository.findById(facility.getId()).get()));
        Reservation waitBefore = saveAndFlush(Reservation.reserve(waitBeforeSuccess,null, facilityRepository.findById(facility.getId()).get()));
        Reservation waitBetween = saveAndFlush(Reservation.reserve(waitBetweenSuccess,null, facilityRepository.findById(facility.getId()).get()));
        Reservation waitAfter = saveAndFlush(Reservation.reserve(waitAfterSuccess,null, facilityRepository.findById(facility.getId()).get()));
        Reservation completeAfter = saveAndFlush(Reservation.reserve(completeAfterSuccess,null, facilityRepository.findById(facility.getId()).get()));
        Reservation completeAfterWait = saveAndFlush(Reservation.reserve(completeAfterSuccess,null, facilityRepository.findById(facility.getId()).get()));

        assertEquals(ReservationStatus.COMPLETE, completeReserve.getStatus());
        assertEquals(ReservationStatus.WAIT, waitBefore.getStatus());
        assertEquals(ReservationStatus.WAIT, waitBetween.getStatus());
        assertEquals(ReservationStatus.WAIT, waitAfter.getStatus());
        assertEquals(ReservationStatus.COMPLETE, completeAfter.getStatus());
        assertEquals(ReservationStatus.WAIT, completeAfterWait.getStatus());

    }

    @Test
    @Transactional
    public void holiday() {
        ReservationDTO.Request holidayWithReservableRequest = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 02))
                .endDate(LocalDate.of(2020, 01, 9))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();
        ReservationDTO.Request onlyHolidayRequest = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2020, 01, 8))
                .endDate(LocalDate.of(2020, 01, 10))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();



        assertThrows(RuntimeException.class, () -> Reservation.reserve(holidayWithReservableRequest,null, facilityRepository.findById(facility.getId()).get()));
        assertThrows(RuntimeException.class, () -> Reservation.reserve(onlyHolidayRequest,null, facilityRepository.findById(facility.getId()).get()));


    }


    @Test
    @Transactional
    public void canNotReserveDay() {

        ReservationDTO.Request canNotReserveDayRequest = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2050, 01, 02))
                .endDate(LocalDate.of(2050, 01, 9))
                .discount(Discount.HAS_ELECTION_PAPER)
                .numberOfPeople(1)
                .build();

        assertThrows(RuntimeException.class, () -> Reservation.reserve(canNotReserveDayRequest,null, facilityRepository.findById(facility.getId()).get()));
    }
    @Test
    @Transactional
    public void canNotReserveDiscount() {

        ReservationDTO.Request canNotReserveDiscountRequest = ReservationDTO.Request.builder()
                .memberId("..")
                .beginDate(LocalDate.of(2050, 01, 02))
                .endDate(LocalDate.of(2050, 01, 9))
                .discount(Discount.CITIZEN_AND_FAMILY_OVER_4)
                .numberOfPeople(1)
                .build();

        assertThrows(RuntimeException.class, () -> Reservation.reserve(canNotReserveDiscountRequest,null, facilityRepository.findById(facility.getId()).get()));
    }

    /** 취소에 따른 로직 **/
    @Test
    @Transactional
    public void calcRefundAmountCancelEquals1Day() {
        ReservationDTO.Request success = new ReservationDTO.Request();
        success.setMemberId("..");
        success.setBeginDate(LocalDate.of(2020, 01, 03));
        success.setEndDate(LocalDate.of(2020, 01, 07));
        success.setDiscount(Discount.HAS_ELECTION_PAPER);
        success.setNumberOfPeople(1);

        Reservation reservation = saveAndFlush(Reservation.reserve(success, null, facilityRepository.findById(facility.getId()).get()));
        Optional<Reservation> canceled = reservation.cancel(LocalDateTime.of(2020, 01, 02, 00, 00, 00));

        assertEquals(6400, reservation.getFeeAmount());
        assertEquals(2560, reservation.getRefundAmount());
        assertEquals(ReservationStatus.CANCEL, reservation.getStatus());
    }
    @Test
    @Transactional
    public void calcRefundAmountCancelBeforeAbout7Days() {
        ReservationDTO.Request success = new ReservationDTO.Request();
        success.setMemberId("..");
        success.setBeginDate(LocalDate.of(2020, 01, 03));
        success.setEndDate(LocalDate.of(2020, 01, 07));
        success.setDiscount(Discount.HAS_ELECTION_PAPER);
        success.setNumberOfPeople(1);

        Reservation reservation = saveAndFlush(Reservation.reserve(success, null, facilityRepository.findById(facility.getId()).get()));
        Optional<Reservation> canceled = reservation.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00));

        assertEquals(6400, reservation.getFeeAmount());
        assertEquals(3840, reservation.getRefundAmount());
        assertEquals(ReservationStatus.CANCEL, reservation.getStatus());

    }

    @Test
    @Transactional
    public void calcRefundAmountCancelBeforeAbout14Days() {

        ReservationDTO.Request success = new ReservationDTO.Request();
        success.setMemberId("..");
        success.setBeginDate(LocalDate.of(2020, 01, 03));
        success.setEndDate(LocalDate.of(2020, 01, 07));
        success.setDiscount(Discount.HAS_ELECTION_PAPER);
        success.setNumberOfPeople(1);

        Reservation reservation = saveAndFlush(Reservation.reserve(success, null, facilityRepository.findById(facility.getId()).get()));
        Optional<Reservation> canceled = reservation.cancel(LocalDateTime.of(2019, 12, 20, 00, 00, 00));

        assertEquals(6400, reservation.getFeeAmount());
        assertEquals(5120, reservation.getRefundAmount());
        assertEquals(ReservationStatus.CANCEL, reservation.getStatus());
    }

    @Test
    @Transactional
    public void calcRefundAmountCancelBeforeAbout40Days() {

        ReservationDTO.Request success = new ReservationDTO.Request();
        success.setMemberId("..");
        success.setBeginDate(LocalDate.of(2020, 01, 03));
        success.setEndDate(LocalDate.of(2020, 01, 07));
        success.setDiscount(Discount.HAS_ELECTION_PAPER);
        success.setNumberOfPeople(1);

        Reservation reservation = saveAndFlush(Reservation.reserve(success, null, facilityRepository.findById(facility.getId()).get()));
        Optional<Reservation> canceled = reservation.cancel(LocalDateTime.of(2019, 11, 20, 00, 00, 00));

        assertEquals(6400, reservation.getFeeAmount());
        assertEquals(6400, reservation.getRefundAmount());
        assertEquals(ReservationStatus.CANCEL, reservation.getStatus());
    }

    @Test
    @Transactional
    public void getNextCompleteWhenCanceledReserveStepByStep() {
        ReservationDTO.Request request03To06 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,03)).endDate(LocalDate.of(2020,01,06)).build();

        ReservationDTO.Request request01To04 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,01)).endDate(LocalDate.of(2020,01,04)).build();

        ReservationDTO.Request request05To08 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,05)).endDate(LocalDate.of(2020,01,8)).build();

        ReservationDTO.Request request05To06 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,05)).endDate(LocalDate.of(2020,01,06)).build();

        ReservationDTO.Request request01To08 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,1)).endDate(LocalDate.of(2020,01,8)).build();

        Long id03To06 = saveAndFlush(Reservation.reserve(request03To06, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id01To04 = saveAndFlush(Reservation.reserve(request01To04, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id05To08 = saveAndFlush(Reservation.reserve(request05To08, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id05To06 = saveAndFlush(Reservation.reserve(request05To06, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id01To08 = saveAndFlush(Reservation.reserve(request01To08, null, facilityRepository.findById(facility.getId()).get())).getId();


        Reservation reservation03To06 = reservationRepository.findById(id03To06).get();
        Reservation reservation01To04 = reservationRepository.findById(id01To04).get();
        Reservation reservation05To08 = reservationRepository.findById(id05To08).get();
        Reservation reservation05To06 = reservationRepository.findById(id05To06).get();
        Reservation reservation01To08 = reservationRepository.findById(id01To08).get();

        assertAll(
                () -> assertEquals(ReservationStatus.COMPLETE, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To08.getStatus())
        );

        // 03 - 06 취소
        Reservation nextOf03To06 = reservation03To06.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다."));

        assertAll(
                () -> assertEquals(ReservationStatus.CANCEL, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.COMPLETE, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To08.getStatus())
        );

        // 01 - 04 취소
        Reservation nextOf01To04 = nextOf03To06.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다."));

        assertAll(
                () -> assertEquals(ReservationStatus.CANCEL, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.COMPLETE, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To08.getStatus())
        );

        // 01 - 04 취소
        Reservation nextOf05To08 = nextOf01To04.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다."));

        assertAll(
                () -> assertEquals(ReservationStatus.CANCEL, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.COMPLETE, reservation05To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To08.getStatus())
        );

        // 05 - 06 취소
        Reservation nextOf01To06 = nextOf05To08.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다."));

        assertAll(
                () -> assertEquals(ReservationStatus.CANCEL, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation05To06.getStatus()),
                () -> assertEquals(ReservationStatus.COMPLETE, reservation01To08.getStatus())
        );

        // 01 - 08 취소
        assertThrows(RuntimeException.class, () -> nextOf01To06.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다.")));
    }



    @Test
    @Transactional
    public void exceptWhenCanceled() {
        ReservationDTO.Request request03To06 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,03)).endDate(LocalDate.of(2020,01,06)).build();

        ReservationDTO.Request request01To04 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,01)).endDate(LocalDate.of(2020,01,04)).build();

        ReservationDTO.Request request05To08 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,05)).endDate(LocalDate.of(2020,01,8)).build();

        ReservationDTO.Request request05To06 = ReservationDTO.Request.builder()
                .memberId("..").discount(Discount.HAS_ELECTION_PAPER).numberOfPeople(1)
                .beginDate(LocalDate.of(2020,01,05)).endDate(LocalDate.of(2020,01,06)).build();

        Long id03To06 = saveAndFlush(Reservation.reserve(request03To06, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id01To04 = saveAndFlush(Reservation.reserve(request01To04, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id05To08 = saveAndFlush(Reservation.reserve(request05To08, null, facilityRepository.findById(facility.getId()).get())).getId();
        Long id05To06 = saveAndFlush(Reservation.reserve(request05To06, null, facilityRepository.findById(facility.getId()).get())).getId();


        Reservation reservation03To06 = reservationRepository.findById(id03To06).get();
        Reservation reservation01To04 = reservationRepository.findById(id01To04).get();
        Reservation reservation05To08 = reservationRepository.findById(id05To08).get();
        Reservation reservation05To06 = reservationRepository.findById(id05To06).get();

        assertAll(
                () -> assertEquals(ReservationStatus.COMPLETE, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To06.getStatus())
        );

        // 05 - 08 취소
        assertThrows(RuntimeException.class, () -> reservation05To08.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다.")));

        assertAll(
                () -> assertEquals(ReservationStatus.COMPLETE, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation05To06.getStatus())
        );

        // 05 - 06 취소
        assertThrows(RuntimeException.class, () -> reservation05To06.cancel(LocalDateTime.of(2020, 01, 01, 00, 00, 00)).orElseThrow(() -> new RuntimeException("다음 예약이 없습니다.")));

        assertAll(
                () -> assertEquals(ReservationStatus.COMPLETE, reservation03To06.getStatus()),
                () -> assertEquals(ReservationStatus.WAIT, reservation01To04.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation05To08.getStatus()),
                () -> assertEquals(ReservationStatus.CANCEL, reservation05To06.getStatus())
        );

    }

    @Test
    @Transactional
    void getBetweenCancelDateAndMinOfReserveDate() {
        ReservationDTO.Request success = new ReservationDTO.Request();
        success.setMemberId("..");
        success.setBeginDate(LocalDate.of(2020, 01, 03));
        success.setEndDate(LocalDate.of(2020, 01, 07));
        success.setDiscount(Discount.HAS_ELECTION_PAPER);
        success.setNumberOfPeople(1);

        Reservation reservation = saveAndFlush(Reservation.reserve(success, null, facilityRepository.findById(facility.getId()).get()));

        long betweenCancelDateAndMinOfReserveDate = reservation.getBetweenCancelDateAndMinOfReserveDate(LocalDateTime.of(2020, 01, 01, 00, 00, 00));

        assertEquals(2, betweenCancelDateAndMinOfReserveDate);
    }

    private Reservation saveAndFlush(Reservation reservation) {
        Reservation save = reservationRepository.save(reservation);
        entityManager.flush();
        entityManager.clear();

        return reservationRepository.findById(save.getId()).get();
    }
    private SiteDTO.Request createSite() {
        SiteDTO.Request request = new SiteDTO.Request();
        request.setId("www");
        request.setName("www");

        return request;
    }
    private FacilityDTO.Request createFacility() {
        FacilityDTO.Request request = new FacilityDTO.Request();

        request.setSiteId("www");
        request.setName("휴양림 시설A");
        request.setNormalAmount(1000);
        request.setPeakAmount(5000);
        request.setFacilityType(FacilityType.NORMAL);
        request.setDiscounts(Sets.newSet(Discount.CITIZEN, Discount.HAS_ELECTION_PAPER));

        return request;
    }
    private List<FacilitySchedulDTO.Request> createFacilitySchedules(Facility facility) {
        List<FacilitySchedulDTO.Request> results = new ArrayList<>();
        for(int i = 1; i<=5; i++) {
            results.add(FacilitySchedulDTO.Request.builder()
                    .facilityId(facility.getId())
                    .title("Normal-2020-01-0"+i)
                    .contents("Normal-2020-01-0"+i)
                    .scheduleType(ScheduleType.NORMAL)
                    .date(new FacilitySchedulDTO.Request.DateTime(2020,01,i))
                    .build()
            );
        }

        for(int i = 6; i<=7; i++) {
            results.add(FacilitySchedulDTO.Request.builder()
                    .facilityId(facility.getId())
                    .title("Normal-2020-01-0"+i)
                    .contents("Normal-2020-01-0"+i)
                    .scheduleType(ScheduleType.PEAK)
                    .date(new FacilitySchedulDTO.Request.DateTime(2020,01,i))
                    .build()
            );
        }
        for(int i = 8; i<=10; i++) {
            results.add(FacilitySchedulDTO.Request.builder()
                    .facilityId(facility.getId())
                    .title("Normal-2020-01-08")
                    .contents("Normal-2020-01-10")
                    .scheduleType(ScheduleType.REST)
                    .date(new FacilitySchedulDTO.Request.DateTime(2020, 01, i))
                    .build()
            );
        }

        return results;
    }
}