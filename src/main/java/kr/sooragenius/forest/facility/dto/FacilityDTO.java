package kr.sooragenius.forest.facility.dto;

import kr.sooragenius.forest.facility.domain.Facility;
import kr.sooragenius.forest.facility.enums.Discount;
import kr.sooragenius.forest.facility.enums.FacilityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class FacilityDTO {
    @Data
    public static class Response {
        private Long id;
        private String name;
        public Response(Facility facility) {
            this.id = facility.getId();
            this.name = facility.getName();
        }
    }
    @Data
    public static class Request {
        @NotNull @NotEmpty
        private String siteId;
        @NotNull @NotEmpty
        private String name;
        @NotNull @Min(0)
        private int normalAmount;
        @NotNull @Min(0)
        private int peakAmount;
        @NotNull
        private FacilityType facilityType;
        private Set<Discount> discounts;
    }
}
