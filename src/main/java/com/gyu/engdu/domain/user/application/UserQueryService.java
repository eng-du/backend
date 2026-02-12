package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.user.application.dto.response.UserSummaryResponse;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import com.gyu.engdu.domain.user.exception.UserNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

  private final UserRepository userRepository;

  public Optional<User> findUserBySub(String sub) {
    return userRepository.findBySub(sub);
  }

  public User findExistingUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public UserSummaryResponse findSummaryUserInfo(Long userId) {
    User user = findExistingUser(userId);
    return UserSummaryResponse.fromEntity(user);
  }
}
