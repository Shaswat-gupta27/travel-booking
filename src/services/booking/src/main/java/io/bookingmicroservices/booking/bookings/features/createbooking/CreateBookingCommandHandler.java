package io.bookingmicroservices.booking.bookings.features.createbooking;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import buildingblocks.utils.protobuf.ProtobufUtils;
import flight.Flight;
import flight.FlightServiceGrpc;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.exceptions.BookingAlreadyExistException;
import io.bookingmicroservices.booking.bookings.exceptions.FlightNotFoundException;
import io.bookingmicroservices.booking.bookings.exceptions.PassengerNotFoundException;
import io.bookingmicroservices.booking.bookings.exceptions.SeatNumberIsNotAvailableException;
import io.bookingmicroservices.booking.bookings.features.Mappings;
import io.bookingmicroservices.booking.bookings.modles.Booking;
import io.bookingmicroservices.booking.bookings.valueobjects.BookingId;
import io.bookingmicroservices.booking.bookings.valueobjects.PassengerInfo;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import io.bookingmicroservices.booking.data.jpa.entities.BookingEntity;
import io.bookingmicroservices.booking.data.jpa.repositories.BookingRepository;
import org.springframework.stereotype.Service;
import passenger.Passenger;
import passenger.PassengerServiceGrpc;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static io.bookingmicroservices.booking.bookings.features.Mappings.*;

@Service
public class CreateBookingCommandHandler implements ICommandHandler<CreateBookingCommand, BookingDto> {

    private final FlightServiceGrpc.FlightServiceBlockingStub flightServiceBlockingStub;
    private final PassengerServiceGrpc.PassengerServiceBlockingStub passengerServiceBlockingStub;
    private final BookingRepository bookingRepository;

    public CreateBookingCommandHandler(
            FlightServiceGrpc.FlightServiceBlockingStub flightServiceBlockingStub,
            PassengerServiceGrpc.PassengerServiceBlockingStub passengerServiceBlockingStub,
            BookingRepository bookingRepository) {
        this.flightServiceBlockingStub = flightServiceBlockingStub;
        this.passengerServiceBlockingStub = passengerServiceBlockingStub;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingDto handle(CreateBookingCommand command) {

       Flight.GetByIdRequestDto.newBuilder().setId(command.flightId().toString()).build();

        Flight.FlightResponseDto flight = flightServiceBlockingStub.getById(toFlightGetByIdRequestDto(command));

        if (flight == null)
        {
            throw new FlightNotFoundException();
        }

        Passenger.PassengerResponseDto passenger = passengerServiceBlockingStub.getById(toPassengerGetByIdRequestDto(command));

        if (passenger == null){
            throw new PassengerNotFoundException();
        }

        // Travellers can only book for passengers they saved themselves; admins may book for anyone.
        if (!command.isAdmin() && !command.userId().equals(passenger.getUserId())) {
            throw new PassengerNotFoundException();
        }

        Flight.SeatResponseDto emptySeat = flightServiceBlockingStub.getAvailableSeats(toGetAvailableSeatsResponseDto(command)).getSeatsDtoList().stream()
                .filter(seat -> seat.getFlightId().equalsIgnoreCase(flight.getId()))
                .findAny().orElse(null);

        if(emptySeat == null){
            throw new SeatNumberIsNotAvailableException();
        }

        var existBooking = bookingRepository.findBookingByIdAndIsDeletedFalse(command.flightId());

        if(existBooking != null){
            throw new BookingAlreadyExistException();
        }

       Booking booking = Booking.create(new BookingId(command.id()), command.userId(), new PassengerInfo(passenger.getName()), new Trip(flight.getFlightNumber(),
                UUID.fromString(flight.getAircraftId()), UUID.fromString(flight.getDepartureAirportId()),
                UUID.fromString(flight.getArriveAirportId()), ProtobufUtils.toLocalDateTime(flight.getFlightDate()),
                BigDecimal.valueOf(flight.getPrice()), command.description(), Objects.requireNonNull(emptySeat).getSeatNumber(),
                UUID.fromString(flight.getId()), ProtobufUtils.toLocalDateTime(flight.getDepartureDate()),
                ProtobufUtils.toLocalDateTime(flight.getArriveDate())));

        flightServiceBlockingStub.reserveSeat(toReserveSeatRequestDto(flight.getId(), emptySeat.getSeatNumber()));

        BookingEntity bookingEntity = Mappings.toBookingEntity(booking);
        BookingEntity createdBooking = bookingRepository.save(bookingEntity);

        return toBookingDto(createdBooking);
    }
}
