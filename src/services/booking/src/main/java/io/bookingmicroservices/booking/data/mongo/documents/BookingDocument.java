package io.bookingmicroservices.booking.data.mongo.documents;

import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookingDocument {
    @Id
    private ObjectId id;
    private UUID bookingId;
    private String userId;
    private PassengerInfo passengerInfo;
    private Trip trip;
    private BookingStatus status;
    private boolean isDeleted;

    public BookingDocument(UUID bookingId, String userId, PassengerInfo passengerInfo, Trip trip, BookingStatus status, boolean isDeleted) {
      this.bookingId = bookingId;
      this.userId = userId;
      this.passengerInfo = passengerInfo;
      this.trip = trip;
      this.status = status;
      this.isDeleted = isDeleted;
    }
}
