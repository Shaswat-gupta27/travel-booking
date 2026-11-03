package io.bookingmicroservices.booking.unittest.bookings.features;

import buildingblocks.testbase.UnitTestBase;
import flight.Flight;
import flight.FlightServiceGrpc;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.exceptions.BookingAlreadyCancelledException;
import io.bookingmicroservices.booking.bookings.exceptions.BookingCannotBeCancelledException;
import io.bookingmicroservices.booking.bookings.exceptions.BookingNotFoundException;
import io.bookingmicroservices.booking.bookings.features.cancelbooking.CancelBookingCommand;
import io.bookingmicroservices.booking.bookings.features.cancelbooking.CancelBookingCommandHandler;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import io.bookingmicroservices.booking.data.jpa.entities.BookingEntity;
import io.bookingmicroservices.booking.data.jpa.repositories.BookingRepository;
import io.bookingmicroservices.booking.unittest.fakes.BookingFakes;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CancelBookingTests extends UnitTestBase {
  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private FlightServiceGrpc.FlightServiceBlockingStub flightServiceBlockingStub;

  @InjectMocks
  private CancelBookingCommandHandler cancelBookingCommandHandler;

  private final UUID bookingId = UUID.randomUUID();
  private final UUID flightId = UUID.randomUUID();
  private final Trip trip = BookingFakes.trip(flightId, LocalDateTime.of(2026, 11, 1, 10, 0));

  private void stored(BookingEntity entity) {
    when(bookingRepository.findBookingByIdAndIsDeletedFalse(bookingId)).thenReturn(entity);
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
  }

  private void assertNothingChanged() {
    verify(bookingRepository, never()).save(any(BookingEntity.class));
    verify(flightServiceBlockingStub, never()).releaseSeat(any());
  }

  @Test
  public void should_cancel_own_booking_and_release_its_seat() {
    stored(BookingFakes.entity(bookingId, "user-1", trip, BookingStatus.Confirmed));

    BookingDto result = cancelBookingCommandHandler.handle(new CancelBookingCommand(bookingId, "user-1", false));

    assertEquals(BookingStatus.Cancelled, result.status());
    ArgumentCaptor<BookingEntity> saved = ArgumentCaptor.forClass(BookingEntity.class);
    verify(bookingRepository).save(saved.capture());
    assertEquals(BookingStatus.Cancelled, saved.getValue().getStatus());
    ArgumentCaptor<Flight.ReleaseSeatRequestDto> released = ArgumentCaptor.forClass(Flight.ReleaseSeatRequestDto.class);
    verify(flightServiceBlockingStub).releaseSeat(released.capture());
    assertEquals(flightId.toString(), released.getValue().getFlightId());
    assertEquals("12A", released.getValue().getSeatNumber());
  }

  @Test
  public void should_let_an_admin_cancel_someone_elses_booking() {
    stored(BookingFakes.entity(bookingId, "user-1", trip, BookingStatus.Confirmed));

    BookingDto result = cancelBookingCommandHandler.handle(new CancelBookingCommand(bookingId, "admin-1", true));

    assertEquals(BookingStatus.Cancelled, result.status());
    verify(flightServiceBlockingStub).releaseSeat(any());
  }

  @Test
  public void should_not_find_a_booking_of_another_user() {
    stored(BookingFakes.entity(bookingId, "user-1", trip, BookingStatus.Confirmed));

    assertThrows(BookingNotFoundException.class,
      () -> cancelBookingCommandHandler.handle(new CancelBookingCommand(bookingId, "user-2", false)));
    assertNothingChanged();
  }

  @Test
  public void should_not_find_a_missing_booking() {
    stored(null);

    assertThrows(BookingNotFoundException.class,
      () -> cancelBookingCommandHandler.handle(new CancelBookingCommand(bookingId, "user-1", false)));
    assertNothingChanged();
  }

  @Test
  public void should_refuse_to_cancel_twice() {
    stored(BookingFakes.entity(bookingId, "user-1", trip, BookingStatus.Cancelled));

    assertThrows(BookingAlreadyCancelledException.class,
      () -> cancelBookingCommandHandler.handle(new CancelBookingCommand(bookingId, "user-1", false)));
    assertNothingChanged();
  }

  @Test
  public void should_refuse_to_cancel_an_old_booking_without_a_flight_reference() {
    stored(BookingFakes.entity(bookingId, "user-1", new Trip(), BookingStatus.Confirmed));

    assertThrows(BookingCannotBeCancelledException.class,
      () -> cancelBookingCommandHandler.handle(new CancelBookingCommand(bookingId, "user-1", false)));
    assertNothingChanged();
  }
}
