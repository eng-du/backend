package com.gyu.engdu.global.config;

import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OAuthProviderConverter implements Converter<String, OAuthProvider> {

    @Override
    public OAuthProvider convert(String source) {
        return OAuthProvider.fromString(source);
    }
}
