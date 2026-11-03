package io.bookingmicroservices.passenger.passengers.features.getmypassengers;

import buildingblocks.mediator.abstractions.queries.IQuery;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import java.util.List;

public record GetMyPassengersQuery(String userId) implements IQuery<List<PassengerDto>> {
}
