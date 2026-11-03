package io.bookingmicroservices.flight.unittest.aircrafts.features;

import io.bookingmicroservices.flight.aircrafts.features.createaircraft.CreateAircraftCommand;
import io.bookingmicroservices.flight.aircrafts.features.createaircraft.CreateAircraftCommandValidator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CreateAircraftCommandValidatorTests {
  // Same way the validation pipeline runs validators.
  private BindingResult validate(int year) {
    DataBinder dataBinder = new DataBinder(new CreateAircraftCommand(UUID.randomUUID(), "Airbus A320", "A320-200", year));
    dataBinder.setValidator(new CreateAircraftCommandValidator());
    dataBinder.validate();
    return dataBinder.getBindingResult();
  }

  @Test
  void should_accept_a_real_manufacturing_year() {
    assertFalse(validate(2018).hasErrors());
    assertFalse(validate(1900).hasErrors());
  }

  @Test
  void should_reject_a_year_before_1900() {
    assertTrue(validate(1899).hasFieldErrors("manufacturingYear"));
  }
}
