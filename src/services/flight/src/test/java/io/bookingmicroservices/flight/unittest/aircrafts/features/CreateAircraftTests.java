package io.bookingmicroservices.flight.unittest.aircrafts.features;

import buildingblocks.testbase.UnitTestBase;
import com.github.f4b6a3.uuid.UuidCreator;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.bookingmicroservices.flight.aircrafts.exceptions.AircraftAlreadyExistException;
import io.bookingmicroservices.flight.aircrafts.features.createaircraft.CreateAircraftCommand;
import io.bookingmicroservices.flight.aircrafts.features.createaircraft.CreateAircraftCommandHandler;
import io.bookingmicroservices.flight.aircrafts.valueobjects.ManufacturingYear;
import io.bookingmicroservices.flight.aircrafts.valueobjects.Model;
import io.bookingmicroservices.flight.aircrafts.valueobjects.Name;
import io.bookingmicroservices.flight.data.jpa.entities.AircraftEntity;
import io.bookingmicroservices.flight.data.jpa.repositories.AircraftRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateAircraftTests extends UnitTestBase {
  @Mock
  private AircraftRepository aircraftRepository;

  @InjectMocks
  private CreateAircraftCommandHandler createAircraftCommandHandler;

  private final CreateAircraftCommand command = new CreateAircraftCommand(UuidCreator.getTimeOrderedEpoch(), "Airbus A320", "A320-200", 2018);

  @Test
  public void should_look_the_model_up_as_a_value_object_and_create_the_aircraft() {
    when(aircraftRepository.findAircraftByModelAndIsDeletedFalse(new Model("A320-200"))).thenReturn(null);
    when(aircraftRepository.save(any(AircraftEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AircraftDto result = createAircraftCommandHandler.handle(command);

    assertEquals("A320-200", result.model());
    assertEquals(2018, result.manufacturingYear());
  }

  @Test
  public void should_throw_when_the_model_already_exists() {
    AircraftEntity existing = new AircraftEntity(UUID.randomUUID(), new Name("x"), new Model("A320-200"), new ManufacturingYear(2000));
    when(aircraftRepository.findAircraftByModelAndIsDeletedFalse(new Model("A320-200"))).thenReturn(existing);

    assertThrows(AircraftAlreadyExistException.class, () -> createAircraftCommandHandler.handle(command));
    verify(aircraftRepository, never()).save(any(AircraftEntity.class));
  }
}
