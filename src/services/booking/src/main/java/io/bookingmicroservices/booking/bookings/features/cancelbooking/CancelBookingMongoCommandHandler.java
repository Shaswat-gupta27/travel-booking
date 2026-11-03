package io.bookingmicroservices.booking.bookings.features.cancelbooking;

import buildingblocks.mediator.abstractions.commands.ICommandHandler;
import buildingblocks.mediator.abstractions.requests.Unit;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.exceptions.BookingNotFoundException;
import io.bookingmicroservices.booking.data.mongo.documents.BookingDocument;
import io.bookingmicroservices.booking.data.mongo.repositories.BookingReadRepository;
import org.springframework.stereotype.Service;

@Service
public class CancelBookingMongoCommandHandler implements ICommandHandler<CancelBookingMongoCommand, Unit> {
    private final BookingReadRepository bookingReadRepository;

    public CancelBookingMongoCommandHandler(BookingReadRepository bookingReadRepository) {
        this.bookingReadRepository = bookingReadRepository;
    }

    @Override
    public Unit handle(CancelBookingMongoCommand command) {
        BookingDocument booking = bookingReadRepository.findBookingByBookingIdAndIsDeletedFalse(command.id());

        if (booking == null) {
            throw new BookingNotFoundException();
        }

        bookingReadRepository.save(new BookingDocument(booking.getId(), booking.getBookingId(), booking.getUserId(),
                booking.getPassengerInfo(), booking.getTrip(), BookingStatus.Cancelled, booking.isDeleted()));

        return Unit.VALUE;
    }
}
