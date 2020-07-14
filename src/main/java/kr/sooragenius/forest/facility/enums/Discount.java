package kr.sooragenius.forest.facility.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum Discount {
    FAMILY_OVER_4("4인가족이상", 20),
    CITIZEN("지역시민", 20),
    CITIZEN_AND_FAMILY_OVER_4("지역시민이면서 4인가족이상", 20),
    HAS_ELECTION_PAPER("선거용지 소유자", 20);
    private String description;
    private Integer percent;

}
