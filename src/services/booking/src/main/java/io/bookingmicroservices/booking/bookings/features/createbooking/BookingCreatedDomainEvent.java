package io.bookingmicroservices.booking.bookings.features.createbooking;

import buildingblocks.core.event.DomainEvent;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;

import java.util.UUID;

public record BookingCreatedDomainEvent(
        UUID id,
        String userId,
        PassengerInfo passengerInfo,
        Trip trip,
        BookingStatus status,
        boolean isDeleted) implements DomainEvent {
}
