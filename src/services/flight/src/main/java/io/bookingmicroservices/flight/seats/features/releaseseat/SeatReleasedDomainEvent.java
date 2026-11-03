package io.bookingmicroservices.flight.seats.features.releaseseat;

import buildingblocks.core.event.DomainEvent;
import io.bookingmicroservices.flight.seats.enums.SeatClass;
import io.bookingmicroservices.flight.seats.enums.SeatType;
import java.util.UUID;

public record SeatReleasedDomainEvent(
  UUID id,
  String seatNumber,
  SeatType seatType,
  SeatClass seatClass,
  UUID flightId,
  boolean isDeleted) implements DomainEvent {
}
