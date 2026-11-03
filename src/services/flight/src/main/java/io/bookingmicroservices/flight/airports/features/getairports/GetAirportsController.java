package io.bookingmicroservices.flight.airports.features.getairports;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.flight.airports.dtos.AirportDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/flight/airport")
@Tag(name = "flight")
public class GetAirportsController {

  private final IMediator mediator;

  public GetAirportsController(IMediator mediator) {
    this.mediator = mediator;
  }

  @GetMapping()
  public ResponseEntity<List<AirportDto>> getAirports(@RequestParam(required = false) String q,
                                                      @RequestParam(required = false) String city) {
    List<AirportDto> result = this.mediator.send(new GetAirportsQuery(q, city));
    return ResponseEntity.ok().body(result);
  }
}
