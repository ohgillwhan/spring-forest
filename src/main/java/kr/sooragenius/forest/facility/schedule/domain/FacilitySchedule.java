package kr.sooragenius.forest.facility.schedule.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.reservation.detail.domain.ReservationDetail;
import kr.sooragenius.forest.facility.reservation.domain.Reservation;
import kr.sooragenius.forest.facility.reservation.enums.ReservationStatus;
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
    @JoinColumn(name = "facility_id", referencedColumnName = "facility_id")
    private Facility facility;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facilitySchedule")
    private List<ReservationDetail> reservationDetails;


    public static FacilitySchedule of(FacilitySchedulDTO.Request request, Facility facility) {
        FacilitySchedule facilitySchedule = new FacilitySchedule();
        facilitySchedule.title = request.getTitle();
        facilitySchedule.contents = request.getContents();
        facilitySchedule.scheduleType = request.getScheduleType();
        facilitySchedule.date = request.getDate().convertToLocalDate();

        facilitySchedule.facility = facility;

        return facilitySchedule;
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

    public boolean isNotReservableStatus() {
        return !isReservableStatus();
    }
    public boolean isReservableStatus() {
        if(scheduleType == ScheduleType.PEAK || scheduleType == ScheduleType.NORMAL) {
            return true;
        }
        return false;
    }

    private boolean hasCanNotReserveStatusByReservations() {
        for(ReservationDetail detail : getReservationDetails()) {
            if(detail.getReservation().isNotReservableStatus()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCompleteStatusOfReservations(ReservationDetail ignoreDetail) {
        return hasStatusWithoutIgnoreDetail(ignoreDetail, ReservationStatus.COMPLETE);
    }

    public boolean hasWaitStatusOfReservations(ReservationDetail ignoreDetail) {
        return hasStatusWithoutIgnoreDetail(ignoreDetail, ReservationStatus.WAIT);
    }

    private boolean hasStatusWithoutIgnoreDetail(ReservationDetail ignoreDetail, ReservationStatus status) {
        for(ReservationDetail detail : getReservationDetails()) {
            if(detail == ignoreDetail) {
                continue;
            }
            if(detail.getReservation().getStatus() == status) {
                return true;
            }
        }
        return false;
    }
}
