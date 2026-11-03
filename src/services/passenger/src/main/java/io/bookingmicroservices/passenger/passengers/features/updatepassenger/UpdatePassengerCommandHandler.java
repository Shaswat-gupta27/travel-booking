package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import io.bookingmicroservices.passenger.data.jpa.entities.PassengerEntity;
import io.bookingmicroservices.passenger.data.jpa.repositories.PassengerRepository;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.exceptions.PassengerAlreadyExistException;
import io.bookingmicroservices.passenger.passengers.exceptions.PassengerNotFoundException;
import io.bookingmicroservices.passenger.passengers.features.Mappings;
import io.bookingmicroservices.passenger.passengers.models.Passenger;
import io.bookingmicroservices.passenger.passengers.valueobjects.Age;
import io.bookingmicroservices.passenger.passengers.valueobjects.Name;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassportNumber;
import org.springframework.stereotype.Service;

@Service
public class UpdatePassengerCommandHandler implements ICommandHandler<UpdatePassengerCommand, PassengerDto> {
    private final PassengerRepository passengerRepository;

    public UpdatePassengerCommandHandler(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public PassengerDto handle(UpdatePassengerCommand command) {
        PassengerEntity entity = passengerRepository.findPassengerByIdAndIsDeletedFalse(command.id());

        // Someone else's passenger looks the same as a missing one.
        if (entity == null || !(command.isAdmin() || command.userId().equals(entity.getUserId()))) {
            throw new PassengerNotFoundException();
        }

        PassportNumber passportNumber = new PassportNumber(command.passportNumber());

        // The passport stays unique per owner; the passenger itself does not count as a duplicate.
        if (entity.getUserId() != null) {
            PassengerEntity samePassport = passengerRepository
                    .findPassengerByUserIdAndPassportNumberAndIsDeletedFalse(entity.getUserId(), passportNumber);
            if (samePassport != null && !samePassport.getId().equals(entity.getId())) {
                throw new PassengerAlreadyExistException();
            }
        }

        Passenger passenger = Mappings.toPassengerAggregate(entity);
        passenger.update(new Name(command.name()), passportNumber, command.passengerType(), new Age(command.age()));

        PassengerEntity updated = passengerRepository.save(Mappings.toPassengerEntity(passenger));

        return Mappings.toPassengerDto(updated);
    }
}
