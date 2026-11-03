package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import buildingblocks.mediator.abstractions.requests.Unit;
import io.bookingmicroservices.passenger.data.mongo.documents.PassengerDocument;
import io.bookingmicroservices.passenger.data.mongo.repositories.PassengerReadRepository;
import io.bookingmicroservices.passenger.passengers.exceptions.PassengerNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdatePassengerMongoCommandHandler implements ICommandHandler<UpdatePassengerMongoCommand, Unit> {
    private final PassengerReadRepository passengerReadRepository;

    public UpdatePassengerMongoCommandHandler(PassengerReadRepository passengerReadRepository) {
        this.passengerReadRepository = passengerReadRepository;
    }

    @Override
    public Unit handle(UpdatePassengerMongoCommand command) {
        PassengerDocument existing = passengerReadRepository.findPassengerByPassengerIdAndIsDeletedFalse(command.id());

        if (existing == null) {
            throw new PassengerNotFoundException();
        }

        // Keep the Mongo id so the document is replaced, not duplicated.
        passengerReadRepository.save(new PassengerDocument(existing.getId(), command.id(), command.userId(), command.name(),
                command.passportNumber(), command.passengerType(), command.age(), command.isDeleted()));

        return Unit.VALUE;
    }
}
