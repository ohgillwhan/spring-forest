package kr.sooragenius.forest.site.service;

import kr.sooragenius.forest.site.domain.Site;
import kr.sooragenius.forest.site.dto.SiteDTO;
import kr.sooragenius.forest.site.infra.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.sooragenius.forest.site.dto.SiteDTO.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository siteRepository;

    public SiteDTO.Response saveSite(SiteDTO.Request request) {
        if(siteRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("이미 존재하는 사이트입니다.");
        }
        Site site = Site.of(request);

        Site save = siteRepository.save(site);

        return Response.of(save);
    }

    public SiteDTO.Response findById(String siteId) {
        Site site = siteRepository.findById(siteId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사이트입니다."));

        return Response.of(site);
    }

    public void deleteById(String siteId) {
        siteRepository.deleteById(siteId);
    }

    public SiteDTO.Response updateById(SiteDTO.Request request, String siteId) {
        Site site = siteRepository.findById(siteId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사이트입니다."));

        site.update(request);

        return Response.of(site);
    }

}
