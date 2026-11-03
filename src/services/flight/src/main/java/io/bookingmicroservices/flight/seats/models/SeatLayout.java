package io.bookingmicroservices.flight.seats.models;

import io.bookingmicroservices.flight.seats.enums.SeatClass;
import io.bookingmicroservices.flight.seats.enums.SeatType;
import io.bookingmicroservices.flight.seats.valueobjects.FlightId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatNumber;
import com.github.f4b6a3.uuid.UuidCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Generates the seats of a flight: rows of up to `seatsPerRow` letters (A, B, C ...), all Economy.
public final class SeatLayout {
  public static final int DEFAULT_SEATS_PER_ROW = 6;
  public static final int MAX_SEATS_PER_ROW = 10;
  public static final int MAX_SEATS = 1000;

  public record SeatSpec(String seatNumber, SeatType seatType) { }

  private SeatLayout() {
  }

  // Plain data only (no domain events), so seed data can use it too.
  public static List<SeatSpec> layout(int numberOfSeats, int seatsPerRow) {
    List<SeatSpec> seats = new ArrayList<>();
    for (int index = 0; index < numberOfSeats; index++) {
      int row = index / seatsPerRow + 1;
      int position = index % seatsPerRow;
      seats.add(new SeatSpec(row + String.valueOf((char) ('A' + position)), typeAt(position, seatsPerRow)));
    }
    return seats;
  }

  // New, unreserved seats (isDeleted = false) that raise the usual SeatCreated events.
  public static List<Seat> createSeats(UUID flightId, int numberOfSeats, int seatsPerRow) {
    return layout(numberOfSeats, seatsPerRow).stream()
      .map(spec -> Seat.create(
        new SeatId(UuidCreator.getTimeOrderedEpoch()),
        new SeatNumber(spec.seatNumber()),
        spec.seatType(),
        SeatClass.Economy,
        new FlightId(flightId)))
      .toList();
  }

  // Window at both ends of a row; the two centre seats of an even-width row are aisle seats; the rest are middle.
  private static SeatType typeAt(int position, int seatsPerRow) {
    if (position == 0 || position == seatsPerRow - 1) {
      return SeatType.Window;
    }
    boolean evenWidth = seatsPerRow % 2 == 0;
    if (evenWidth && (position == seatsPerRow / 2 - 1 || position == seatsPerRow / 2)) {
      return SeatType.Aisle;
    }
    return SeatType.Middle;
  }
}
