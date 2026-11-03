package io.bookingmicroservices.flight.unittest.aircrafts.features;

import buildingblocks.testbase.UnitTestBase;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.bookingmicroservices.flight.aircrafts.exceptions.AircraftNotFoundException;
import io.bookingmicroservices.flight.aircrafts.features.getaircraftbyid.GetAircraftByIdQuery;
import io.bookingmicroservices.flight.aircrafts.features.getaircraftbyid.GetAircraftByIdQueryHandler;
import io.bookingmicroservices.flight.aircrafts.features.getaircrafts.GetAircraftsQuery;
import io.bookingmicroservices.flight.aircrafts.features.getaircrafts.GetAircraftsQueryHandler;
import io.bookingmicroservices.flight.data.mongo.documents.AircraftDocument;
import io.bookingmicroservices.flight.data.mongo.repositories.AircraftReadRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AircraftReadTests extends UnitTestBase {
  @Mock
  private AircraftReadRepository aircraftReadRepository;

  @InjectMocks
  private GetAircraftsQueryHandler getAircraftsQueryHandler;

  @InjectMocks
  private GetAircraftByIdQueryHandler getAircraftByIdQueryHandler;

  @Test
  public void should_list_aircraft_sorted_by_name() {
    var boeing = new AircraftDocument(UUID.randomUUID(), "Boeing 737", "B737", 2005, false);
    var airbus = new AircraftDocument(UUID.randomUUID(), "Airbus 320", "A320", 2003, false);
    when(aircraftReadRepository.findAllByIsDeletedFalse()).thenReturn(List.of(boeing, airbus));

    List<AircraftDto> result = getAircraftsQueryHandler.handle(new GetAircraftsQuery());

    assertEquals(List.of("Airbus 320", "Boeing 737"), result.stream().map(AircraftDto::name).toList());
  }

  @Test
  public void should_return_aircraft_by_id() {
    var airbus = new AircraftDocument(UUID.randomUUID(), "Airbus 320", "A320", 2003, false);
    when(aircraftReadRepository.findByAircraftIdAndIsDeletedFalse(airbus.getAircraftId())).thenReturn(airbus);

    AircraftDto result = getAircraftByIdQueryHandler.handle(new GetAircraftByIdQuery(airbus.getAircraftId()));

    assertEquals("A320", result.model());
    assertEquals(2003, result.manufacturingYear());
  }

  @Test
  public void should_throw_when_aircraft_does_not_exist() {
    UUID id = UUID.randomUUID();
    when(aircraftReadRepository.findByAircraftIdAndIsDeletedFalse(id)).thenReturn(null);

    assertThrows(AircraftNotFoundException.class, () -> getAircraftByIdQueryHandler.handle(new GetAircraftByIdQuery(id)));
  }
}
