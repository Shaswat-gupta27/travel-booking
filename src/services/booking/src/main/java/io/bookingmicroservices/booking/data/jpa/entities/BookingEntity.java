package io.bookingmicroservices.booking.data.jpa.entities;

import buildingblocks.core.model.BaseEntity;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "bookings")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookingEntity extends BaseEntity<UUID> {

    private String userId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @Embedded
    private PassengerInfo passengerInfo;
    @Embedded
    private Trip trip;

    public BookingEntity(UUID id, String userId, PassengerInfo passengerInfo, Trip trip, BookingStatus status, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModified, Long lastModifiedBy, Long version, boolean isDeleted) {
        this.id = id;
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

    public BookingEntity(UUID id, String userId, PassengerInfo passengerInfo, Trip trip, BookingStatus status) {
        this.id = id;
        this.userId = userId;
        this.passengerInfo = passengerInfo;
        this.trip = trip;
        this.status = status;
    }
}
