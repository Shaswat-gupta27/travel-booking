package io.bookingmicroservices.flight.seats.features.releaseseat;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import buildingblocks.mediator.abstractions.requests.Unit;
import io.bookingmicroservices.flight.data.mongo.documents.SeatDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.SeatReadRepository;
import io.bookingmicroservices.flight.seats.features.Mappings;
import org.springframework.stereotype.Service;

@Service
public class ReleaseSeatMongoCommandHandler implements ICommandHandler<ReleaseSeatMongoCommand, Unit> {
  private final SeatReadRepository seatReadRepository;

  public ReleaseSeatMongoCommandHandler(SeatReadRepository seatReadRepository) {
    this.seatReadRepository = seatReadRepository;
  }

  @Override
  public Unit handle(ReleaseSeatMongoCommand command) {
    SeatDocument reservedSeat = seatReadRepository.findSeatByFlightIdAndSeatNumberAndIsDeletedTrue(command.flightId(), command.seatNumber());

    // Nothing to do when the read copy already shows the seat as free.
    if (reservedSeat != null) {
      seatReadRepository.save(Mappings.toReleaseSeatDocument(reservedSeat));
    }

    return Unit.VALUE;
  }
}
