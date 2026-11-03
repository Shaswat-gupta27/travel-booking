package io.bookingmicroservices.flight.airports.features;

import io.bookingmicroservices.flight.data.mongo.documents.AirportDocument;

import java.util.Locale;

public final class AirportMatcher {
  private AirportMatcher() {
  }

  // True when the text (ignoring case) appears in the airport's name, code, city, region or country; blank text matches all.
  public static boolean matches(AirportDocument airport, String text) {
    if (text == null || text.isBlank()) {
      return true;
    }
    String needle = lower(text.trim());
    return lower(airport.getName()).contains(needle)
      || lower(airport.getCode()).contains(needle)
      || lower(airport.getCity()).contains(needle)
      || lower(airport.getRegion()).contains(needle)
      || lower(airport.getCountry()).contains(needle);
  }

  private static String lower(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }
}
