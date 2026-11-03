package io.bookingmicroservices.flight.aircrafts.features.getaircraftbyid;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/flight/aircraft")
@Tag(name = "flight")
public class GetAircraftByIdController {

  private final IMediator mediator;

  public GetAircraftByIdController(IMediator mediator) {
    this.mediator = mediator;
  }

  @GetMapping("/{id}")
  public ResponseEntity<AircraftDto> getAircraftById(@PathVariable UUID id) {
    AircraftDto result = this.mediator.send(new GetAircraftByIdQuery(id));
    return ResponseEntity.ok().body(result);
  }
}
