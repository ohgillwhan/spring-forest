package kr.sooragenius.forest.facility.schedule.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.schedule.dto.FacilitySchedulDTO;
import kr.sooragenius.forest.facility.schedule.enums.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class FacilitySchedule {
    @Id @GeneratedValue
    @Column(name = "schedule_id")
    private Long id;

    private String title;
    private String contents;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Facility facility;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facilitySchedule")
    private List<Reservation> reservations;


    public FacilitySchedule(FacilitySchedulDTO.Request request, Facility facility) {
        this.title = request.getTitle();
        this.contents = request.getContents();
        this.scheduleType = request.getScheduleType();
        this.date = request.getDate().convertToLocalDate();

        this.facility = facility;
    }

    public boolean isBetween(LocalDate beginDate, LocalDate endDate) {
        boolean isAfter = date.isEqual(beginDate) || date.isAfter(beginDate);
        boolean isBefore = date.isBefore(endDate);
        return isAfter && isBefore;
    }

    public boolean isReservable() {
        if(isReservableStatus() && !hasCanNotReserveStatusByReservations()) {
            return true;
        }
        return false;
    }
    public boolean isNotReservable() {
        return !isReservable();
    }

    public int getReserveAmount() {
        if(scheduleType == ScheduleType.PEAK) {
            return facility.getPeakAmount();
        }
        return facility.getNormalAmount();
    }

    private boolean isReservableStatus() {
        if(scheduleType == ScheduleType.PEAK || scheduleType == ScheduleType.NORMAL) {
            return true;
        }
        return false;
    }
    private boolean hasCanNotReserveStatusByReservations() {
        for(Reservation reservation : reservations) {
            if(reservation.isNotReservableStatus()) {
                return false;
            }
        }
        return true;
    }
}
