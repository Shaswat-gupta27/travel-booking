package io.bookingmicroservices.passenger.passengers.features.getpassengerbyid;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;

import java.util.UUID;

public record GetPassengerByIdQuery(
        UUID id,
        String userId,
        boolean isAdmin
) implements IQuery<PassengerDto> {
}

