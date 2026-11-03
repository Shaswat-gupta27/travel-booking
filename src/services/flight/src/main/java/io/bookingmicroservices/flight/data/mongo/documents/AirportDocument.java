package io.bookingmicroservices.flight.data.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "airports")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AirportDocument {
  @Id
  private ObjectId id;
  private UUID airportId;
  private String name;
  private String code;
  private String address;
  private String city;
  private String region;
  private String country;
  private boolean isDeleted;

  public AirportDocument(UUID airportId, String name, String code, String address, String city, String region, String country, boolean isDeleted) {
    this.airportId = airportId;
    this.name = name;
    this.code = code;
    this.address = address;
    this.city = city;
    this.region = region;
    this.country = country;
    this.isDeleted = isDeleted;
  }
}
