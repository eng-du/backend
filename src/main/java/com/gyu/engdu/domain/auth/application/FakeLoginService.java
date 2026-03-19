package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.User;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FakeLoginService {

    private final CreateTokenService createTokenService;
    private final PersistTokenService persistTokenService;
    private final UserQueryService userQueryService;

    public AuthTokenServiceResponse fakeLogin(Long userId) {
        User user = userQueryService.findExistingUser(userId);
        AuthTokenServiceResponse authToken = createTokenService.createAuthToken(
                user.getId(), user.getRole(), new Date());
        persistTokenService.persistRefreshToken(authToken.refreshToken());
        return authToken;
    }
}
