package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserService {

  private final UserRepository userRepository;
  private final UserQueryService userQueryService;

  public void delete(Long userId) {
    User user = userQueryService.findExistingUser(userId);
    userRepository.delete(user);
  }
}
