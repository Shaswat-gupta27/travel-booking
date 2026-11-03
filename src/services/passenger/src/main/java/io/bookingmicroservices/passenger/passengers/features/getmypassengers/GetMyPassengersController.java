package io.bookingmicroservices.passenger.passengers.features.getmypassengers;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/passenger")
@Tag(name = "passenger")
public class GetMyPassengersController {

    private final IMediator mediator;

    public GetMyPassengersController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<PassengerDto>> getMyPassengers(Authentication authentication) {
        List<PassengerDto> result = this.mediator.send(new GetMyPassengersQuery(authentication.getName()));
        return ResponseEntity.ok().body(result);
    }
}
