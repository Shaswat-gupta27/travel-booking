package io.bookingmicroservices.flight.data.mongo.repositories;

import io.bookingmicroservices.flight.data.mongo.documents.AirportDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface AirportReadRepository extends MongoRepository<AirportDocument, ObjectId> {
  AirportDocument findByAirportIdAndIsDeletedFalse(UUID airportId);
  List<AirportDocument> findAllByIsDeletedFalse();
}

