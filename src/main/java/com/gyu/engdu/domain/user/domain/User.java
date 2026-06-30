package com.gyu.engdu.domain.user.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.user.exception.UserNameTooLongException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_provider_sub", columnNames = { "provider", "sub" }) })

public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  private String email;

  @Column(length = 30, nullable = false)
  private String name;

  private String sub;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private OAuthProvider provider;

  @Builder
  private User(String email, Role role, String sub, String name, OAuthProvider provider) {
    this.email = email;
    this.role = role;
    this.sub = sub;
    this.name = name;
    this.provider = provider;
  }

  public static User of(String email, Role role, String sub, String name, OAuthProvider provider) {
    return User.builder()
        .email(email)
        .role(role)
        .sub(sub)
        .name(name)
        .provider(provider)
        .build();
  }

  public void changeName(String newName) {
    if (newName.length() > 30) {
      throw new UserNameTooLongException(this.id, newName);
    }
    this.name = newName;
  }
}