package io.bookingmicroservices.flight.flights.features.searchflights;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.bookingmicroservices.flight.airports.features.AirportMatcher;
import io.bookingmicroservices.flight.data.mongo.documents.AircraftDocument;
import io.bookingmicroservices.flight.data.mongo.documents.AirportDocument;
import io.bookingmicroservices.flight.data.mongo.documents.FlightDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AircraftReadRepository;
import io.bookingmicroservices.flight.data.mongo.repositories.AirportReadRepository;
import io.bookingmicroservices.flight.data.mongo.repositories.FlightReadRepository;
import io.bookingmicroservices.flight.data.mongo.repositories.SeatReadRepository;
import io.bookingmicroservices.flight.flights.dtos.FlightSearchPageDto;
import io.bookingmicroservices.flight.flights.dtos.FlightSearchResultDto;
import io.bookingmicroservices.flight.flights.enums.FlightStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.bookingmicroservices.flight.aircrafts.features.Mappings.toAircraftDto;
import static io.bookingmicroservices.flight.airports.features.Mappings.toAirportDto;

@Service
public class SearchFlightsQueryHandler implements IQueryHandler<SearchFlightsQuery, FlightSearchPageDto> {
  private final FlightReadRepository flightReadRepository;
  private final AirportReadRepository airportReadRepository;
  private final AircraftReadRepository aircraftReadRepository;
  private final SeatReadRepository seatReadRepository;
  private final Clock clock;

  public SearchFlightsQueryHandler(
    FlightReadRepository flightReadRepository,
    AirportReadRepository airportReadRepository,
    AircraftReadRepository aircraftReadRepository,
    SeatReadRepository seatReadRepository,
    Clock clock) {
    this.flightReadRepository = flightReadRepository;
    this.airportReadRepository = airportReadRepository;
    this.aircraftReadRepository = aircraftReadRepository;
    this.seatReadRepository = seatReadRepository;
    this.clock = clock;
  }

  @Override
  public FlightSearchPageDto handle(SearchFlightsQuery query) {
    // Airports and aircraft are small reference data, so they are loaded once and matched in memory.
    List<AirportDocument> airports = airportReadRepository.findAllByIsDeletedFalse();
    Set<UUID> fromAirportIds = idsMatching(airports, query.from());
    Set<UUID> toAirportIds = idsMatching(airports, query.to());

    if (fromAirportIds.isEmpty() || toAirportIds.isEmpty()) {
      return new FlightSearchPageDto(List.of(), 0, query.page(), query.size());
    }

    LocalDateTime now = LocalDateTime.now(clock);

    List<FlightDocument> flights = flightReadRepository
      .findAllByDepartureAirportIdInAndArriveAirportIdInAndIsDeletedFalse(fromAirportIds, toAirportIds).stream()
      .filter(flight -> flight.getStatus() != FlightStatus.Canceled && flight.getStatus() != FlightStatus.Completed)
      .filter(flight -> flight.getDepartureDate().isAfter(now))
      .filter(flight -> query.date() == null || flight.getDepartureDate().toLocalDate().equals(query.date()))
      .sorted(comparator(query.sort()))
      .toList();

    long offset = (long) (query.page() - 1) * query.size();
    List<FlightDocument> pageOfFlights = offset >= flights.size()
      ? List.of()
      : flights.subList((int) offset, (int) Math.min(flights.size(), offset + query.size()));

    Map<UUID, AirportDocument> airportsById = airports.stream()
      .collect(Collectors.toMap(AirportDocument::getAirportId, Function.identity()));
    Map<UUID, AircraftDocument> aircraftsById = aircraftReadRepository.findAllByIsDeletedFalse().stream()
      .collect(Collectors.toMap(AircraftDocument::getAircraftId, Function.identity()));

    List<FlightSearchResultDto> items = pageOfFlights.stream()
      .map(flight -> toResult(flight, airportsById, aircraftsById))
      .toList();

    return new FlightSearchPageDto(items, flights.size(), query.page(), query.size());
  }

  private FlightSearchResultDto toResult(FlightDocument flight, Map<UUID, AirportDocument> airportsById, Map<UUID, AircraftDocument> aircraftsById) {
    AirportDocument departure = airportsById.get(flight.getDepartureAirportId());
    AirportDocument arrival = airportsById.get(flight.getArriveAirportId());
    AircraftDocument aircraft = aircraftsById.get(flight.getAircraftId());

    return new FlightSearchResultDto(
      flight.getFlightId(),
      flight.getFlightNumber(),
      flight.getDepartureDate(),
      flight.getArriveDate(),
      flight.getDurationMinutes(),
      flight.getPrice(),
      flight.getStatus(),
      departure == null ? null : toAirportDto(departure),
      arrival == null ? null : toAirportDto(arrival),
      aircraft == null ? null : toAircraftDto(aircraft),
      seatReadRepository.countByFlightIdAndIsDeletedFalse(flight.getFlightId()));
  }

  private static Set<UUID> idsMatching(List<AirportDocument> airports, String text) {
    return airports.stream()
      .filter(airport -> AirportMatcher.matches(airport, text))
      .map(AirportDocument::getAirportId)
      .collect(Collectors.toSet());
  }

  private static Comparator<FlightDocument> comparator(String sort) {
    Comparator<FlightDocument> byDeparture = Comparator.comparing(FlightDocument::getDepartureDate);
    return switch (sort) {
      case "price" -> Comparator.comparing(FlightDocument::getPrice).thenComparing(byDeparture);
      case "duration" -> Comparator.comparing(FlightDocument::getDurationMinutes).thenComparing(byDeparture);
      default -> byDeparture;
    };
  }
}
