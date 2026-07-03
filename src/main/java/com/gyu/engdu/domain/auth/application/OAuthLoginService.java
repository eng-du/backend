package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserProfile;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.user.application.CreateUserService;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.User;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final Map<String, OAuthClientStrategy> oauthClients;
    private final UserQueryService userQueryService;
    private final CreateUserService createUserService;
    private final CreateTokenService createTokenService;
    private final PersistTokenService persistTokenService;

    public AuthTokenServiceResponse login(OAuthProvider provider, String code, String redirectUri) {
        // 1. 외부 API 연동하여 유저 정보 획득 (트랜잭션 없이 실행)
        OAuthClientStrategy strategy = getStrategy(provider);
        OAuthUserProfile userInfo = strategy.fetchUserInfo(code, redirectUri);

        // 2. 가입 여부 확인 및 회원가입
        User user = userQueryService.findByProviderAndSub(provider, userInfo.sub())
                .orElseGet(() -> createUserService.create(provider, userInfo.sub(), userInfo.email()));

        // 3. 자체 토큰 발급 및 저장
        AuthTokenServiceResponse authToken = createTokenService.createAuthToken(
                user.getId(), user.getRole(), new Date());
        persistTokenService.persistRefreshToken(authToken.refreshToken());

        return authToken;
    }

    private OAuthClientStrategy getStrategy(OAuthProvider provider) {
        String beanName = provider.name().toLowerCase() + "OAuthClient";
        OAuthClientStrategy strategy = oauthClients.get(beanName);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 인증 전략입니다: " + provider);
        }
        return strategy;
    }
}
