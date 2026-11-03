package io.bookingmicroservices.flight.unittest.flights.features;

import buildingblocks.testbase.UnitTestBase;
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
import io.bookingmicroservices.flight.flights.features.searchflights.SearchFlightsQuery;
import io.bookingmicroservices.flight.flights.features.searchflights.SearchFlightsQueryHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SearchFlightsTests extends UnitTestBase {
  private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 3, 12, 0);

  @Mock
  private FlightReadRepository flightReadRepository;
  @Mock
  private AirportReadRepository airportReadRepository;
  @Mock
  private AircraftReadRepository aircraftReadRepository;
  @Mock
  private SeatReadRepository seatReadRepository;

  private SearchFlightsQueryHandler handler;

  private final AirportDocument del = airport("DEL", "Indira Gandhi International Airport", "Delhi", "Delhi", "India");
  private final AirportDocument lax = airport("LAX", "Los Angeles International Airport", "Los Angeles", "California", "United States");
  private final AirportDocument sfo = airport("SFO", "San Francisco International Airport", "San Francisco", "California", "United States");
  private final AirportDocument lis = airport("LIS", "Lisbon International Airport", "Lisbon", "Lisbon", "Portugal");
  private final AircraftDocument a320 = new AircraftDocument(UUID.randomUUID(), "Airbus 320", "A320", 2003, false);

  private final FlightDocument delToLax = flight("AI101", del, lax, 2, 950, 1070, FlightStatus.Flying);
  private final FlightDocument delToSfo = flight("AI102", del, sfo, 1, 980, 1110, FlightStatus.Delay);
  private final FlightDocument canceled = flight("AI103", del, lax, 3, 900, 1000, FlightStatus.Canceled);
  private final FlightDocument completed = flight("AI104", del, lax, 4, 900, 1000, FlightStatus.Completed);
  private final FlightDocument departed = flight("AI105", del, lax, -1, 900, 1000, FlightStatus.Flying);
  private final FlightDocument laxToDel = flight("AI201", lax, del, 2, 900, 1000, FlightStatus.Flying);

  private static AirportDocument airport(String code, String name, String city, String region, String country) {
    return new AirportDocument(UUID.randomUUID(), name, code, "address", city, region, country, false);
  }

  private FlightDocument flight(String number, AirportDocument from, AirportDocument to, int daysAhead, int price, int minutes, FlightStatus status) {
    LocalDateTime departure = NOW.plusDays(daysAhead);
    return new FlightDocument(UUID.randomUUID(), number, a320.getAircraftId(), from.getAirportId(), to.getAirportId(),
      new BigDecimal(minutes), status, new BigDecimal(price), departure.plusMinutes(minutes), departure, departure, false);
  }

  @BeforeEach
  void arrange() {
    List<FlightDocument> all = List.of(delToLax, delToSfo, canceled, completed, departed, laxToDel);
    when(airportReadRepository.findAllByIsDeletedFalse()).thenReturn(List.of(del, lax, sfo, lis));
    when(aircraftReadRepository.findAllByIsDeletedFalse()).thenReturn(List.of(a320));
    // behaves like the Mongo query: only flights from one of the first ids to one of the second ids
    when(flightReadRepository.findAllByDepartureAirportIdInAndArriveAirportIdInAndIsDeletedFalse(any(), any()))
      .thenAnswer(invocation -> {
        Collection<UUID> from = invocation.getArgument(0);
        Collection<UUID> to = invocation.getArgument(1);
        return all.stream().filter(f -> from.contains(f.getDepartureAirportId()) && to.contains(f.getArriveAirportId())).toList();
      });
    when(seatReadRepository.countByFlightIdAndIsDeletedFalse(any())).thenReturn(42L);
    handler = new SearchFlightsQueryHandler(flightReadRepository, airportReadRepository, aircraftReadRepository,
      seatReadRepository, Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
  }

  private FlightSearchPageDto search(String from, String to, LocalDate date, String sort, int page, int size) {
    return handler.handle(new SearchFlightsQuery(from, to, date, sort, page, size));
  }

  private List<String> numbers(FlightSearchPageDto result) {
    return result.items().stream().map(FlightSearchResultDto::flightNumber).toList();
  }

  @Test
  public void should_find_flights_from_a_city_to_every_airport_of_a_region_earliest_first() {
    FlightSearchPageDto result = search("Delhi", "California", null, "departure", 1, 20);

    assertEquals(List.of("AI102", "AI101"), numbers(result));
    assertEquals(2, result.total());
  }

  @Test
  public void should_hide_canceled_completed_departed_and_reverse_flights() {
    FlightSearchPageDto result = search("delhi", "california", null, "departure", 1, 20);

    assertFalse(numbers(result).containsAll(List.of("AI103")));
    assertTrue(result.items().stream().noneMatch(item -> List.of("AI103", "AI104", "AI105", "AI201").contains(item.flightNumber())));
  }

  @Test
  public void should_include_airports_aircraft_and_seats_left_in_each_result() {
    FlightSearchResultDto first = search("Delhi", "San Francisco", null, "departure", 1, 20).items().getFirst();

    assertEquals("AI102", first.flightNumber());
    assertEquals(FlightStatus.Delay, first.status());
    assertEquals("DEL", first.departureAirport().code());
    assertEquals("Delhi", first.departureAirport().city());
    assertEquals("SFO", first.arriveAirport().code());
    assertEquals("California", first.arriveAirport().region());
    assertEquals("A320", first.aircraft().model());
    assertEquals(42L, first.seatsLeft());
  }

  @Test
  public void should_match_partial_text_ignoring_case() {
    assertEquals(List.of("AI102", "AI101"), numbers(search("DEL", "cali", null, "departure", 1, 20)));
    assertEquals(List.of("AI101"), numbers(search("indira", "lax", null, "departure", 1, 20)));
  }

  @Test
  public void should_filter_by_departure_day() {
    LocalDate day = delToLax.getDepartureDate().toLocalDate();

    assertEquals(List.of("AI101"), numbers(search("Delhi", "California", day, "departure", 1, 20)));
    assertTrue(search("Delhi", "California", day.plusDays(10), "departure", 1, 20).items().isEmpty());
  }

  @Test
  public void should_sort_by_price_and_by_duration() {
    assertEquals(List.of("AI101", "AI102"), numbers(search("Delhi", "California", null, "price", 1, 20)));
    assertEquals(List.of("AI101", "AI102"), numbers(search("Delhi", "California", null, "duration", 1, 20)));
  }

  @Test
  public void should_page_the_results() {
    FlightSearchPageDto second = search("Delhi", "California", null, "departure", 2, 1);

    assertEquals(List.of("AI101"), numbers(second));
    assertEquals(2, second.total());
    assertEquals(2, second.page());
    assertEquals(1, second.size());
    assertTrue(search("Delhi", "California", null, "departure", 3, 1).items().isEmpty());
  }

  @Test
  public void should_return_nothing_when_an_end_matches_no_airport() {
    FlightSearchPageDto result = search("Atlantis", "California", null, "departure", 1, 20);

    assertTrue(result.items().isEmpty());
    assertEquals(0, result.total());
  }

  @Test
  public void should_return_nothing_for_a_route_without_flights() {
    assertTrue(search("Lisbon", "California", null, "departure", 1, 20).items().isEmpty());
  }
}
