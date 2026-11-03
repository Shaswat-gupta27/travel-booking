package io.bookingmicroservices.flight.unittest.seats.models;

import io.bookingmicroservices.flight.seats.enums.SeatType;
import io.bookingmicroservices.flight.seats.models.Seat;
import io.bookingmicroservices.flight.seats.models.SeatLayout;
import io.bookingmicroservices.flight.seats.models.SeatLayout.SeatSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SeatLayoutTests {
  @Test
  void should_number_rows_of_six_with_window_middle_and_aisle_seats() {
    List<SeatSpec> seats = SeatLayout.layout(12, 6);

    assertEquals(List.of("1A", "1B", "1C", "1D", "1E", "1F", "2A", "2B", "2C", "2D", "2E", "2F"),
      seats.stream().map(SeatSpec::seatNumber).toList());
    assertEquals(List.of(SeatType.Window, SeatType.Middle, SeatType.Aisle, SeatType.Aisle, SeatType.Middle, SeatType.Window),
      seats.subList(0, 6).stream().map(SeatSpec::seatType).toList());
  }

  @Test
  void should_leave_the_last_row_partly_filled() {
    List<SeatSpec> seats = SeatLayout.layout(8, 6);

    assertEquals(8, seats.size());
    assertEquals("2B", seats.getLast().seatNumber());
  }

  @Test
  void should_support_other_row_widths() {
    assertEquals(List.of(SeatType.Window, SeatType.Aisle, SeatType.Aisle, SeatType.Window),
      SeatLayout.layout(4, 4).stream().map(SeatSpec::seatType).toList());
    assertEquals(List.of(SeatType.Window, SeatType.Middle, SeatType.Window),
      SeatLayout.layout(3, 3).stream().map(SeatSpec::seatType).toList());
  }

  @Test
  void should_never_repeat_a_seat_number() {
    assertEquals(1000, SeatLayout.layout(1000, 10).stream().map(SeatSpec::seatNumber).distinct().count());
  }

  @Test
  void should_create_unreserved_economy_seats_for_the_flight() {
    UUID flightId = UUID.randomUUID();

    List<Seat> seats = SeatLayout.createSeats(flightId, 7, 6);

    assertEquals(7, seats.size());
    assertTrue(seats.stream().noneMatch(Seat::isDeleted));
    assertTrue(seats.stream().allMatch(seat -> seat.getFlightId().getFlightId().equals(flightId)));
    assertTrue(seats.stream().allMatch(seat -> seat.getSeatClass().name().equals("Economy")));
  }
}
