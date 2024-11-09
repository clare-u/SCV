package com.scv.global.config;

import com.scv.domain.user.exception.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomResponseErrorhandler());
        return restTemplate;
    }

    private static class CustomResponseErrorhandler extends DefaultResponseErrorHandler {

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode = HttpStatus.valueOf(response.getStatusCode().value());

            switch (statusCode) {
                case BAD_REQUEST:
                    throw GithubBadRequestException.getInstance();
                case UNAUTHORIZED:
                    throw GithubUnauthorizedException.getInstance();
                case FORBIDDEN:
                    throw GithubForbiddenException.getInstance();
                case NOT_FOUND:
                    throw GithubNotFoundException.getInstance();
                case CONFLICT:
                    throw GithubConflictException.getInstance();
                case UNPROCESSABLE_ENTITY:
                    throw GithubUnprocessableEntityException.getInstance();
                default:
                    super.handleError(response);
            }
        }
    }
}
