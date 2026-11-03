package io.bookingmicroservices.flight.airports.features.getairports;

import io.bookingmicroservices.flight.airports.features.AirportMatcher;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.bookingmicroservices.flight.airports.features.Mappings;
import io.bookingmicroservices.flight.data.mongo.documents.AirportDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AirportReadRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class GetAirportsQueryHandler implements IQueryHandler<GetAirportsQuery, List<AirportDto>> {
  private final AirportReadRepository airportReadRepository;

  public GetAirportsQueryHandler(AirportReadRepository airportReadRepository) {
    this.airportReadRepository = airportReadRepository;
  }

  @Override
  public List<AirportDto> handle(GetAirportsQuery query) {
    // Airports are small reference data, so filtering in memory keeps this simple.
    return airportReadRepository.findAllByIsDeletedFalse().stream()
      .filter(airport -> matchesCity(airport, query.city()))
      .filter(airport -> AirportMatcher.matches(airport, query.q()))
      .sorted(Comparator.comparing((AirportDocument airport) -> lower(airport.getCity()))
        .thenComparing(airport -> lower(airport.getName())))
      .map(Mappings::toAirportDto)
      .toList();
  }

  private static boolean matchesCity(AirportDocument airport, String city) {
    return city == null || city.isBlank() || lower(airport.getCity()).equals(lower(city.trim()));
  }

  private static String lower(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }
}
