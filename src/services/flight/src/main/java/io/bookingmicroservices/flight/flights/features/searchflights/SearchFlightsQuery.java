package io.bookingmicroservices.flight.flights.features.searchflights;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.flight.flights.dtos.FlightSearchPageDto;

import java.time.LocalDate;

// from/to: text matched against airport name, code, city, region and country. date: departure day, optional.
// sort: departure | price | duration. page starts at 1.
public record SearchFlightsQuery(
  String from,
  String to,
  LocalDate date,
  String sort,
  int page,
  int size
) implements IQuery<FlightSearchPageDto> {
}
