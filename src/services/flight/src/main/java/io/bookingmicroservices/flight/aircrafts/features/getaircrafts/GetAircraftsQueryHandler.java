package io.bookingmicroservices.flight.aircrafts.features.getaircrafts;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.bookingmicroservices.flight.aircrafts.features.Mappings;
import io.bookingmicroservices.flight.data.mongo.documents.AircraftDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AircraftReadRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GetAircraftsQueryHandler implements IQueryHandler<GetAircraftsQuery, List<AircraftDto>> {
  private final AircraftReadRepository aircraftReadRepository;

  public GetAircraftsQueryHandler(AircraftReadRepository aircraftReadRepository) {
    this.aircraftReadRepository = aircraftReadRepository;
  }

  @Override
  public List<AircraftDto> handle(GetAircraftsQuery query) {
    return aircraftReadRepository.findAllByIsDeletedFalse().stream()
      .sorted(Comparator.comparing(AircraftDocument::getName, String.CASE_INSENSITIVE_ORDER))
      .map(Mappings::toAircraftDto)
      .toList();
  }
}
