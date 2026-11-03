package io.bookingmicroservices.flight.unittest.seats.features;

import buildingblocks.testbase.UnitTestBase;
import io.bookingmicroservices.flight.data.jpa.entities.SeatEntity;
import io.bookingmicroservices.flight.data.jpa.repositories.SeatRepository;
import io.bookingmicroservices.flight.seats.dtos.SeatDto;
import io.bookingmicroservices.flight.seats.enums.SeatClass;
import io.bookingmicroservices.flight.seats.enums.SeatType;
import io.bookingmicroservices.flight.seats.exceptions.SeatNotFoundException;
import io.bookingmicroservices.flight.seats.features.releaseseat.ReleaseSeatCommand;
import io.bookingmicroservices.flight.seats.features.releaseseat.ReleaseSeatCommandHandler;
import io.bookingmicroservices.flight.seats.valueobjects.FlightId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatNumber;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReleaseSeatTests extends UnitTestBase {
  @Mock
  private SeatRepository seatRepository;

  @InjectMocks
  private ReleaseSeatCommandHandler releaseSeatCommandHandler;

  private final UUID flightId = UUID.randomUUID();
  private final ReleaseSeatCommand command = new ReleaseSeatCommand("12A", flightId);

  private SeatEntity seat(boolean reserved) {
    return new SeatEntity(UUID.randomUUID(), new SeatNumber("12A"), SeatType.Window, SeatClass.Economy, new FlightId(flightId),
      LocalDateTime.now(), 1L, LocalDateTime.now(), 1L, 0L, reserved);
  }

  @Test
  public void should_free_a_reserved_seat() {
    when(seatRepository.findSeatByFlightIdAndSeatNumber(new FlightId(flightId), new SeatNumber("12A"))).thenReturn(seat(true));
    when(seatRepository.save(any(SeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    SeatDto result = releaseSeatCommandHandler.handle(command);

    ArgumentCaptor<SeatEntity> saved = ArgumentCaptor.forClass(SeatEntity.class);
    verify(seatRepository).save(saved.capture());
    assertFalse(saved.getValue().isDeleted());
    assertEquals("12A", result.seatNumber());
    assertEquals(flightId, result.flightId());
  }

  @Test
  public void should_do_nothing_when_the_seat_is_already_free() {
    when(seatRepository.findSeatByFlightIdAndSeatNumber(any(), any())).thenReturn(seat(false));

    SeatDto result = releaseSeatCommandHandler.handle(command);

    assertEquals("12A", result.seatNumber());
    verify(seatRepository, never()).save(any(SeatEntity.class));
  }

  @Test
  public void should_throw_when_the_seat_does_not_exist() {
    when(seatRepository.findSeatByFlightIdAndSeatNumber(any(), any())).thenReturn(null);

    assertThrows(SeatNotFoundException.class, () -> releaseSeatCommandHandler.handle(command));
  }
}
