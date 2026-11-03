package io.bookingmicroservices.flight.airports.models;

import io.bookingmicroservices.flight.airports.valueobjects.Location;

import buildingblocks.core.model.AggregateRoot;
import io.bookingmicroservices.flight.airports.features.createairport.AirportCreatedDomainEvent;
import io.bookingmicroservices.flight.airports.valueobjects.Address;
import io.bookingmicroservices.flight.airports.valueobjects.AirportId;
import io.bookingmicroservices.flight.airports.valueobjects.Code;
import io.bookingmicroservices.flight.airports.valueobjects.Name;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Airport extends AggregateRoot<AirportId> {
  Name name;
  Code code;
  Address address;
  Location location;

  public Airport(AirportId airportId, Name name, Code code, Address address, Location location, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModified, Long lastModifiedBy, Long version, boolean isDeleted) {
    this.id = airportId;
    this.name = name;
    this.code = code;
    this.address = address;
    this.location = location;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.lastModified = lastModified;
    this.lastModifiedBy = lastModifiedBy;
    this.version = version;
    this.isDeleted = isDeleted;
  }

  public Airport(AirportId airportId, Name name, Code code, Address address, Location location) {
    this.id = airportId;
    this.name = name;
    this.code = code;
    this.address = address;
    this.location = location;
  }

  public static Airport create(AirportId id, Name name, Code code, Address address, Location location) {
    var airport = new Airport(id, name, code, address, location);

    airport.addDomainEvent(new AirportCreatedDomainEvent(
      airport.id.getAirportId(),
      airport.name.getName(),
      airport.code.getCode(),
      airport.address.getAddress(),
      airport.location.getCity(),
      airport.location.getRegion(),
      airport.location.getCountry(),
      false
    ));

    return airport;
  }
}
