package com.gyu.engdu.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

  private static final String API_START_TIME_ATTR = "apiStartTime";

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    request.setAttribute(API_START_TIME_ATTR, System.currentTimeMillis());
    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      @Nullable Exception ex) {
    Long startTime = (Long) request.getAttribute(API_START_TIME_ATTR);

    if (startTime != null) {
      long duration = System.currentTimeMillis() - startTime;
      log.info("[API RESPONSE] status={} method={} uri={} duration={}ms",
          response.getStatus(),
          request.getMethod(),
          request.getRequestURI(),
          duration);
    }
  }
}
