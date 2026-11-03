package io.bookingmicroservices.booking.unittest.fakes;

import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import io.bookingmicroservices.booking.data.jpa.entities.BookingEntity;
import io.bookingmicroservices.booking.data.mongo.documents.BookingDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingFakes {
  public static Trip trip(UUID flightId, LocalDateTime departure) {
    return new Trip("BA-202", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), departure, BigDecimal.TEN,
      "Vacation trip", "12A", flightId, departure, departure.plusHours(3));
  }

  public static BookingEntity entity(UUID id, String userId, Trip trip, BookingStatus status) {
    return new BookingEntity(id, userId, new PassengerInfo("Jane Doe"), trip, status,
      LocalDateTime.now(), 1L, LocalDateTime.now(), 1L, 0L, false);
  }

  public static BookingDocument document(UUID id, String userId, LocalDateTime departure, BookingStatus status) {
    return new BookingDocument(id, userId, new PassengerInfo("Jane Doe"), trip(UUID.randomUUID(), departure), status, false);
  }
}
