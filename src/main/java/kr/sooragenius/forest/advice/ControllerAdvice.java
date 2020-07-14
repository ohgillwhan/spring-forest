package kr.sooragenius.forest.advice;

import kr.sooragenius.forest.common.RedirectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public String runtimeException(ModelMap modelMap, IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return RedirectUtil.backWithMessage(modelMap, ex.getMessage());
    }
}
