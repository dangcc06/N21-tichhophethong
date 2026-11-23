package com.example.tichhophethong.portal;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PortalConfiguration {

    @Bean
    @Qualifier("academicRestClient")
    RestClient academicRestClient(
        @Value("${portal.academic.base-url}") String baseUrl,
        @Value("${portal.connect-timeout:2s}") Duration connectTimeout,
        @Value("${portal.read-timeout:3s}") Duration readTimeout
    ) {
        return createRestClient(baseUrl, connectTimeout, readTimeout);
    }

    @Bean
    @Qualifier("financeRestClient")
    RestClient financeRestClient(
        @Value("${portal.finance.base-url}") String baseUrl,
        @Value("${portal.connect-timeout:2s}") Duration connectTimeout,
        @Value("${portal.read-timeout:3s}") Duration readTimeout
    ) {
        return createRestClient(baseUrl, connectTimeout, readTimeout);
    }

    private RestClient createRestClient(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory =
            new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .build();
    }
}
