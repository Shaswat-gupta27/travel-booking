package io.bookingmicroservices.passenger.passengers.features.createpassenger;

import io.bookingmicroservices.passenger.passengers.enums.PassengerType;

public record CreatePassengerRequestDto(
        String name,
        String passportNumber,
        PassengerType passengerType,
        int age){
}

