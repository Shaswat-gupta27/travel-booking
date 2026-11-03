package io.bookingmicroservices.flight.aircrafts.exceptions;

import buildingblocks.core.exception.NotFoundException;

public class AircraftNotFoundException extends NotFoundException {
  public AircraftNotFoundException() {
    super("Aircraft not found!");
  }
}
