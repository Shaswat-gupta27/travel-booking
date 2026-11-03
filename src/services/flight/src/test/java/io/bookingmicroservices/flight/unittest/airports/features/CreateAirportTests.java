package io.bookingmicroservices.flight.unittest.airports.features;

import buildingblocks.testbase.UnitTestBase;
import com.github.f4b6a3.uuid.UuidCreator;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.bookingmicroservices.flight.airports.exceptions.AirportAlreadyExistException;
import io.bookingmicroservices.flight.airports.features.createairport.CreateAirportCommand;
import io.bookingmicroservices.flight.airports.features.createairport.CreateAirportCommandHandler;
import io.bookingmicroservices.flight.airports.valueobjects.Address;
import io.bookingmicroservices.flight.airports.valueobjects.Code;
import io.bookingmicroservices.flight.airports.valueobjects.Location;
import io.bookingmicroservices.flight.airports.valueobjects.Name;
import io.bookingmicroservices.flight.data.jpa.entities.AirportEntity;
import io.bookingmicroservices.flight.data.jpa.repositories.AirportRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateAirportTests extends UnitTestBase {
  @Mock
  private AirportRepository airportRepository;

  @InjectMocks
  private CreateAirportCommandHandler createAirportCommandHandler;

  private CreateAirportCommand command() {
    return new CreateAirportCommand(UuidCreator.getTimeOrderedEpoch(), "Los Angeles International Airport", "LAX",
      "1 World Way", "Los Angeles", "California", "United States");
  }

  @Test
  public void should_create_airport_with_city_region_and_country() {
    when(airportRepository.findAirportByCodeAndIsDeletedFalse(new Code("LAX"))).thenReturn(null);
    when(airportRepository.save(any(AirportEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AirportDto result = createAirportCommandHandler.handle(command());

    assertEquals("LAX", result.code());
    assertEquals("Los Angeles", result.city());
    assertEquals("California", result.region());
    assertEquals("United States", result.country());
  }

  @Test
  public void should_throw_when_airport_code_already_exists() {
    AirportEntity existing = new AirportEntity(UUID.randomUUID(), new Name("x"), new Code("LAX"), new Address("a"), new Location("c", "r", "k"));
    when(airportRepository.findAirportByCodeAndIsDeletedFalse(new Code("LAX"))).thenReturn(existing);

    assertThrows(AirportAlreadyExistException.class, () -> createAirportCommandHandler.handle(command()));
    verify(airportRepository, never()).save(any(AirportEntity.class));
  }

  @Test
  public void should_require_city_and_country_on_a_location() {
    assertThrows(RuntimeException.class, () -> new Location("", "California", "United States"));
    assertThrows(RuntimeException.class, () -> new Location("Los Angeles", "California", null));
  }
}
