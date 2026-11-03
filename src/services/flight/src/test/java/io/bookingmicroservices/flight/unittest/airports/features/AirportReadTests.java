package io.bookingmicroservices.flight.unittest.airports.features;

import buildingblocks.testbase.UnitTestBase;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.bookingmicroservices.flight.airports.exceptions.AirportNotFoundException;
import io.bookingmicroservices.flight.airports.features.getairportbyid.GetAirportByIdQuery;
import io.bookingmicroservices.flight.airports.features.getairportbyid.GetAirportByIdQueryHandler;
import io.bookingmicroservices.flight.airports.features.getairports.GetAirportsQuery;
import io.bookingmicroservices.flight.airports.features.getairports.GetAirportsQueryHandler;
import io.bookingmicroservices.flight.data.mongo.documents.AirportDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AirportReadRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AirportReadTests extends UnitTestBase {
  @Mock
  private AirportReadRepository airportReadRepository;

  @InjectMocks
  private GetAirportsQueryHandler getAirportsQueryHandler;

  @InjectMocks
  private GetAirportByIdQueryHandler getAirportByIdQueryHandler;

  private final AirportDocument sfo = airport("SFO", "San Francisco International Airport", "San Francisco", "California");
  private final AirportDocument lax = airport("LAX", "Los Angeles International Airport", "Los Angeles", "California");
  private final AirportDocument del = airport("DEL", "Indira Gandhi International Airport", "Delhi", "Delhi");

  private static AirportDocument airport(String code, String name, String city, String region) {
    String country = region.equals("California") ? "United States" : "India";
    return new AirportDocument(UUID.randomUUID(), name, code, "address", city, region, country, false);
  }

  private List<String> codes(GetAirportsQuery query) {
    when(airportReadRepository.findAllByIsDeletedFalse()).thenReturn(List.of(sfo, lax, del));
    return getAirportsQueryHandler.handle(query).stream().map(AirportDto::code).toList();
  }

  @Test
  public void should_list_all_airports_sorted_by_city() {
    assertEquals(List.of("DEL", "LAX", "SFO"), codes(new GetAirportsQuery(null, null)));
  }

  @Test
  public void should_find_every_airport_of_a_region() {
    assertEquals(List.of("LAX", "SFO"), codes(new GetAirportsQuery("california", null)));
  }

  @Test
  public void should_find_airports_by_code_or_name_text() {
    assertEquals(List.of("LAX"), codes(new GetAirportsQuery("lax", null)));
    assertEquals(List.of("DEL"), codes(new GetAirportsQuery("indira", null)));
  }

  @Test
  public void should_filter_by_exact_city_ignoring_case() {
    assertEquals(List.of("LAX"), codes(new GetAirportsQuery(null, "los angeles")));
    assertEquals(List.of(), codes(new GetAirportsQuery(null, "Los")));
  }

  @Test
  public void should_return_nothing_when_no_airport_matches() {
    assertTrue(codes(new GetAirportsQuery("atlantis", null)).isEmpty());
  }

  @Test
  public void should_tolerate_old_airports_without_location() {
    AirportDocument old = new AirportDocument(UUID.randomUUID(), "Old Airport", "OLD", "address", null, null, null, false);
    when(airportReadRepository.findAllByIsDeletedFalse()).thenReturn(List.of(old));

    assertEquals(1, getAirportsQueryHandler.handle(new GetAirportsQuery("old", null)).size());
    assertTrue(getAirportsQueryHandler.handle(new GetAirportsQuery("california", null)).isEmpty());
  }

  @Test
  public void should_return_airport_by_id_with_its_city() {
    when(airportReadRepository.findByAirportIdAndIsDeletedFalse(lax.getAirportId())).thenReturn(lax);

    AirportDto result = getAirportByIdQueryHandler.handle(new GetAirportByIdQuery(lax.getAirportId()));

    assertEquals("LAX", result.code());
    assertEquals("Los Angeles", result.city());
    assertEquals("California", result.region());
    assertEquals("United States", result.country());
  }

  @Test
  public void should_throw_when_airport_does_not_exist() {
    UUID id = UUID.randomUUID();
    when(airportReadRepository.findByAirportIdAndIsDeletedFalse(id)).thenReturn(null);

    assertThrows(AirportNotFoundException.class, () -> getAirportByIdQueryHandler.handle(new GetAirportByIdQuery(id)));
  }
}
