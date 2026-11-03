package io.bookingmicroservices.flight.seats.features.releaseseat;

import buildingblocks.core.event.InternalCommand;
import buildingblocks.mediator.abstractions.commands.ICommand;
import io.bookingmicroservices.flight.seats.dtos.SeatDto;
import java.util.UUID;

public record ReleaseSeatCommand(
  String seatNumber,
  UUID flightId
) implements ICommand<SeatDto>, InternalCommand {
}
