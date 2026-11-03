package io.bookingmicroservices.flight.airports.features.createairport;

import buildingblocks.core.event.DomainEvent;
import java.util.UUID;

public record AirportCreatedDomainEvent(
  UUID id,
  String name,
  String code,
  String address,
  String city,
  String region,
  String country,
  boolean isDeleted) implements DomainEvent {
}

