package io.bookingmicroservices.flight.unittest.seeds;

import io.bookingmicroservices.flight.data.jpa.seeds.InitialData;
import io.bookingmicroservices.flight.data.jpa.entities.SeatEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InitialDataTests {
  @Test
  void should_seed_six_airports_three_aircraft_and_nine_flights() {
    assertEquals(6, InitialData.airports.size());
    assertEquals(3, InitialData.aircrafts.size());
    assertEquals(9, InitialData.flights.size());
  }

  @Test
  void should_give_every_demo_flight_its_own_unique_seats() {
    Map<UUID, List<SeatEntity>> seatsByFlight = InitialData.seats.stream()
      .collect(Collectors.groupingBy(seat -> seat.getFlightId().getFlightId()));

    assertEquals(9, seatsByFlight.size());
    seatsByFlight.values().forEach(seats ->
      assertEquals(seats.size(), seats.stream().map(seat -> seat.getSeatNumber().getSeatNumber()).distinct().count()));
    assertTrue(seatsByFlight.values().stream().anyMatch(seats -> seats.size() == 180));
    assertTrue(InitialData.seats.stream().noneMatch(SeatEntity::isDeleted));
  }

  @Test
  void should_seed_upcoming_flights_for_the_demo_routes() {
    long upcoming = InitialData.flights.stream()
      .filter(flight -> flight.getDepartureDate().getDepartureDate().isAfter(java.time.LocalDateTime.now()))
      .count();

    assertEquals(8, upcoming);
  }

  @Test
  void should_give_the_california_airports_a_region() {
    long california = InitialData.airports.stream()
      .filter(airport -> airport.getLocation().getRegion().equals("California"))
      .count();

    assertEquals(3, california);
  }
}
