package io.bookingmicroservices.passenger.passengers.features;

import com.github.f4b6a3.uuid.UuidCreator;
import io.bookingmicroservices.passenger.data.jpa.entities.PassengerEntity;
import io.bookingmicroservices.passenger.data.mongo.documents.PassengerDocument;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.CreatePassengerCommand;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.CreatePassengerMongoCommand;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.CreatePassengerRequestDto;
import io.bookingmicroservices.passenger.passengers.features.updatepassenger.UpdatePassengerCommand;
import io.bookingmicroservices.passenger.passengers.features.updatepassenger.UpdatePassengerRequestDto;
import io.bookingmicroservices.passenger.passengers.models.Passenger;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassengerId;

import java.util.UUID;

public final class Mappings {

    public static PassengerEntity toPassengerEntity(Passenger passenger) {
        return new PassengerEntity(
                passenger.getId().getPassengerId(),
                passenger.getUserId(),
                passenger.getName(),
                passenger.getPassportNumber(),
                passenger.getPassengerType(),
                passenger.getAge(),
                passenger.getCreatedAt(),
                passenger.getCreatedBy(),
                passenger.getLastModified(),
                passenger.getLastModifiedBy(),
                passenger.getVersion(),
                passenger.isDeleted()
        );
    }

    public static Passenger toPassengerAggregate(PassengerEntity passengerEntity) {
        return new Passenger(
                new PassengerId(passengerEntity.getId()),
                passengerEntity.getUserId(),
                passengerEntity.getName(),
                passengerEntity.getPassportNumber(),
                passengerEntity.getPassengerType(),
                passengerEntity.getAge(),
                passengerEntity.getCreatedAt(),
                passengerEntity.getCreatedBy(),
                passengerEntity.getLastModified(),
                passengerEntity.getLastModifiedBy(),
                passengerEntity.getVersion(),
                passengerEntity.isDeleted()
        );
    }

    public static PassengerDto toPassengerDto(PassengerEntity passengerEntity) {
        return new PassengerDto(
                passengerEntity.getId(),
                passengerEntity.getName().getName(),
                passengerEntity.getPassportNumber().getPassportNumber(),
                passengerEntity.getPassengerType(),
                passengerEntity.getAge().getAge(),
                passengerEntity.getUserId());
    }


    public static PassengerDocument toPassengerDocument(CreatePassengerMongoCommand createPassengerMongoCommand) {
        return new PassengerDocument(
                createPassengerMongoCommand.id(),
                createPassengerMongoCommand.userId(),
                createPassengerMongoCommand.name(),
                createPassengerMongoCommand.passportNumber(),
                createPassengerMongoCommand.passengerType(),
                createPassengerMongoCommand.age(),
                createPassengerMongoCommand.isDeleted()
        );
    }

    public static PassengerDocument toPassengerDocument(PassengerEntity passengerEntity) {
        return new PassengerDocument(
                passengerEntity.getId(),
                passengerEntity.getUserId(),
                passengerEntity.getName().getName(),
                passengerEntity.getPassportNumber().getPassportNumber(),
                passengerEntity.getPassengerType(),
                passengerEntity.getAge().getAge(),
                passengerEntity.isDeleted()
        );
    }

    public static CreatePassengerCommand toCreatePassengerCommand(CreatePassengerRequestDto passengerRequestDto, String userId) {
        return new CreatePassengerCommand(
                UuidCreator.getTimeOrderedEpoch(),
                passengerRequestDto.name(),
                passengerRequestDto.passportNumber(),
                passengerRequestDto.passengerType(),
                passengerRequestDto.age(),
                userId
        );
    }

    public static UpdatePassengerCommand toUpdatePassengerCommand(UUID id, UpdatePassengerRequestDto request, String userId, boolean isAdmin) {
        return new UpdatePassengerCommand(
                id,
                request.name(),
                request.passportNumber(),
                request.passengerType(),
                request.age(),
                userId,
                isAdmin
        );
    }


    public static PassengerDto toPassengerDto(PassengerDocument passengerDocument) {
        return new PassengerDto(
                passengerDocument.getPassengerId(),
                passengerDocument.getName(),
                passengerDocument.getPassportNumber(),
                passengerDocument.getPassengerType(),
                passengerDocument.getAge(),
                passengerDocument.getUserId()
        );
    }

    public static passenger.Passenger.PassengerResponseDto toPassengerResponseDtoGrpc(PassengerDto passengerDto) {

        return passenger.Passenger.PassengerResponseDto.newBuilder()
                .setId(passengerDto.id().toString())
                .setName(passengerDto.name())
                .setPassportNumber(passengerDto.passportNumber())
                .setPassengerType(toPassengerTypeGrpc(passengerDto.passengerType()))
                .setAge(passengerDto.age())
                .setUserId(passengerDto.userId() == null ? "" : passengerDto.userId())
                .build();
    }

    public static passenger.Passenger.PassengerType toPassengerTypeGrpc(PassengerType passengerType) {
        return switch (passengerType) {
            case Male -> passenger.Passenger.PassengerType.PASSENGER_TYPE_MALE;
            case Female -> passenger.Passenger.PassengerType.PASSENGER_TYPE_FEMALE;
            case Baby -> passenger.Passenger.PassengerType.PASSENGER_TYPE_BABY;
        };
    }
}
