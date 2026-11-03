package io.bookingmicroservices.booking.bookings.features.getmybookings;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/booking")
@Tag(name = "booking")
public class GetMyBookingsController {

    private final IMediator mediator;

    public GetMyBookingsController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<BookingDto>> getMyBookings(Authentication authentication) {
        List<BookingDto> result = this.mediator.send(new GetMyBookingsQuery(authentication.getName()));
        return ResponseEntity.ok().body(result);
    }
}
