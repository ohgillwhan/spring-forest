package kr.sooragenius.forest.site.dto;

import kr.sooragenius.forest.site.domain.Site;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SiteDTO {
    @Data
    public static class Request {
        @NotNull @NotEmpty
        private String id;
        @NotNull @NotEmpty
        private String name;
    }
    @Data
    public static class Response {
        private String id;
        private String name;

        public static Response of(Site site) {
            Response response = new Response();
            response.setId(site.getId());
            response.setName(site.getName());

            return response;
        }
    }
}
