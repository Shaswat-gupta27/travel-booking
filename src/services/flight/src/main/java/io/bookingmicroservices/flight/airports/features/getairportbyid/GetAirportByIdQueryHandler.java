package io.bookingmicroservices.flight.airports.features.getairportbyid;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.bookingmicroservices.flight.airports.exceptions.AirportNotFoundException;
import io.bookingmicroservices.flight.airports.features.Mappings;
import io.bookingmicroservices.flight.data.mongo.documents.AirportDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AirportReadRepository;
import org.springframework.stereotype.Service;

@Service
public class GetAirportByIdQueryHandler implements IQueryHandler<GetAirportByIdQuery, AirportDto> {
  private final AirportReadRepository airportReadRepository;

  public GetAirportByIdQueryHandler(AirportReadRepository airportReadRepository) {
    this.airportReadRepository = airportReadRepository;
  }

  @Override
  public AirportDto handle(GetAirportByIdQuery query) {
    AirportDocument airportDocument = airportReadRepository.findByAirportIdAndIsDeletedFalse(query.id());
    if (airportDocument == null) {
      throw new AirportNotFoundException();
    }
    return Mappings.toAirportDto(airportDocument);
  }
}
