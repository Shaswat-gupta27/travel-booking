package io.bookingmicroservices.flight.flights.features.searchflights;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Set;

@Component
public class SearchFlightsQueryValidator implements Validator {
  public static final Set<String> SORTS = Set.of("departure", "price", "duration");
  public static final int MAX_SIZE = 100;

  @Override
  public boolean supports(Class<?> clazz) {
    return SearchFlightsQuery.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    SearchFlightsQuery query = (SearchFlightsQuery) target;

    if (query.from() == null || query.from().isBlank()) {
      errors.rejectValue("from", "from.required", "From is required");
    }

    if (query.to() == null || query.to().isBlank()) {
      errors.rejectValue("to", "to.required", "To is required");
    }

    if (query.sort() == null || !SORTS.contains(query.sort())) {
      errors.rejectValue("sort", "sort.invalid", "Sort must be departure, price or duration");
    }

    if (query.page() < 1) {
      errors.rejectValue("page", "page.invalid", "Page must be 1 or more");
    }

    if (query.size() < 1 || query.size() > MAX_SIZE) {
      errors.rejectValue("size", "size.invalid", "Size must be between 1 and " + MAX_SIZE);
    }
  }
}
