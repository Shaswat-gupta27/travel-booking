package io.bookingmicroservices.booking.bookings.features.getmybookings;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.features.Mappings;
import io.bookingmicroservices.booking.data.mongo.documents.BookingDocument;
import io.bookingmicroservices.booking.data.mongo.repositories.BookingReadRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GetMyBookingsQueryHandler implements IQueryHandler<GetMyBookingsQuery, List<BookingDto>> {
    private final BookingReadRepository bookingReadRepository;

    public GetMyBookingsQueryHandler(BookingReadRepository bookingReadRepository) {
        this.bookingReadRepository = bookingReadRepository;
    }

    @Override
    public List<BookingDto> handle(GetMyBookingsQuery query) {
        // Cancelled bookings are included so the traveller keeps their history; newest trip first.
        return bookingReadRepository.findAllByUserIdAndIsDeletedFalse(query.userId()).stream()
                .sorted(Comparator.comparing((BookingDocument booking) -> booking.getTrip().getDepartureDate()).reversed())
                .map(Mappings::toBookingDto)
                .toList();
    }
}
