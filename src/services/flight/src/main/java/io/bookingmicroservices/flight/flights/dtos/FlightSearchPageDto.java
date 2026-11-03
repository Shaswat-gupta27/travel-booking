package io.bookingmicroservices.flight.flights.dtos;

import java.util.List;

public record FlightSearchPageDto(
  List<FlightSearchResultDto> items,
  long total,
  int page,
  int size
) { }
