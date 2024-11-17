package com.scv.global.oauth2.service;

import com.scv.global.util.AESUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    public RedisOAuth2AuthorizedClientService(@Qualifier("oauthMasterTemplate") RedisTemplate<String, Object> oauthRedisMasterTemplate) {
        this.oauthRedisMasterTemplate = oauthRedisMasterTemplate;
    }

    private final RedisTemplate<String, Object> oauthRedisMasterTemplate;

    private String generateKey(String clientRegistrationId, String principalName) {
        return String.format("%s:%s", clientRegistrationId, principalName);
    }

    public String getAccessToken(String clientRegistrationId, String principalName) {
        String key = generateKey(clientRegistrationId, principalName);
        return AESUtil.decrypt((String) oauthRedisMasterTemplate.opsForValue().get(key));
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String key = generateKey(authorizedClient.getClientRegistration().getRegistrationId(), principal.getName());

        long duration = 14;

        oauthRedisMasterTemplate.opsForValue().set(key, AESUtil.encrypt(authorizedClient.getAccessToken().getTokenValue()), duration, TimeUnit.DAYS);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        String key = generateKey(clientRegistrationId, principalName);
        oauthRedisMasterTemplate.delete(key);
    }
}
