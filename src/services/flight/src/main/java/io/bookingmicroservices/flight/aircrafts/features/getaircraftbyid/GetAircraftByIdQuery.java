package io.bookingmicroservices.flight.aircrafts.features.getaircraftbyid;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import java.util.UUID;

public record GetAircraftByIdQuery(UUID id) implements IQuery<AircraftDto> {
}
