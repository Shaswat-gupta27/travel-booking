package io.bookingmicroservices.booking.bookings.features.getbookingbyid;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.features.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/booking")
@Tag(name = "booking")
public class GetBookingByIdController {

    private final IMediator mediator;

    public GetBookingByIdController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable UUID id, Authentication authentication) {
        BookingDto result = this.mediator.send(new GetBookingByIdQuery(id, authentication.getName(), CurrentUser.isAdmin(authentication)));
        return ResponseEntity.ok().body(result);
    }
}
