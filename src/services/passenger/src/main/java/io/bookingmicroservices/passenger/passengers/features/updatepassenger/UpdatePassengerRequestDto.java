package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import io.bookingmicroservices.passenger.passengers.enums.PassengerType;

public record UpdatePassengerRequestDto(
        String name,
        String passportNumber,
        PassengerType passengerType,
        int age) {
}
