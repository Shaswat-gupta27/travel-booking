package io.bookingmicroservices.booking.bookings.features.cancelbooking;

import buildingblocks.core.event.DomainEvent;
import java.util.UUID;

public record BookingCancelledDomainEvent(UUID id) implements DomainEvent {
}
