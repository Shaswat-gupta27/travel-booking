package io.bookingmicroservices.passenger.data.jpa.repositories;

import io.bookingmicroservices.passenger.data.jpa.entities.PassengerEntity;
import io.bookingmicroservices.passenger.passengers.features.Mappings;
import io.bookingmicroservices.passenger.passengers.models.Passenger;
import jakarta.persistence.EntityManager;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassportNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;


@Repository
public interface PassengerRepository extends JpaRepository<PassengerEntity, UUID> {
    PassengerEntity findPassengerByIdAndIsDeletedFalse(UUID id);
    PassengerEntity findPassengerByUserIdAndPassportNumberAndIsDeletedFalse(String userId, PassportNumber passportNumber);
}
