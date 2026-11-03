package io.bookingmicroservices.passenger.passengers.features.createpassenger;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.features.Mappings;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "api/v1/passenger")
@Tag(name = "passenger")
public class PassengerController {

    private final IMediator mediator;

    public PassengerController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PassengerDto> createPassenger(@RequestBody CreatePassengerRequestDto createPassengerRequestDto, Authentication authentication) {
        CreatePassengerCommand command = Mappings.toCreatePassengerCommand(createPassengerRequestDto, authentication.getName());
        var result = this.mediator.send(command);
        return ResponseEntity.ok().body(result);
    }
}

