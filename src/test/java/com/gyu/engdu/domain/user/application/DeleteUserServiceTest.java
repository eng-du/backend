package com.gyu.engdu.domain.user.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import com.gyu.engdu.domain.user.domain.event.UserDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @InjectMocks
    private DeleteUserService deleteUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("회원 삭제 시 userRepository.delete가 호출되고 UserDeletedEvent가 발행된다")
    void deleteUser_ShouldCallDeleteAndPublishEvent() {
        // given
        Long userId = 1L;
        User user = createUser("test@test.com", "sub123", "TestUser");
        given(userQueryService.findExistingUser(userId)).willReturn(user);

        // when
        deleteUserService.delete(userId);

        // then
        verify(userRepository).delete(user);
        
        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        UserDeletedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.userId()).isEqualTo(userId);
    }

    private User createUser(String email, String sub, String name) {
        return User.builder()
                .email(email)
                .role(Role.ROLE_USER)
                .sub(sub)
                .name(name)
                .build();
    }
}
