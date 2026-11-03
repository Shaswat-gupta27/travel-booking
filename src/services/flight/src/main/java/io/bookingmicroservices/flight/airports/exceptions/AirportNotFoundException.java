package io.bookingmicroservices.flight.airports.exceptions;

import buildingblocks.core.exception.NotFoundException;

public class AirportNotFoundException extends NotFoundException {
  public AirportNotFoundException() {
    super("Airport not found!");
  }
}
