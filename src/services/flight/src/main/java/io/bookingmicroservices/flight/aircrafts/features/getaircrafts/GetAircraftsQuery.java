package io.bookingmicroservices.flight.aircrafts.features.getaircrafts;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import java.util.List;

public record GetAircraftsQuery() implements IQuery<List<AircraftDto>> {
}
