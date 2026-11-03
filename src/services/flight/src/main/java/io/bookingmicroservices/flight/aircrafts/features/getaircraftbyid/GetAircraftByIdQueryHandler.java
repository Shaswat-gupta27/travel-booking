package io.bookingmicroservices.flight.aircrafts.features.getaircraftbyid;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.bookingmicroservices.flight.aircrafts.exceptions.AircraftNotFoundException;
import io.bookingmicroservices.flight.aircrafts.features.Mappings;
import io.bookingmicroservices.flight.data.mongo.documents.AircraftDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AircraftReadRepository;
import org.springframework.stereotype.Service;

@Service
public class GetAircraftByIdQueryHandler implements IQueryHandler<GetAircraftByIdQuery, AircraftDto> {
  private final AircraftReadRepository aircraftReadRepository;

  public GetAircraftByIdQueryHandler(AircraftReadRepository aircraftReadRepository) {
    this.aircraftReadRepository = aircraftReadRepository;
  }

  @Override
  public AircraftDto handle(GetAircraftByIdQuery query) {
    AircraftDocument aircraftDocument = aircraftReadRepository.findByAircraftIdAndIsDeletedFalse(query.id());
    if (aircraftDocument == null) {
      throw new AircraftNotFoundException();
    }
    return Mappings.toAircraftDto(aircraftDocument);
  }
}
