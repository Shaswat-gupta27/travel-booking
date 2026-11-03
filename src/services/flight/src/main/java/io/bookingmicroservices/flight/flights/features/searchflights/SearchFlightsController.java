package io.bookingmicroservices.flight.flights.features.searchflights;

import buildingblocks.mediator.abstractions.IMediator;
import io.bookingmicroservices.flight.flights.dtos.FlightSearchPageDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Locale;

@RestController
@RequestMapping(path = "api/v1/flight")
@Tag(name = "flight")
public class SearchFlightsController {

  private final IMediator mediator;

  public SearchFlightsController(IMediator mediator) {
    this.mediator = mediator;
  }

  @GetMapping("/search")
  public ResponseEntity<FlightSearchPageDto> searchFlights(
    @RequestParam(required = false) String from,
    @RequestParam(required = false) String to,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @RequestParam(defaultValue = "departure") String sort,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "20") int size) {
    FlightSearchPageDto result = this.mediator.send(new SearchFlightsQuery(from, to, date, sort.toLowerCase(Locale.ROOT), page, size));
    return ResponseEntity.ok().body(result);
  }
}
