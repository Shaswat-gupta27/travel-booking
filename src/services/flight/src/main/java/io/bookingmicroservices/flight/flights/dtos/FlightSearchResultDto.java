package io.bookingmicroservices.flight.flights.dtos;

import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.bookingmicroservices.flight.flights.enums.FlightStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FlightSearchResultDto(
  UUID id,
  String flightNumber,
  LocalDateTime departureDate,
  LocalDateTime arriveDate,
  BigDecimal durationMinutes,
  BigDecimal price,
  FlightStatus status,
  AirportDto departureAirport,
  AirportDto arriveAirport,
  AircraftDto aircraft,
  long seatsLeft
) { }
