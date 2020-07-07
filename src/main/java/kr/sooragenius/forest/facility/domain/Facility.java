package kr.sooragenius.forest.facility.domain;

import kr.sooragenius.forest.facility.dto.FacilityDTO;
import kr.sooragenius.forest.facility.enums.Discount;
import kr.sooragenius.forest.facility.enums.FacilityType;
import kr.sooragenius.forest.facility.schedule.domain.FacilitySchedule;
import kr.sooragenius.forest.site.domain.Site;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor
public class Facility {
    @Id @GeneratedValue
    @Column(name = "facilty_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SITE_ID")
    private Site site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityType facilityType;

    @ElementCollection
    @CollectionTable(name="FACILITY_DISCOUNT")
    @Enumerated(EnumType.STRING)
    private Set<Discount> discounts;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.PERSIST)
    private List<FacilitySchedule> facilitySchedules = new ArrayList<>();

    public Facility(FacilityDTO.Request request, Site site) {
        this.name = request.getName();
        this.site = site;
        this.facilityType = request.getFacilityType();
        this.discounts = request.getDiscounts();
    }

    public void addSchedule(FacilitySchedule facilitySchedule) {
        if(!getFacilitySchedules().contains(facilitySchedule)) {
            getFacilitySchedules().add(facilitySchedule);
        }
    }
}
