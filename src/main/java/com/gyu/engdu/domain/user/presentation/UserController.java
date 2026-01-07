package com.gyu.engdu.domain.user.presentation;

import com.gyu.engdu.domain.user.application.DeleteUserService;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.application.dto.response.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserQueryService userQueryService;
  private final DeleteUserService deleteUserService;

  @GetMapping("/me")
  public ResponseEntity<UserSummaryResponse> readMySummaryInfo(
      @AuthenticationPrincipal(expression = "userId") Long userId
  ) {
    return ResponseEntity.ok(userQueryService.findSummaryUserInfo(userId));
  }

  @DeleteMapping("/withdrawal")
  public ResponseEntity<Void> withdrawAccount(
      @AuthenticationPrincipal(expression = "userId") Long userId
  ) {
    deleteUserService.delete(userId);
    return ResponseEntity.noContent()
        .build();
  }
}
