package io.bookingmicroservices.booking.bookings.features.getmybookings;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import java.util.List;

public record GetMyBookingsQuery(String userId) implements IQuery<List<BookingDto>> {
}
