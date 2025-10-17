package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.user.domain.Role;
import java.util.Date;

public interface TokenProvider {

  String createRawAccessToken(Long userId, Role role, Date issuedAt);

  String createRawRefreshToken(Long userId, Date issuedAt);
}
