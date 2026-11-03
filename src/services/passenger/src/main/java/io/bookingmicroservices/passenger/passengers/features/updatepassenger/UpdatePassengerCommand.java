package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import buildingblocks.core.event.InternalCommand;
import buildingblocks.mediator.abstractions.commands.ICommand;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;

import java.util.UUID;

public record UpdatePassengerCommand(
        UUID id,
        String name,
        String passportNumber,
        PassengerType passengerType,
        int age,
        String userId,
        boolean isAdmin
) implements ICommand<PassengerDto>, InternalCommand {
}
