package com.gyu.engdu.domain.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gyu.engdu.domain.auth.application.OAuthLoginService;
import com.gyu.engdu.domain.auth.application.ReissueTokenService;
import com.gyu.engdu.domain.auth.application.LogoutService;
import com.gyu.engdu.domain.auth.application.TokenParser;
import com.gyu.engdu.security.JwtAuthenticationProvider;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.domain.AccessToken;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.global.config.OAuthProviderConverter;
import com.gyu.engdu.global.config.WebMvcConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ WebMvcConfig.class, OAuthProviderConverter.class })
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OAuthLoginService oauthLoginService;

  @MockBean
  private ReissueTokenService reissueTokenService;

  @MockBean
  private LogoutService logoutService;

  @MockBean
  private TokenParser tokenParser;

  @MockBean
  private JwtAuthenticationProvider jwtAuthenticationProvider;

  @Test
  @DisplayName("명시된 인증 제공자로 로그인 요청을 보내면 해당 제공자를 통해 인증이 처리된다")
  void shouldRouteEndpointWithProviderPathVariable() throws Exception {
    // given
    String providerStr = "google";
    String requestUrl = "/api/v1/auth/signup/oauth/" + providerStr;
    String code = "test_code";

    AuthTokenServiceResponse expectedResponse = mock(AuthTokenServiceResponse.class);
    RefreshToken mockRefreshToken = mock(RefreshToken.class);
    AccessToken mockAccessToken = mock(AccessToken.class);
    given(mockRefreshToken.getRawToken()).willReturn("mock_refresh_token");
    given(mockAccessToken.getRawToken()).willReturn("mock_access_token");
    given(expectedResponse.refreshToken()).willReturn(mockRefreshToken);
    given(expectedResponse.accessToken()).willReturn(mockAccessToken);

    given(oauthLoginService.login(eq(OAuthProvider.GOOGLE), eq(code), any()))
        .willReturn(expectedResponse);

    // when & then
    mockMvc.perform(get(requestUrl)
        .param("code", code))
        .andExpect(status().isOk());
  }
}
