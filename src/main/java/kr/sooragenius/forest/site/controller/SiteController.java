package kr.sooragenius.forest.site.controller;

import kr.sooragenius.forest.common.RedirectUtil;
import kr.sooragenius.forest.site.dto.SiteDTO;
import kr.sooragenius.forest.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;
    @GetMapping("/{siteId}")
    public String detailView(ModelMap modelMap, @PathVariable String siteId) {
        modelMap.addAttribute("site", siteService.findById(siteId));

        return "/site/detailView";
    }
    @GetMapping("/createView")
    public String createView(@ModelAttribute("request") SiteDTO.Request request) {
        return "/site/createView";
    }
    @GetMapping("/updateView/{siteId}")
    public String updateView(ModelMap modelMap, @PathVariable String siteId) {
        modelMap.addAttribute("site", siteService.findById(siteId));
        return "/site/updateView";
    }
    @PostMapping(value = {"", "/"})
    public String save(ModelMap modelMap, @ModelAttribute("request") @Valid SiteDTO.Request request, Errors errors) {
        if(errors.hasErrors()) {
            return createView(request);
        }
        SiteDTO.Response response = siteService.saveSite(request);

        return RedirectUtil.redirectWithMessage(modelMap, "/site/" + response.getId(), "추가를 성공했습니다.");
    }
    @PostMapping("/{siteId}")
    public String update(ModelMap modelMap, @ModelAttribute("request") @Valid SiteDTO.Request request, Errors errors, @PathVariable String siteId) {
        if(errors.hasErrors()) {
            modelMap.addAttribute("site", request);
            return "/site/updateView";
        }
        SiteDTO.Response response = siteService.updateById(request, siteId);

        return RedirectUtil.redirectWithMessage(modelMap, "/site/" + response.getId(), "추가를 성공했습니다.");
    }
    @DeleteMapping("/{siteId}")
    public String delete(ModelMap modelMap, @PathVariable String siteId) {
        siteService.deleteById(siteId);

        return RedirectUtil.redirectWithMessage(modelMap, "/site", "삭제를 성공했습니다.");
    }
}
