package io.bookingmicroservices.passenger.data.jpa.entities;

import buildingblocks.core.model.BaseEntity;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;
import io.bookingmicroservices.passenger.passengers.valueobjects.Age;
import io.bookingmicroservices.passenger.passengers.valueobjects.Name;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassportNumber;
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
@Table(name = "passengers")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PassengerEntity extends BaseEntity<UUID> {

    private String userId;
    @Embedded
    private Name name;
    @Embedded
    private PassportNumber passportNumber;
    @Enumerated(EnumType.STRING)
    private PassengerType passengerType;
    @Embedded
    private Age age;

    public PassengerEntity(UUID id, String userId, Name name, PassportNumber passportNumber, PassengerType passengerType, Age age) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.passportNumber = passportNumber;
        this.passengerType = passengerType;
        this.age = age;
    }

    public PassengerEntity(UUID id, String userId, Name name, PassportNumber passportNumber, PassengerType passengerType, Age age, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModified, Long lastModifiedBy, Long version, boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.passportNumber = passportNumber;
        this.passengerType = passengerType;
        this.age = age;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModified = lastModified;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
        this.isDeleted = isDeleted;
    }
}
