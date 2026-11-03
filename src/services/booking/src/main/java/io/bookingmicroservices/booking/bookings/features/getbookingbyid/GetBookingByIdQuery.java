package io.bookingmicroservices.booking.bookings.features.getbookingbyid;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import java.util.UUID;

public record GetBookingByIdQuery(UUID id, String userId, boolean isAdmin) implements IQuery<BookingDto> {
}
