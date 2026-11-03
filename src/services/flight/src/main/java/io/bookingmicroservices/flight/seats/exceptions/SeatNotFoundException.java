package io.bookingmicroservices.flight.seats.exceptions;

import buildingblocks.core.exception.NotFoundException;

public class SeatNotFoundException extends NotFoundException {
  public SeatNotFoundException() {
    super("Seat not found!");
  }
}
