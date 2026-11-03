package io.bookingmicroservices.booking.unittest.bookings.features;

import buildingblocks.testbase.UnitTestBase;
import buildingblocks.utils.protobuf.ProtobufUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import flight.Flight;
import flight.FlightServiceGrpc;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.exceptions.PassengerNotFoundException;
import io.bookingmicroservices.booking.bookings.exceptions.SeatNumberIsNotAvailableException;
import io.bookingmicroservices.booking.bookings.features.createbooking.CreateBookingCommand;
import io.bookingmicroservices.booking.bookings.features.createbooking.CreateBookingCommandHandler;
import io.bookingmicroservices.booking.data.jpa.entities.BookingEntity;
import io.bookingmicroservices.booking.data.jpa.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import passenger.Passenger;
import passenger.PassengerServiceGrpc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateBookingTests extends UnitTestBase {
  private static final LocalDateTime DEPARTURE = LocalDateTime.of(2026, 11, 1, 10, 0);
  private static final LocalDateTime ARRIVAL = LocalDateTime.of(2026, 11, 1, 13, 30);

  @Mock
  private FlightServiceGrpc.FlightServiceBlockingStub flightServiceBlockingStub;

  @Mock
  private PassengerServiceGrpc.PassengerServiceBlockingStub passengerServiceBlockingStub;

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private CreateBookingCommandHandler createBookingCommandHandler;

  private CreateBookingCommand command;
  private String passengerOwner = "user-1";

  private void arrange(double price) {
    command = new CreateBookingCommand(
      UuidCreator.getTimeOrderedEpoch(), UUID.randomUUID(), UUID.randomUUID(), "Vacation trip", "user-1", false);

    Flight.FlightResponseDto flight = Flight.FlightResponseDto.newBuilder()
      .setId(command.flightId().toString())
      .setFlightNumber("BA-202")
      .setAircraftId(UUID.randomUUID().toString())
      .setDepartureAirportId(UUID.randomUUID().toString())
      .setArriveAirportId(UUID.randomUUID().toString())
      .setFlightDate(ProtobufUtils.toProtobufTimestamp(DEPARTURE))
      .setDepartureDate(ProtobufUtils.toProtobufTimestamp(DEPARTURE))
      .setArriveDate(ProtobufUtils.toProtobufTimestamp(ARRIVAL))
      .setPrice(price)
      .build();

    when(flightServiceBlockingStub.getById(any())).thenReturn(flight);
    when(passengerServiceBlockingStub.getById(any()))
      .thenReturn(Passenger.PassengerResponseDto.newBuilder().setName("Jane Doe").setUserId(passengerOwner).build());
    when(bookingRepository.findBookingByIdAndIsDeletedFalse(any())).thenReturn(null);
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
  }

  private void availableSeats(Flight.SeatResponseDto... seats) {
    when(flightServiceBlockingStub.getAvailableSeats(any()))
      .thenReturn(Flight.GetAvailableSeatsResponseDto.newBuilder().addAllSeatsDto(List.of(seats)).build());
  }

  private Flight.SeatResponseDto seat(String seatNumber, String flightId) {
    return Flight.SeatResponseDto.newBuilder().setSeatNumber(seatNumber).setFlightId(flightId).build();
  }

  @Test
  public void should_book_with_the_price_of_the_flight() {
    arrange(250.50);
    availableSeats(seat("12A", command.flightId().toString()));

    BookingDto result = createBookingCommandHandler.handle(command);

    assertEquals(0, new BigDecimal("250.50").compareTo(result.price()));
  }

  @Test
  public void should_save_owner_flight_times_and_confirmed_status_on_the_booking() {
    arrange(100);
    availableSeats(seat("12A", command.flightId().toString()));

    BookingDto result = createBookingCommandHandler.handle(command);

    ArgumentCaptor<BookingEntity> saved = ArgumentCaptor.forClass(BookingEntity.class);
    verify(bookingRepository).save(saved.capture());
    assertEquals("user-1", saved.getValue().getUserId());
    assertEquals(command.flightId(), result.flightId());
    assertEquals(DEPARTURE, result.departureDate());
    assertEquals(ARRIVAL, result.arriveDate());
    assertEquals(BookingStatus.Confirmed, result.status());
  }

  @Test
  public void should_only_book_a_seat_that_belongs_to_the_flight() {
    arrange(100);
    availableSeats(
      seat("1A", UUID.randomUUID().toString()),
      seat("12A", command.flightId().toString()));

    BookingDto result = createBookingCommandHandler.handle(command);

    assertEquals("12A", result.seatNumber());
  }

  @Test
  public void should_fail_without_saving_when_no_seat_of_the_flight_is_available() {
    arrange(100);
    availableSeats(seat("1A", UUID.randomUUID().toString()));

    assertThrows(SeatNumberIsNotAvailableException.class, () -> createBookingCommandHandler.handle(command));
    verify(bookingRepository, never()).save(any(BookingEntity.class));
    verify(flightServiceBlockingStub, never()).reserveSeat(any());
  }

  @Test
  public void should_refuse_a_passenger_that_belongs_to_someone_else() {
    passengerOwner = "someone-else";
    arrange(100);
    availableSeats(seat("12A", command.flightId().toString()));

    assertThrows(PassengerNotFoundException.class, () -> createBookingCommandHandler.handle(command));
    verify(bookingRepository, never()).save(any(BookingEntity.class));
    verify(flightServiceBlockingStub, never()).reserveSeat(any());
  }

  @Test
  public void should_let_an_admin_book_for_any_passenger() {
    passengerOwner = "someone-else";
    arrange(100);
    availableSeats(seat("12A", command.flightId().toString()));
    CreateBookingCommand adminCommand = new CreateBookingCommand(command.id(), command.passengerId(), command.flightId(), "desc", "admin-1", true);

    BookingDto result = createBookingCommandHandler.handle(adminCommand);

    assertEquals("12A", result.seatNumber());
  }

  @Test
  public void should_refuse_a_passenger_without_an_owner_for_a_traveller() {
    passengerOwner = "";
    arrange(100);
    availableSeats(seat("12A", command.flightId().toString()));

    assertThrows(PassengerNotFoundException.class, () -> createBookingCommandHandler.handle(command));
  }
}
