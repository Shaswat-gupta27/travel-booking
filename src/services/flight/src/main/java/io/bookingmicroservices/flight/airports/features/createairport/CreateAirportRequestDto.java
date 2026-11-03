package io.bookingmicroservices.flight.airports.features.createairport;

public record CreateAirportRequestDto(
  String name,
  String code,
  String address,
  String city,
  String region,
  String country){
}

