package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeUserNameService {

  private final UserQueryService userQueryService;

  public void changeName(Long userId, String name) {
    User user = userQueryService.findExistingUser(userId);
    user.changeName(name);
  }
}
