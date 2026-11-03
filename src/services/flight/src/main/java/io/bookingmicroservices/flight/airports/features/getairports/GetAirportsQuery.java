package io.bookingmicroservices.flight.airports.features.getairports;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import java.util.List;

// q: free text matched against name, code, city, region and country; city: exact city name. Both optional.
public record GetAirportsQuery(String q, String city) implements IQuery<List<AirportDto>> {
}
