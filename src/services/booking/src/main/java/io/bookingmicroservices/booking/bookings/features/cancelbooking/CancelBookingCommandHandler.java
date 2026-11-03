package io.bookingmicroservices.booking.bookings.features.cancelbooking;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import flight.FlightServiceGrpc;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.exceptions.BookingCannotBeCancelledException;
import io.bookingmicroservices.booking.bookings.exceptions.BookingNotFoundException;
import io.bookingmicroservices.booking.bookings.features.Mappings;
import io.bookingmicroservices.booking.bookings.modles.Booking;
import io.bookingmicroservices.booking.bookings.valueobjects.Trip;
import io.bookingmicroservices.booking.data.jpa.entities.BookingEntity;
import io.bookingmicroservices.booking.data.jpa.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import static io.bookingmicroservices.booking.bookings.features.Mappings.toReleaseSeatRequestDto;

@Service
public class CancelBookingCommandHandler implements ICommandHandler<CancelBookingCommand, BookingDto> {
    private final BookingRepository bookingRepository;
    private final FlightServiceGrpc.FlightServiceBlockingStub flightServiceBlockingStub;

    public CancelBookingCommandHandler(
            BookingRepository bookingRepository,
            FlightServiceGrpc.FlightServiceBlockingStub flightServiceBlockingStub) {
        this.bookingRepository = bookingRepository;
        this.flightServiceBlockingStub = flightServiceBlockingStub;
    }

    @Override
    public BookingDto handle(CancelBookingCommand command) {
        BookingEntity entity = bookingRepository.findBookingByIdAndIsDeletedFalse(command.id());

        // Someone else's booking looks the same as a missing one.
        if (entity == null || !(command.isAdmin() || command.userId().equals(entity.getUserId()))) {
            throw new BookingNotFoundException();
        }

        Trip trip = entity.getTrip();

        // Bookings made before flights were stored on them cannot say which seat to free.
        if (trip.getFlightId() == null) {
            throw new BookingCannotBeCancelledException();
        }

        Booking booking = Mappings.toBookingAggregate(entity);
        booking.cancel();

        BookingEntity cancelled = bookingRepository.save(Mappings.toBookingEntity(booking));

        // If this call fails the whole transaction rolls back, so the booking stays confirmed and the seat stays reserved.
        flightServiceBlockingStub.releaseSeat(toReleaseSeatRequestDto(trip.getFlightId().toString(), trip.getSeatNumber()));

        return Mappings.toBookingDto(cancelled);
    }
}
