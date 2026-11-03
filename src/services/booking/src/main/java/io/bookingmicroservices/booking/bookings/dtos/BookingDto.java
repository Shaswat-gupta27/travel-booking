package io.bookingmicroservices.booking.bookings.dtos;

import io.bookingmicroservices.booking.bookings.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingDto(
        UUID id,
        String name,
        String flightNumber,
        UUID aircraftId,
        BigDecimal price,
        LocalDateTime flightDate,
        String seatNumber,
        UUID departureAirportId,
        UUID arriveAirportId,
        String description,
        UUID flightId,
        LocalDateTime departureDate,
        LocalDateTime arriveDate,
        BookingStatus status
) { }
