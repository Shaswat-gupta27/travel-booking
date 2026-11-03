package io.bookingmicroservices.passenger.passengers.features.updatepassenger;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdatePassengerCommandValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdatePassengerCommand.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdatePassengerCommand command = (UpdatePassengerCommand) target;

        if (command.name() == null || command.name().isBlank()) {
            errors.rejectValue("name", "name.required", "Name is required");
        }

        if (command.passportNumber() == null || command.passportNumber().isBlank()) {
            errors.rejectValue("passportNumber", "passportNumber.required", "PassportNumber is required");
        }

        if (command.passengerType() == null) {
            errors.rejectValue("passengerType", "passengerType.invalid", "PassengerType must be Male, Female or Baby");
        }

        if (command.age() <= 0) {
            errors.rejectValue("age", "age.invalid", "Age must be greater than 0");
        }

        if (command.userId() == null || command.userId().isBlank()) {
            errors.rejectValue("userId", "userId.required", "User ID is required");
        }
    }
}
