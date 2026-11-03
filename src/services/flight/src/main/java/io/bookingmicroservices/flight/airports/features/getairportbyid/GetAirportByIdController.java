package io.bookingmicroservices.flight.airports.features.getairportbyid;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/flight/airport")
@Tag(name = "flight")
public class GetAirportByIdController {

  private final IMediator mediator;

  public GetAirportByIdController(IMediator mediator) {
    this.mediator = mediator;
  }

  @GetMapping("/{id}")
  public ResponseEntity<AirportDto> getAirportById(@PathVariable UUID id) {
    AirportDto result = this.mediator.send(new GetAirportByIdQuery(id));
    return ResponseEntity.ok().body(result);
  }
}
