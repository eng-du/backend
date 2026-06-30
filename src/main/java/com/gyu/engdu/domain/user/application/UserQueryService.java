package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.user.application.dto.response.UserDetailResponse;
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

  public Optional<User> findByProviderAndSub(OAuthProvider provider, String sub) {
    return userRepository.findByProviderAndSub(provider, sub);
  }

  public User findExistingUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public UserSummaryResponse findSummaryUserInfo(Long userId) {
    User user = findExistingUser(userId);
    return UserSummaryResponse.fromEntity(user);
  }

  public UserDetailResponse findDetailUserInfo(Long userId) {
    User user = findExistingUser(userId);
    return UserDetailResponse.fromEntity(user);
  }
}
