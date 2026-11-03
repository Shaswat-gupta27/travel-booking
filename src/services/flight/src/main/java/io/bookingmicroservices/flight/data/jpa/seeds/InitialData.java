package io.bookingmicroservices.flight.data.jpa.seeds;

import io.bookingmicroservices.flight.seats.models.SeatLayout;

import io.bookingmicroservices.flight.airports.valueobjects.Location;

import com.github.f4b6a3.uuid.UuidCreator;
import io.bookingmicroservices.flight.aircrafts.valueobjects.AircraftId;
import io.bookingmicroservices.flight.aircrafts.valueobjects.ManufacturingYear;
import io.bookingmicroservices.flight.aircrafts.valueobjects.Model;
import io.bookingmicroservices.flight.airports.valueobjects.Address;
import io.bookingmicroservices.flight.airports.valueobjects.AirportId;
import io.bookingmicroservices.flight.airports.valueobjects.Code;
import io.bookingmicroservices.flight.airports.valueobjects.Name;
import io.bookingmicroservices.flight.data.jpa.entities.AircraftEntity;
import io.bookingmicroservices.flight.data.jpa.entities.AirportEntity;
import io.bookingmicroservices.flight.data.jpa.entities.FlightEntity;
import io.bookingmicroservices.flight.data.jpa.entities.SeatEntity;
import io.bookingmicroservices.flight.flights.enums.FlightStatus;
import io.bookingmicroservices.flight.flights.valueobjects.*;
import io.bookingmicroservices.flight.seats.enums.SeatClass;
import io.bookingmicroservices.flight.seats.enums.SeatType;
import io.bookingmicroservices.flight.seats.valueobjects.FlightId;
import io.bookingmicroservices.flight.seats.valueobjects.SeatNumber;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InitialData {

  public static final List<FlightEntity> flights;
  public static final List<AirportEntity> airports;
  public static final List<AircraftEntity> aircrafts;
  public static final List<SeatEntity> seats;

  static  {
    airports = new ArrayList<>();
    airports.add(new AirportEntity(UUID.fromString("3c5c0000-97c6-fc34-a0cb-08db322230c8"), new Name("Lisbon International Airport"), new Code("LIS"), new Address("12988"), new Location("Lisbon", "Lisbon", "Portugal")));
    airports.add(new AirportEntity(UUID.fromString("3c5c0000-97c6-fc34-fc3c-08db322230c8"), new Name("Sao Paulo International Airport"), new Code("BRZ"), new Address("11200"), new Location("Sao Paulo", "Sao Paulo", "Brazil")));
    airports.add(new AirportEntity(UUID.fromString("3c5c0000-97c6-fc34-0001-08db322230c8"), new Name("Indira Gandhi International Airport"), new Code("DEL"), new Address("New Delhi 110037"), new Location("Delhi", "Delhi", "India")));
    airports.add(new AirportEntity(UUID.fromString("3c5c0000-97c6-fc34-0002-08db322230c8"), new Name("Los Angeles International Airport"), new Code("LAX"), new Address("1 World Way, Los Angeles"), new Location("Los Angeles", "California", "United States")));
    airports.add(new AirportEntity(UUID.fromString("3c5c0000-97c6-fc34-0003-08db322230c8"), new Name("San Francisco International Airport"), new Code("SFO"), new Address("San Francisco, CA 94128"), new Location("San Francisco", "California", "United States")));
    airports.add(new AirportEntity(UUID.fromString("3c5c0000-97c6-fc34-0004-08db322230c8"), new Name("San Diego International Airport"), new Code("SAN"), new Address("3225 N Harbor Dr, San Diego"), new Location("San Diego", "California", "United States")));

    aircrafts = new ArrayList<>();
    aircrafts.add(new AircraftEntity(UUID.fromString("3c5c0000-97c6-fc34-fcd3-08db322230c8"), new io.bookingmicroservices.flight.aircrafts.valueobjects.Name("Boeing 737"), new Model("B737"), new ManufacturingYear(2005)));
    aircrafts.add(new AircraftEntity(UUID.fromString("3c5c0000-97c6-fc34-2e04-08db322230c9"), new io.bookingmicroservices.flight.aircrafts.valueobjects.Name("Airbus 300"), new Model("A300"), new ManufacturingYear(2000)));
    aircrafts.add(new AircraftEntity(UUID.fromString("3c5c0000-97c6-fc34-2e11-08db322230c9"), new io.bookingmicroservices.flight.aircrafts.valueobjects.Name("Airbus 320"), new Model("A320"), new ManufacturingYear(2003)));

    flights = new ArrayList<>();
    flights.add(new FlightEntity(UUID.fromString("3c5c0000-97c6-fc34-2eb9-08db322230c9"), new FlightNumber("BD467"),
      new AircraftId(aircrafts.getFirst().getId()), new AirportId(airports.getFirst().getId()), new AirportId(airports.get(1).getId()), new DurationMinutes(new BigDecimal(120)), FlightStatus.Completed, new Price(new BigDecimal(8000)), new ArriveDate(LocalDateTime.of(2022, 1, 31, 12, 0)),
      new DepartureDate(LocalDateTime.of(2022, 1, 31, 14, 0)),
      new FlightDate(LocalDateTime.of(2022, 1, 31, 13, 0))));

    seats = new ArrayList<>();
    seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber("12A"), SeatType.Window, SeatClass.Economy, new FlightId(flights.get(0).getId())));
    seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber("12B"), SeatType.Window, SeatClass.Economy, new FlightId(flights.get(0).getId())));
    seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber("12C") , SeatType.Middle, SeatClass.Economy, new FlightId(flights.get(0).getId())));
    seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber("12D"), SeatType.Middle, SeatClass.Economy, new FlightId(flights.get(0).getId())));
    seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber("12E"), SeatType.Aisle, SeatClass.Economy, new FlightId(flights.get(0).getId())));
    seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber("12F"), SeatType.Aisle, SeatClass.Economy, new FlightId(flights.get(0).getId())));

    // Demo flights for the search: dates are relative to the moment the database is first seeded.
    // Airport indexes: 0 LIS, 1 BRZ, 2 DEL, 3 LAX, 4 SFO, 5 SAN. Aircraft indexes: 0 B737, 1 A300, 2 A320.
    addDemoFlight("AI101", 1, 2, 3, 2, 22, 30, 1070, 950, 180);
    addDemoFlight("AI102", 1, 2, 4, 3, 21, 15, 1110, 980, 180);
    addDemoFlight("AI103", 2, 2, 5, 5, 23, 0, 1150, 1020, 150);
    addDemoFlight("AI201", 1, 3, 2, 4, 11, 0, 1000, 900, 180);
    addDemoFlight("AI202", 1, 4, 2, 6, 10, 30, 1030, 930, 180);
    addDemoFlight("AI203", 2, 5, 2, 8, 9, 45, 1070, 960, 150);
    addDemoFlight("TP301", 0, 0, 1, 3, 10, 0, 600, 640, 120);
    addDemoFlight("TP302", 0, 1, 0, 7, 22, 0, 570, 620, 120);
  }

  private static void addDemoFlight(String number, int aircraft, int from, int to, int daysAhead, int hour, int minute,
                                    int durationMinutes, int price, int seatCount) {
    LocalDateTime departure = LocalDateTime.now().plusDays(daysAhead).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
    UUID flightId = UuidCreator.getTimeOrderedEpoch();

    flights.add(new FlightEntity(flightId, new FlightNumber(number),
      new AircraftId(aircrafts.get(aircraft).getId()), new AirportId(airports.get(from).getId()), new AirportId(airports.get(to).getId()),
      new DurationMinutes(new BigDecimal(durationMinutes)), FlightStatus.Flying, new Price(new BigDecimal(price)),
      new ArriveDate(departure.plusMinutes(durationMinutes)), new DepartureDate(departure), new FlightDate(departure)));

    SeatLayout.layout(seatCount, SeatLayout.DEFAULT_SEATS_PER_ROW).forEach(spec ->
      seats.add(new SeatEntity(UuidCreator.getTimeOrderedEpoch(), new SeatNumber(spec.seatNumber()), spec.seatType(), SeatClass.Economy, new FlightId(flightId))));
  }
}
