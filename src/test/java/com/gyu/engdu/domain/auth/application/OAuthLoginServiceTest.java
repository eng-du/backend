package com.gyu.engdu.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserProfile;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.user.application.CreateUserService;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

    @InjectMocks
    private OAuthLoginService oauthLoginService;

    @Mock
    private Map<String, OAuthClientStrategy> oauthClients;

    @Mock
    private OAuthClientStrategy googleOAuthClient;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private CreateUserService createUserService;

    @Mock
    private CreateTokenService createTokenService;

    @Mock
    private PersistTokenService persistTokenService;

    @Test
    @DisplayName("이미 가입된 회원의 OAuth 인증 요청 시 기존 유저 정보로 토큰을 발급한다")
    void shouldIssueTokenWhenUserAlreadyExists() {
        // given
        OAuthProvider provider = OAuthProvider.GOOGLE;
        String code = "auth_code";
        String redirectUri = "redirect_uri";
        String sub = "google_sub";
        String email = "test@gmail.com";

        OAuthUserProfile userInfo = new OAuthUserProfile(sub, email);
        User existingUser = User.of(email, Role.ROLE_USER, sub, "testUser", provider);
        AuthTokenServiceResponse expectedResponse = org.mockito.Mockito.mock(
                AuthTokenServiceResponse.class);
        com.gyu.engdu.domain.auth.domain.RefreshToken mockRefreshToken = org.mockito.Mockito.mock(
                com.gyu.engdu.domain.auth.domain.RefreshToken.class);
        given(expectedResponse.refreshToken()).willReturn(mockRefreshToken);

        given(oauthClients.get("googleOAuthClient")).willReturn(googleOAuthClient);
        given(googleOAuthClient.fetchUserInfo(code, redirectUri)).willReturn(userInfo);
        given(userQueryService.findByProviderAndSub(provider, sub)).willReturn(
                Optional.of(existingUser));
        given(createTokenService.createAuthToken(any(), any(), any())).willReturn(expectedResponse);

        // when
        AuthTokenServiceResponse result = oauthLoginService.login(provider, code, redirectUri);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(createUserService, never()).create(any(), any(), any());
        verify(persistTokenService).persistRefreshToken(any());
    }

    @Test
    @DisplayName("가입되지 않은 회원의 OAuth 인증 요청 시 새로운 회원을 생성하고 토큰을 발급한다")
    void shouldCreateUserAndIssueTokenWhenNewUser() {
        // given
        OAuthProvider provider = OAuthProvider.GOOGLE;
        String code = "auth_code";
        String redirectUri = "redirect_uri";
        String sub = "new_sub";
        String email = "new@gmail.com";
        OAuthUserProfile userInfo = new OAuthUserProfile(sub, email);
        User newUser = User.of(email, Role.ROLE_USER, sub, "newUser", provider);
        AuthTokenServiceResponse expectedResponse = org.mockito.Mockito.mock(
                AuthTokenServiceResponse.class);
        com.gyu.engdu.domain.auth.domain.RefreshToken mockRefreshToken = org.mockito.Mockito.mock(
                com.gyu.engdu.domain.auth.domain.RefreshToken.class);
        given(expectedResponse.refreshToken()).willReturn(mockRefreshToken);

        given(oauthClients.get("googleOAuthClient")).willReturn(googleOAuthClient);
        given(googleOAuthClient.fetchUserInfo(code, redirectUri)).willReturn(userInfo);
        given(userQueryService.findByProviderAndSub(provider, sub)).willReturn(Optional.empty());
        given(createUserService.create(provider, sub, email)).willReturn(newUser);
        given(createTokenService.createAuthToken(any(), any(), any())).willReturn(expectedResponse);

        // when
        AuthTokenServiceResponse result = oauthLoginService.login(provider, code, redirectUri);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(createUserService).create(provider, sub, email);
        verify(persistTokenService).persistRefreshToken(any());
    }

    @Test
    @DisplayName("지원하지 않는 인증 제공자를 요청하면 예외가 발생한다")
    void shouldThrowExceptionWhenUnsupportedProviderRequested() {
        // given
        String unsupportedProvider = "APPLE";

        // when & then
        assertThatThrownBy(() -> OAuthProvider.fromString(unsupportedProvider))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
