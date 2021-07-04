package com.winter.flashsale.exphandler.handler;

import com.winter.flashsale.exphandler.exception.*;
import com.winter.flashsale.exphandler.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    private static final String DEFAULT_ERROR_VIEW = "ERROR";

    @ExceptionHandler(value = JsonException.class)
    @ResponseBody
    public ApiResponse jsonExceptionHandler(JsonException exception) {
        log.error("[JsonException]: {}", exception.getMessage());
        return ApiResponse.ofException(exception);
    }

    @ExceptionHandler(value = PageException.class)
    @ResponseBody
    public ModelAndView pageExceptionHandler(PageException exception) {
        log.error("[PageException]: {}", exception.getMessage());
        ModelAndView view = new ModelAndView();
        view.addObject("message", exception.getMessage());
        view.setViewName(DEFAULT_ERROR_VIEW);
        return view;
    }

    @ExceptionHandler(value = RedisException.class)
    @ResponseBody
    public ApiResponse redisExceptionHandler(RedisException exception) {
        log.error("[RedisException]: {}", exception.getMessage());
        return ApiResponse.ofException(exception);
    }

    @ExceptionHandler(value = ZookeeperException.class)
    @ResponseBody
    public ApiResponse zkExceptionHandler(RedisException exception) {
        log.error("[ZookeeperException]: {}", exception.getMessage());
        return ApiResponse.ofException(exception);
    }
}
