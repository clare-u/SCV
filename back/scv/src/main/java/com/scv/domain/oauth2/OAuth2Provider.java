package com.scv.domain.oauth2;

import java.util.HashMap;
import java.util.Map;

public enum OAuth2Provider {
    GITHUB;

    private static final Map<String, OAuth2Provider> PROVIDER_MAP = new HashMap<>();

    static {
        for (OAuth2Provider provider : OAuth2Provider.values()) {
            PROVIDER_MAP.put(provider.name().toLowerCase(), provider);
        }
    }

    public static OAuth2Provider from(String oauth2Provider) {
        return PROVIDER_MAP.get(oauth2Provider);
    }
}
