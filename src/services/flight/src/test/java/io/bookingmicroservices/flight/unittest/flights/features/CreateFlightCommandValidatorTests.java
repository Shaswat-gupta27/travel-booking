package io.bookingmicroservices.flight.unittest.flights.features;

import io.bookingmicroservices.flight.flights.features.createflight.CreateFlightCommand;
import io.bookingmicroservices.flight.flights.features.createflight.CreateFlightCommandValidator;
import io.bookingmicroservices.flight.unittest.fakes.CreateFlightCommandFake;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static org.junit.jupiter.api.Assertions.*;

public class CreateFlightCommandValidatorTests {
  // Same way the validation pipeline runs validators.
  private BindingResult validate(CreateFlightCommand command) {
    DataBinder dataBinder = new DataBinder(command);
    dataBinder.setValidator(new CreateFlightCommandValidator());
    dataBinder.validate();
    return dataBinder.getBindingResult();
  }

  @Test
  void should_accept_a_normal_seat_count() {
    assertFalse(validate(CreateFlightCommandFake.generate(180, 6)).hasErrors());
    assertFalse(validate(CreateFlightCommandFake.generate(1000, 10)).hasErrors());
  }

  @Test
  void should_reject_a_missing_zero_or_too_large_seat_count() {
    for (int seats : new int[] {0, -5, 1001}) {
      BindingResult result = validate(CreateFlightCommandFake.generate(seats, 6));
      assertTrue(result.hasFieldErrors("numberOfSeats"), "seats = " + seats);
    }
  }

  @Test
  void should_reject_an_invalid_row_width() {
    for (int perRow : new int[] {0, 11}) {
      BindingResult result = validate(CreateFlightCommandFake.generate(180, perRow));
      assertTrue(result.hasFieldErrors("seatsPerRow"), "perRow = " + perRow);
    }
  }
}
