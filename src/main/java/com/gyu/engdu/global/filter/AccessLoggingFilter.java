package com.gyu.engdu.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class AccessLoggingFilter extends OncePerRequestFilter {

  private static final String[] EXCLUDE_LOGGING_URIS = {
      "/actuator/prometheus",
      "/favicon.ico"
  };

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String uri = request.getRequestURI();

    if (isLoggable(uri)) {
      String clientIp = getClientIp(request);
      String method = request.getMethod();
      String userAgent = request.getHeader("User-Agent");

      log.info("[ACCESS] client_ip={} method={} uri={} user_agent=\"{}\"",
          clientIp, method, uri, userAgent != null ? userAgent : "Unknown");
    }

    filterChain.doFilter(request, response);
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  private boolean isLoggable(String uri) {
    for (String excludeUri : EXCLUDE_LOGGING_URIS) {
      if (uri.startsWith(excludeUri)) {
        return false;
      }
    }
    return true;
  }
}
