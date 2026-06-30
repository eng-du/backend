package com.gyu.engdu.domain.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.user.exception.UserNameTooLongException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  @DisplayName("OAuth 제공자 정보를 포함하여 유저 엔티티를 정상적으로 생성한다")
  void shouldCreateUserWithProvider() {
    // given
    String email = "test@test.com";
    Role role = Role.ROLE_USER;
    String sub = "sub_123";
    String name = "TestUser";
    OAuthProvider provider = OAuthProvider.GOOGLE;

    // when
    User user = User.of(email, role, sub, name, provider);

    // then
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getRole()).isEqualTo(role);
    assertThat(user.getSub()).isEqualTo(sub);
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getProvider()).isEqualTo(provider);
  }

  @Test
  @DisplayName("이름 변경 시 30자를 초과하면 예외가 발생한다")
  void shouldThrowExceptionWhenNameIsTooLong() {
    // given
    User user = User.of("test@test.com", Role.ROLE_USER, "sub", "oldName", OAuthProvider.GOOGLE);
    String tooLongName = "1234567890123456789012345678901"; // 31 characters

    // when & then
    assertThatThrownBy(() -> user.changeName(tooLongName))
        .isInstanceOf(UserNameTooLongException.class);
  }
}
