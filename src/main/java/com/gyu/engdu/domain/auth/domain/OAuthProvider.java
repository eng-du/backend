package com.gyu.engdu.domain.auth.domain;

import java.util.Arrays;

public enum OAuthProvider {
    GOOGLE,
    KAKAO;

    public static OAuthProvider fromString(String providerStr) {
        if (providerStr == null || providerStr.isBlank()) {
            throw new IllegalArgumentException("제공자 이름이 비어있습니다.");
        }
        
        return Arrays.stream(OAuthProvider.values())
            .filter(provider -> provider.name().equalsIgnoreCase(providerStr))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + providerStr));
    }
}
