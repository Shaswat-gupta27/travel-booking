package io.bookingmicroservices.flight.seats.features.releaseseat;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import io.bookingmicroservices.flight.data.jpa.entities.SeatEntity;
import io.bookingmicroservices.flight.data.jpa.repositories.SeatRepository;
import io.bookingmicroservices.flight.seats.dtos.SeatDto;
import io.bookingmicroservices.flight.seats.exceptions.SeatNotFoundException;
import io.bookingmicroservices.flight.seats.features.Mappings;
import io.bookingmicroservices.flight.seats.models.Seat;
import io.bookingmicroservices.flight.seats.valueobjects.FlightId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatNumber;
import org.springframework.stereotype.Service;

@Service
public class ReleaseSeatCommandHandler implements ICommandHandler<ReleaseSeatCommand, SeatDto> {
  private final SeatRepository seatRepository;

  public ReleaseSeatCommandHandler(SeatRepository seatRepository) {
    this.seatRepository = seatRepository;
  }

  @Override
  public SeatDto handle(ReleaseSeatCommand command) {
    SeatEntity existSeat = seatRepository.findSeatByFlightIdAndSeatNumber(new FlightId(command.flightId()), new SeatNumber(command.seatNumber()));

    if (existSeat == null) {
      throw new SeatNotFoundException();
    }

    // Already free: releasing again is harmless, so a retried cancellation still succeeds.
    if (!existSeat.isDeleted()) {
      return Mappings.toSeatDto(existSeat);
    }

    Seat seat = Mappings.toSeatAggregate(existSeat);

    seat.releaseSeat();

    SeatEntity seatUpdated = seatRepository.save(Mappings.toSeatEntity(seat));

    return Mappings.toSeatDto(seatUpdated);
  }
}
