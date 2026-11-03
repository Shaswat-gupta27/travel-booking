package io.bookingmicroservices.booking.bookings.exceptions;

import buildingblocks.core.exception.NotFoundException;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException() {
        super("Booking not found!");
    }
}
