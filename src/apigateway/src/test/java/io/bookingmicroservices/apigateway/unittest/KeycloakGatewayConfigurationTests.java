package io.bookingmicroservices.apigateway.unittest;

import io.bookingmicroservices.apigateway.KeycloakGatewayConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KeycloakGatewayConfigurationTests {

  private CorsConfiguration corsFor(List<String> origins) {
    CorsConfigurationSource source = new KeycloakGatewayConfiguration().corsConfigurationSource(origins);
    return source.getCorsConfiguration(MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/flight")));
  }

  @Test
  void should_allow_configured_origin_and_refuse_others() {
    CorsConfiguration cors = corsFor(List.of("http://localhost:5173"));

    assertEquals("http://localhost:5173", cors.checkOrigin("http://localhost:5173"));
    assertNull(cors.checkOrigin("http://evil.example"));
  }

  @Test
  void should_allow_bearer_token_header_and_preflight_but_not_cookies() {
    CorsConfiguration cors = corsFor(List.of("http://localhost:5173"));

    assertNotNull(cors.checkHeaders(List.of("Authorization", "Content-Type")));
    assertNotNull(cors.checkHttpMethod(org.springframework.http.HttpMethod.OPTIONS));
    assertNotNull(cors.checkHttpMethod(org.springframework.http.HttpMethod.POST));
    assertNotEquals(Boolean.TRUE, cors.getAllowCredentials());
  }
}
