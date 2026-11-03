package io.bookingmicroservices.flight.unittest.flights.features;

import io.bookingmicroservices.flight.flights.features.searchflights.SearchFlightsQuery;
import io.bookingmicroservices.flight.flights.features.searchflights.SearchFlightsQueryValidator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static org.junit.jupiter.api.Assertions.*;

public class SearchFlightsQueryValidatorTests {
  // Same way the validation pipeline runs validators.
  private BindingResult validate(SearchFlightsQuery query) {
    DataBinder dataBinder = new DataBinder(query);
    dataBinder.setValidator(new SearchFlightsQueryValidator());
    dataBinder.validate();
    return dataBinder.getBindingResult();
  }

  @Test
  void should_accept_a_normal_search() {
    assertFalse(validate(new SearchFlightsQuery("Delhi", "California", null, "price", 1, 100)).hasErrors());
  }

  @Test
  void should_require_from_and_to() {
    BindingResult result = validate(new SearchFlightsQuery(" ", null, null, "departure", 1, 20));

    assertTrue(result.hasFieldErrors("from"));
    assertTrue(result.hasFieldErrors("to"));
  }

  @Test
  void should_reject_unknown_sort_and_bad_paging() {
    assertTrue(validate(new SearchFlightsQuery("a", "b", null, "cheapest", 1, 20)).hasFieldErrors("sort"));
    assertTrue(validate(new SearchFlightsQuery("a", "b", null, "price", 0, 20)).hasFieldErrors("page"));
    assertTrue(validate(new SearchFlightsQuery("a", "b", null, "price", 1, 0)).hasFieldErrors("size"));
    assertTrue(validate(new SearchFlightsQuery("a", "b", null, "price", 1, 101)).hasFieldErrors("size"));
  }
}
