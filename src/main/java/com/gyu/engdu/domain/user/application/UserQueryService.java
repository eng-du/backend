package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
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
}
