package kr.sooragenius.forest.facility.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ScheduleType {
    REST("휴관일"),
    PEAK("성수기");

    private String title;
}
