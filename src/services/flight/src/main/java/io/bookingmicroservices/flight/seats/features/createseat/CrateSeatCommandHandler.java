package io.bookingmicroservices.flight.seats.features.createseat;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import io.bookingmicroservices.flight.data.jpa.entities.SeatEntity;
import io.bookingmicroservices.flight.data.jpa.repositories.FlightRepository;
import io.bookingmicroservices.flight.data.jpa.repositories.SeatRepository;
import io.bookingmicroservices.flight.flights.exceptions.FlightNotFoundException;
import io.bookingmicroservices.flight.seats.dtos.SeatDto;
import io.bookingmicroservices.flight.seats.exceptions.SeatAlreadyExistException;
import io.bookingmicroservices.flight.seats.exceptions.SeatNumberAlreadyExistException;
import io.bookingmicroservices.flight.seats.features.Mappings;
import io.bookingmicroservices.flight.seats.models.Seat;
import io.bookingmicroservices.flight.seats.valueobjects.FlightId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatNumber;
import org.springframework.stereotype.Service;

@Service
public class CrateSeatCommandHandler implements ICommandHandler<CreateSeatCommand, SeatDto> {

  private final SeatRepository seatRepository;
  private final FlightRepository flightRepository;

  public CrateSeatCommandHandler(SeatRepository seatRepository, FlightRepository flightRepository) {
    this.seatRepository = seatRepository;
    this.flightRepository = flightRepository;
  }

  @Override
  public SeatDto handle(CreateSeatCommand command) {

    SeatEntity existSeat = seatRepository.findSeatByIdAndIsDeletedFalse(command.id());
    if (existSeat!= null) {
      throw new SeatAlreadyExistException();
    }

    if (flightRepository.findFlightByIdAndIsDeletedFalse(command.flightId()) == null) {
      throw new FlightNotFoundException();
    }

    if (seatRepository.existsByFlightIdAndSeatNumber(new FlightId(command.flightId()), new SeatNumber(command.seatNumber()))) {
      throw new SeatNumberAlreadyExistException();
    }

    Seat seat = Seat.create(
      new SeatId(command.id()),
      new SeatNumber(command.seatNumber()),
      command.seatType(),
      command.seatClass(),
      new FlightId(command.flightId())
    );

    SeatEntity seatEntity = Mappings.toSeatEntity(seat);

    SeatEntity seatCreated = seatRepository.save(seatEntity);
    return Mappings.toSeatDto(seatCreated);
  }
}
