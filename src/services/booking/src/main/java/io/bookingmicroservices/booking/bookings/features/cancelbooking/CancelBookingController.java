package io.bookingmicroservices.booking.bookings.features.cancelbooking;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.features.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/booking")
@Tag(name = "booking")
public class CancelBookingController {

    private final IMediator mediator;

    public CancelBookingController(IMediator mediator) {
        this.mediator = mediator;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable UUID id, Authentication authentication) {
        BookingDto result = this.mediator.send(new CancelBookingCommand(id, authentication.getName(), CurrentUser.isAdmin(authentication)));
        return ResponseEntity.ok().body(result);
    }
}
