package com.gyu.engdu.domain.user.application;

import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.context.ApplicationEventPublisher;
import com.gyu.engdu.domain.user.domain.event.UserDeletedEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserService {

  private final UserRepository userRepository;
  private final UserQueryService userQueryService;
  private final ApplicationEventPublisher eventPublisher;

  public void delete(Long userId) {
    User user = userQueryService.findExistingUser(userId);
    userRepository.delete(user);
    eventPublisher.publishEvent(new UserDeletedEvent(userId));
  }
}
