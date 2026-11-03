package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import buildingblocks.core.event.DomainEvent;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;

import java.util.UUID;

public record PassengerUpdatedDomainEvent(
        UUID id,
        String userId,
        String name,
        String passportNumber,
        PassengerType passengerType,
        int age,
        boolean isDeleted) implements DomainEvent {
}
