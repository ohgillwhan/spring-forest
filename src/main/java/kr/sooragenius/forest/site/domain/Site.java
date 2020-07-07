package kr.sooragenius.forest.site.domain;

import kr.sooragenius.forest.site.dto.SiteDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor
public class Site {
    @Id
    @Column(name = "site_id")
    private String id;
    private String name;

    public Site(SiteDTO.Request request) {
        this.id = request.getId();
        this.name = request.getName();
    }

    public void update(SiteDTO.Request request) {
        this.name = request.getName();
    }
}
