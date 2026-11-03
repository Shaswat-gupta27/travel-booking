package io.bookingmicroservices.booking.bookings.modles;

import buildingblocks.core.model.AggregateRoot;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.exceptions.BookingAlreadyCancelledException;
import io.bookingmicroservices.booking.bookings.features.cancelbooking.BookingCancelledDomainEvent;
import io.bookingmicroservices.booking.bookings.features.createbooking.BookingCreatedDomainEvent;
import io.bookingmicroservices.booking.bookings.valueobjects.BookingId;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Booking extends AggregateRoot<BookingId> {

    String userId;
    PassengerInfo passengerInfo;
    Trip trip;
    BookingStatus status;

    public Booking(BookingId bookingId, String userId, PassengerInfo passengerInfo, Trip trip, BookingStatus status, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModified, Long lastModifiedBy, Long version, boolean isDeleted) {
        this.id = bookingId;
        this.userId = userId;
        this.passengerInfo = passengerInfo;
        this.trip = trip;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModified = lastModified;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
        this.isDeleted = isDeleted;
    }

    public Booking(BookingId bookingId, String userId, PassengerInfo passengerInfo, Trip trip, BookingStatus status) {
        this.id = bookingId;
        this.userId = userId;
        this.passengerInfo = passengerInfo;
        this.trip = trip;
        this.status = status;
    }

    public static Booking create(BookingId bookingId, String userId, PassengerInfo passengerInfo, Trip trip) {
        var booking = new Booking(bookingId, userId, passengerInfo, trip, BookingStatus.Confirmed);

        booking.addDomainEvent(new BookingCreatedDomainEvent(
                booking.getId().getBookingId(),
                booking.userId,
                booking.passengerInfo,
                booking.trip,
                booking.status,
                false
        ));

        return booking;
    }

    public void cancel() {
        if (this.status == BookingStatus.Cancelled) {
            throw new BookingAlreadyCancelledException();
        }

        this.status = BookingStatus.Cancelled;
        this.addDomainEvent(new BookingCancelledDomainEvent(this.getId().getBookingId()));
    }
}
