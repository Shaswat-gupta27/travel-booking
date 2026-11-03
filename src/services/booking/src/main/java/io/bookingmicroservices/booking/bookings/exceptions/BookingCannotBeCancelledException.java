package io.bookingmicroservices.booking.bookings.exceptions;

import buildingblocks.core.exception.BadRequestException;

public class BookingCannotBeCancelledException extends BadRequestException {
    public BookingCannotBeCancelledException() {
        super("This booking has no flight reference and cannot be cancelled!");
    }
}
