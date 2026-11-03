package io.bookingmicroservices.booking.bookings.features.getbookingbyid;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.exceptions.BookingNotFoundException;
import io.bookingmicroservices.booking.bookings.features.Mappings;
import io.bookingmicroservices.booking.data.mongo.documents.BookingDocument;
import io.bookingmicroservices.booking.data.mongo.repositories.BookingReadRepository;
import org.springframework.stereotype.Service;

@Service
public class GetBookingByIdQueryHandler implements IQueryHandler<GetBookingByIdQuery, BookingDto> {
    private final BookingReadRepository bookingReadRepository;

    public GetBookingByIdQueryHandler(BookingReadRepository bookingReadRepository) {
        this.bookingReadRepository = bookingReadRepository;
    }

    @Override
    public BookingDto handle(GetBookingByIdQuery query) {
        BookingDocument booking = bookingReadRepository.findBookingByBookingIdAndIsDeletedFalse(query.id());

        // Someone else's booking looks the same as a missing one, so ids cannot be probed.
        if (booking == null || !(query.isAdmin() || query.userId().equals(booking.getUserId()))) {
            throw new BookingNotFoundException();
        }

        return Mappings.toBookingDto(booking);
    }
}
