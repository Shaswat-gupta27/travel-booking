package io.bookingmicroservices.flight.seats.exceptions;

import buildingblocks.core.exception.ConflictException;

public class SeatNumberAlreadyExistException extends ConflictException {
  public SeatNumberAlreadyExistException() {
    super("Seat number already exists on this flight!");
  }
}
