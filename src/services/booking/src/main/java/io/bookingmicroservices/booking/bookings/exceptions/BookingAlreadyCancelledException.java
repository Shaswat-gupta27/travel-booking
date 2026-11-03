package io.bookingmicroservices.booking.bookings.exceptions;

import buildingblocks.core.exception.ConflictException;

public class BookingAlreadyCancelledException extends ConflictException {
    public BookingAlreadyCancelledException() {
        super("Booking is already cancelled!");
    }
}
