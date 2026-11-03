package io.bookingmicroservices.flight.aircrafts.features.getaircrafts;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.flight.aircrafts.dtos.AircraftDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/flight/aircraft")
@Tag(name = "flight")
public class GetAircraftsController {

  private final IMediator mediator;

  public GetAircraftsController(IMediator mediator) {
    this.mediator = mediator;
  }

  @GetMapping()
  public ResponseEntity<List<AircraftDto>> getAircrafts() {
    List<AircraftDto> result = this.mediator.send(new GetAircraftsQuery());
    return ResponseEntity.ok().body(result);
  }
}
