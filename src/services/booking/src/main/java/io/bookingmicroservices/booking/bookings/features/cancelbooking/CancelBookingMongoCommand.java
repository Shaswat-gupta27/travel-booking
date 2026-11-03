package io.bookingmicroservices.booking.bookings.features.cancelbooking;

import buildingblocks.core.event.InternalCommand;
import buildingblocks.mediator.abstractions.commands.ICommand;
import buildingblocks.mediator.abstractions.requests.Unit;
import java.util.UUID;

public record CancelBookingMongoCommand(UUID id) implements ICommand<Unit>, InternalCommand {
}
