package io.bookingmicroservices.booking.bookings.features.createbooking;

import buildingblocks.core.event.InternalCommand;
import buildingblocks.mediator.abstractions.commands.ICommand;
import buildingblocks.mediator.abstractions.requests.Unit;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;

import java.util.UUID;

public record CreateBookingMongoCommand(
        UUID id,
        String userId,
        PassengerInfo passengerInfo,
        Trip trip,
        BookingStatus status,
        boolean isDeleted) implements ICommand<Unit>, InternalCommand {
}
