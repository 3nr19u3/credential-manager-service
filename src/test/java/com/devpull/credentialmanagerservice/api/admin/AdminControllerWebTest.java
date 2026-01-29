package com.devpull.credentialmanagerservice.api.admin;

import com.devpull.credentialmanagerservice.api.v1.admin.AdminController;
import com.devpull.credentialmanagerservice.application.credentials.CredentialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.devpull.credentialmanagerservice.shared.error.GlobalErrorHandler;
import com.devpull.credentialmanagerservice.infrastructure.config.JacksonConfig;

@WebFluxTest(controllers = AdminController.class)
@Import({
        GlobalErrorHandler.class,
        JacksonConfig.class,
        AdminControllerWebTest.TestSecurityConfig.class
})
class AdminControllerWebTest {
    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    CredentialService credentialService;

    @Test
    void updateStatus_invalid_returns400() {
        webTestClient.put()
                .uri("/api/v1/admin/credentials/2/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"status\":\"FOO\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BAD_REQUEST");
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityWebFilterChain testChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .build();
        }
    }
}
