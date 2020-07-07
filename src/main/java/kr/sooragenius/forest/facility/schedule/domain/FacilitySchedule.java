package kr.sooragenius.forest.facility.schedule.domain;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.schedule.dto.FacilitySchedulDTO;
import kr.sooragenius.forest.facility.schedule.enums.ScheduleType;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class FacilitySchedule {
    @Id @GeneratedValue
    @Column(name = "schedule_id")
    private Long id;

    private String title;
    private String contents;

    private LocalDateTime beginTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Facility facility;


    public FacilitySchedule(FacilitySchedulDTO.Request request, Facility facility) {
        this.title = request.getTitle();
        this.contents = request.getContents();
        this.scheduleType = request.getScheduleType();
        this.beginTime = request.getBeginTime().convertToLocalDateTime();
        this.endTime = request.getEndTime().convertToLocalDateTime();

        this.facility = facility;
        facility.addSchedule(this);
    }
}
