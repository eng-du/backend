package com.gyu.engdu.domain.user.presentation;

import com.gyu.engdu.domain.user.application.ChangeUserNameService;
import com.gyu.engdu.domain.user.application.DeleteUserService;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.application.dto.response.UserSummaryResponse;
import com.gyu.engdu.domain.user.presentation.dto.request.ChangeUserNameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserQueryService userQueryService;
  private final DeleteUserService deleteUserService;
  private final ChangeUserNameService changeUserNameService;

  @GetMapping("/me")
  public ResponseEntity<UserSummaryResponse> readMySummaryInfo(
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    return ResponseEntity.ok(userQueryService.findSummaryUserInfo(userId));
  }

  @DeleteMapping("/withdrawal")
  public ResponseEntity<Void> withdrawAccount(
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    deleteUserService.delete(userId);
    return ResponseEntity.noContent()
        .build();
  }

  @PatchMapping("/name")
  public ResponseEntity<Void> updateUserName(
      @RequestBody @Valid ChangeUserNameRequest req,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    changeUserNameService.changeName(userId, req.name());
    return ResponseEntity.ok().build();
  }
}
