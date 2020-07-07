package kr.sooragenius.forest.site.infra;

import kr.sooragenius.forest.site.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, String> {
}
