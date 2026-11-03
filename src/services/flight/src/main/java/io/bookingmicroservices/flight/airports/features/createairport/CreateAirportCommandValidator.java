package io.bookingmicroservices.flight.airports.features.createairport;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateAirportCommandValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return CreateAirportCommand.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    CreateAirportCommand command = (CreateAirportCommand) target;

    if (command.id() == null) {
      errors.rejectValue("id", "id.required", "Airport ID is required");
    }

    if (command.name() == null) {
      errors.rejectValue("name", "name.required", "Name is required");
    }

    if (command.code() == null) {
      errors.rejectValue("code", "code.required", "Code is required");
    }

    if (command.address() == null) {
      errors.rejectValue("address", "address.required", "Address is required");
    }

    if (command.city() == null || command.city().trim().isEmpty()) {
      errors.rejectValue("city", "city.required", "City is required");
    }

    if (command.country() == null || command.country().trim().isEmpty()) {
      errors.rejectValue("country", "country.required", "Country is required");
    }
  }
}
