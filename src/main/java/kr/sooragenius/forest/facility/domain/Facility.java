package kr.sooragenius.forest.facility.domain;

import kr.sooragenius.forest.facility.dto.FacilityDTO;
import kr.sooragenius.forest.facility.enums.Discount;
import kr.sooragenius.forest.facility.enums.FacilityType;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.site.domain.Site;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Facility {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id", insertable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int normalAmount;
    private int peakAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SITE_ID")
    private Site site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityType facilityType;

    @ElementCollection
    @CollectionTable(name="FACILITY_DISCOUNT", joinColumns = @JoinColumn(name = "facility_id"))
    @Enumerated(EnumType.STRING)
    private Set<Discount> discounts = new HashSet<>();

    @OneToMany(mappedBy = "facility")
    private List<FacilitySchedule> facilitySchedules = new ArrayList<>();

    @OneToMany(mappedBy = "facility")
    private List<Reservation> reservations = new ArrayList<>();

    public static Facility of(FacilityDTO.Request request, Site site) {
        Facility facility = new Facility();

        facility.name = request.getName();
        facility.site = site;
        facility.normalAmount = request.getNormalAmount();
        facility.peakAmount = request.getPeakAmount();
        facility.facilityType = request.getFacilityType();
        facility.discounts = request.getDiscounts();

        return facility;
    }

    public List<FacilitySchedule> getSchedulesBetweenDate(LocalDate beginDate, LocalDate endDate) {
        List<FacilitySchedule> betweenResults = new ArrayList<>();
        for (FacilitySchedule facilitySchedule : getFacilitySchedules()) {
            if(facilitySchedule.isBetween(beginDate, endDate)) {
                betweenResults.add(facilitySchedule);
            }
        }
        return betweenResults;
    }

    public boolean hasNotDiscount(Discount discount) {
        return !hasDiscount(discount);
    }
    public boolean hasDiscount(Discount discount) {
        return discounts.contains(discount);
    }

    public Optional<Reservation> getWaitToCompleteReserveByCancelReserve(Reservation reservation) {
        if(reservation.getStatus() != ReservationStatus.CANCEL) {
            throw new RuntimeException("취소된 예약정보가 아닙니다");
        }
        Optional<Reservation> dupReserve = getReservations().stream()
                .filter(item -> item != reservation)
                .filter(item -> item.getStatus() == ReservationStatus.WAIT)
                .filter(item ->
                        item.getReservationDetails().stream()
                                .filter(detail ->
                                        detail.getFacilitySchedule().isBetween(reservation.getBeginDate(), reservation.getEndDate()))
                                .count() <= reservation.getReservationDetails().size())
                .findFirst();

        return dupReserve;
    }
}
