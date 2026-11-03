package io.bookingmicroservices.flight.airports.valueobjects;

import buildingblocks.utils.validation.ValidationUtils;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor // Required by JPA
@Getter
public class Location {
  private String city;
  private String region;
  private String country;

  public Location(String city, String region, String country) {
    ValidationUtils.notBeNullOrEmpty(city);
    ValidationUtils.notBeNullOrEmpty(country);
    this.city = city;
    this.region = region == null ? "" : region;
    this.country = country;
  }
}
