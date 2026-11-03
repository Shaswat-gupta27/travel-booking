package io.bookingmicroservices.flight.airports.features.getairportbyid;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import java.util.UUID;

public record GetAirportByIdQuery(UUID id) implements IQuery<AirportDto> {
}
