package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.features.CurrentUser;
import io.bookingmicroservices.passenger.passengers.features.Mappings;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/passenger")
@Tag(name = "passenger")
public class UpdatePassengerController {

    private final IMediator mediator;

    public UpdatePassengerController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PassengerDto> updatePassenger(@PathVariable UUID id, @RequestBody UpdatePassengerRequestDto request, Authentication authentication) {
        UpdatePassengerCommand command = Mappings.toUpdatePassengerCommand(id, request, authentication.getName(), CurrentUser.isAdmin(authentication));
        PassengerDto result = this.mediator.send(command);
        return ResponseEntity.ok().body(result);
    }
}
