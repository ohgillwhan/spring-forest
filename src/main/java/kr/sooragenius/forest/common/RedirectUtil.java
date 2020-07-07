package kr.sooragenius.forest.common;

import org.springframework.ui.ModelMap;

public class RedirectUtil {
    public static String redirectWithMessage(ModelMap modelMap, String url, String message) {
        modelMap.addAttribute("url", url);
        modelMap.addAttribute("message", message);

        return "/common/redirectWithMessage";
    }
}
