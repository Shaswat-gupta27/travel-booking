package io.bookingmicroservices.passenger.passengers.models;

import buildingblocks.core.model.AggregateRoot;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.PassengerCreatedDomainEvent;
import io.bookingmicroservices.passenger.passengers.features.updatepassenger.PassengerUpdatedDomainEvent;
import io.bookingmicroservices.passenger.passengers.valueobjects.Age;
import io.bookingmicroservices.passenger.passengers.valueobjects.Name;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassengerId;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassportNumber;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Passenger extends AggregateRoot<PassengerId> {

    String userId;
    Name name;
    PassportNumber passportNumber;
    PassengerType passengerType;
    Age age;

    public Passenger(PassengerId passengerId, String userId, Name name, PassportNumber passportNumber, PassengerType passengerType, Age age, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModified, Long lastModifiedBy, Long version, boolean isDeleted) {
       this.id = passengerId;
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

    public Passenger(PassengerId passengerId, String userId, Name name, PassportNumber passportNumber, PassengerType passengerType, Age age) {
        this.id = passengerId;
        this.userId = userId;
        this.name = name;
        this.passportNumber = passportNumber;
        this.passengerType = passengerType;
        this.age = age;
    }

    public static Passenger create(PassengerId passengerId, String userId, Name name, PassportNumber passportNumber, PassengerType passengerType, Age age) {
        var passenger = new Passenger(passengerId, userId, name, passportNumber, passengerType, age);

        passenger.addDomainEvent(new PassengerCreatedDomainEvent(
                passenger.id.getPassengerId(),
                passenger.userId,
                passenger.name.getName(),
                passenger.passportNumber.getPassportNumber(),
                passenger.passengerType,
                passenger.age.getAge(),
                false
        ));

        return passenger;
    }

    public void update(Name name, PassportNumber passportNumber, PassengerType passengerType, Age age) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.passengerType = passengerType;
        this.age = age;

        this.addDomainEvent(new PassengerUpdatedDomainEvent(
                this.id.getPassengerId(),
                this.userId,
                this.name.getName(),
                this.passportNumber.getPassportNumber(),
                this.passengerType,
                this.age.getAge(),
                false
        ));
    }
}
