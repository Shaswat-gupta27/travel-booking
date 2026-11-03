package io.bookingmicroservices.apigateway.unittest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

// No downstream services run here, so a request that passes security ends in a 5xx from the proxy.
// "Not 401" therefore means the gateway let the request through.
@SpringBootTest
@AutoConfigureWebTestClient
public class GatewayAccessRulesTests {

  private static final String ID = "3c5c0000-97c6-fc34-2eb9-08db322230c9";

  @Autowired
  private WebTestClient client;

  private void assertPublicGet(String path) {
    client.get().uri(path).exchange().expectStatus().value(status -> assertNotEquals(401, status));
  }

  @Test
  void should_let_anonymous_browse_flights_seats_airports_and_aircraft() {
    assertPublicGet("/api/v1/flight");
    assertPublicGet("/api/v1/flight/" + ID);
    assertPublicGet("/api/v1/flight/get-available-seats/" + ID);
    assertPublicGet("/api/v1/flight/search?from=delhi&to=california");
    assertPublicGet("/api/v1/flight/airport");
    assertPublicGet("/api/v1/flight/airport/" + ID);
    assertPublicGet("/api/v1/flight/aircraft");
    assertPublicGet("/api/v1/flight/aircraft/" + ID);
  }

  @Test
  void should_require_login_to_manage_flights() {
    client.post().uri("/api/v1/flight").exchange().expectStatus().isUnauthorized();
    client.delete().uri("/api/v1/flight/" + ID).exchange().expectStatus().isUnauthorized();
    client.post().uri("/api/v1/flight/airport").exchange().expectStatus().isUnauthorized();
    client.post().uri("/api/v1/flight/aircraft").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void should_require_login_for_passenger_and_booking() {
    client.get().uri("/api/v1/passenger/" + ID).exchange().expectStatus().isUnauthorized();
    client.post().uri("/api/v1/passenger").exchange().expectStatus().isUnauthorized();
    client.post().uri("/api/v1/booking").exchange().expectStatus().isUnauthorized();
  }
}
