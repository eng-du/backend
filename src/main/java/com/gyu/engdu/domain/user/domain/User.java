package com.gyu.engdu.domain.user.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(
        name = "unique_sub",
        columnNames = {"sub"}
    )})

public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  private String email;

  private String name;

  private String sub;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  private User(String email, Role role, String sub) {
    this.email = email;
    this.role = role;
    this.sub = sub;
  }

  public static User of(String email, Role role, String sub) {
    return new User(email, role, sub);
  }

}