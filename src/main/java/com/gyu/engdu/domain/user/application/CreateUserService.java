package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService {

  private final UserRepository userRepository;

  public User create(String sub, String email) {
    User user = User.of(email, Role.ROLE_USER, sub);
    userRepository.save(user);
    return user;
  }
}
