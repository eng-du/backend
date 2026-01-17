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
  private final NameUserService nameUserService;
  public User create(String sub, String email) {
    String name= nameUserService.getRandomName();
    User user = User.of(email, Role.ROLE_USER, sub, name);
    userRepository.save(user);
    return user;
  }
}
