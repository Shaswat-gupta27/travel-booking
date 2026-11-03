package io.bookingmicroservices.flight.airports.dtos;

import java.util.UUID;

public record AirportDto(
  UUID id,
  String name,
  String code,
  String address,
  String city,
  String region,
  String country
) { }

