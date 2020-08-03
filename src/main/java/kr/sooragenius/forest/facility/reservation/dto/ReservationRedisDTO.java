package kr.sooragenius.forest.facility.reservation.dto;

public class ReservationRedisDTO {
    private String siteId;
    private Long facilityId;
    public String getKey() {
        return siteId+"::reserve::"+facilityId;
    }
}
