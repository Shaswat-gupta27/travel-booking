package io.bookingmicroservices.passenger.passengers.features.getpassengerbyid;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import io.bookingmicroservices.passenger.passengers.features.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;


@RestController
@RequestMapping(path = "api/v1/passenger")
@Tag(name = "passenger")
public class GetPassengerByIdController {

    private final IMediator mediator;

    public GetPassengerByIdController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PassengerDto> getPassengerById(@PathVariable UUID id, Authentication authentication) {
        PassengerDto result = this.mediator.send(new GetPassengerByIdQuery(id, authentication.getName(), CurrentUser.isAdmin(authentication)));
        return ResponseEntity.ok().body(result);
    }
}

