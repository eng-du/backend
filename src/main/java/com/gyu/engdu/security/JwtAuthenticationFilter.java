package com.gyu.engdu.security;

import com.gyu.engdu.domain.auth.application.TokenParser;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenParser tokenParser;
  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private static final String MDC_USER_ID = "userId";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    // 로그인 관련 요청은 JWT 검증을 하지 않음
    String requestPath = request.getRequestURI();

    if (requestPath.startsWith("/api/v1/login") || requestPath.startsWith("/favicon.ico")) {
      filterChain.doFilter(request, response);
      return;
    }

    // JWT 관련 에러를 처리하기 위함.
    try {
      String token = getTokenAndValidateJwtRequest(request);
      Role role = tokenParser.parseRoleFromAccessToken(token);
      Long userId = tokenParser.parseUserIdFromToken(token);

      MDC.put(MDC_USER_ID, userId.toString());

      Authentication authentication = jwtAuthenticationProvider.authenticate(userId, role);
      SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy()
          .getContext();
      securityContext.setAuthentication(authentication);
      SecurityContextHolder.getContextHolderStrategy().setContext(securityContext);
    } catch (CustomException e) {
      request.setAttribute("errorCode", e.getErrorCode());
    }

    // 다음 필터로 넘어가기 위해 필수이다.
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_USER_ID);
    }
  }

  private String getTokenAndValidateJwtRequest(HttpServletRequest request) {
    String jwtHeader = request.getHeader("Authorization");

    if (!StringUtils.hasText(jwtHeader) || !jwtHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.JWT_INVALID);
    }

    return jwtHeader.replace("Bearer ", "");
  }

}
