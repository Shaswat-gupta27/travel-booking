package io.bookingmicroservices.flight.unittest.seats.features;

import buildingblocks.testbase.UnitTestBase;
import com.github.f4b6a3.uuid.UuidCreator;
import io.bookingmicroservices.flight.data.jpa.entities.SeatEntity;
import io.bookingmicroservices.flight.data.jpa.repositories.FlightRepository;
import io.bookingmicroservices.flight.data.jpa.repositories.SeatRepository;
import io.bookingmicroservices.flight.flights.exceptions.FlightNotFoundException;
import io.bookingmicroservices.flight.seats.dtos.SeatDto;
import io.bookingmicroservices.flight.seats.enums.SeatClass;
import io.bookingmicroservices.flight.seats.enums.SeatType;
import io.bookingmicroservices.flight.seats.exceptions.SeatNumberAlreadyExistException;
import io.bookingmicroservices.flight.seats.features.createseat.CrateSeatCommandHandler;
import io.bookingmicroservices.flight.seats.features.createseat.CreateSeatCommand;
import io.bookingmicroservices.flight.seats.valueobjects.FlightId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatNumber;
import io.bookingmicroservices.flight.unittest.fakes.FlightEntityFake;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateSeatTests extends UnitTestBase {
  @Mock
  private SeatRepository seatRepository;

  @Mock
  private FlightRepository flightRepository;

  @InjectMocks
  private CrateSeatCommandHandler crateSeatCommandHandler;

  private CreateSeatCommand command() {
    return new CreateSeatCommand(UuidCreator.getTimeOrderedEpoch(), "12A", SeatType.Window, SeatClass.Economy, UUID.randomUUID());
  }

  @Test
  public void should_throw_when_the_flight_does_not_exist() {
    // Arrange
    CreateSeatCommand command = command();
    when(flightRepository.findFlightByIdAndIsDeletedFalse(command.flightId())).thenReturn(null);

    // Act && Assert
    assertThrows(FlightNotFoundException.class, () -> crateSeatCommandHandler.handle(command));
    verify(seatRepository, never()).save(any(SeatEntity.class));
  }

  @Test
  public void should_throw_when_the_seat_number_already_exists_on_the_flight() {
    // Arrange
    CreateSeatCommand command = command();
    when(flightRepository.findFlightByIdAndIsDeletedFalse(command.flightId())).thenReturn(FlightEntityFake.generate());
    when(seatRepository.existsByFlightIdAndSeatNumber(new FlightId(command.flightId()), new SeatNumber("12A"))).thenReturn(true);

    // Act && Assert
    assertThrows(SeatNumberAlreadyExistException.class, () -> crateSeatCommandHandler.handle(command));
    verify(seatRepository, never()).save(any(SeatEntity.class));
  }

  @Test
  public void should_create_the_seat_when_flight_exists_and_number_is_free() {
    // Arrange
    CreateSeatCommand command = command();
    when(flightRepository.findFlightByIdAndIsDeletedFalse(command.flightId())).thenReturn(FlightEntityFake.generate());
    when(seatRepository.existsByFlightIdAndSeatNumber(any(), any())).thenReturn(false);
    when(seatRepository.save(any(SeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    SeatDto result = crateSeatCommandHandler.handle(command);

    // Assert
    assertEquals("12A", result.seatNumber());
    assertEquals(command.flightId(), result.flightId());
    verify(seatRepository, times(1)).save(any(SeatEntity.class));
  }
}
