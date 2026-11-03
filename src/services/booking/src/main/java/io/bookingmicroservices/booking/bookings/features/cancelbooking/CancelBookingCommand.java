package io.bookingmicroservices.booking.bookings.features.cancelbooking;

import buildingblocks.core.event.InternalCommand;
import buildingblocks.mediator.abstractions.commands.ICommand;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import java.util.UUID;

public record CancelBookingCommand(UUID id, String userId, boolean isAdmin) implements ICommand<BookingDto>, InternalCommand {
}
