package io.bookingmicroservices.passenger.unittest.passengers.features;

import buildingblocks.testbase.UnitTestBase;
import io.bookingmicroservices.passenger.data.mongo.documents.PassengerDocument;
import io.bookingmicroservices.passenger.data.mongo.repositories.PassengerReadRepository;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;
import io.bookingmicroservices.passenger.passengers.exceptions.PassengerNotFoundException;
import io.bookingmicroservices.passenger.passengers.features.getmypassengers.GetMyPassengersQuery;
import io.bookingmicroservices.passenger.passengers.features.getmypassengers.GetMyPassengersQueryHandler;
import io.bookingmicroservices.passenger.passengers.features.getpassengerbyid.GetPassengerByIdQuery;
import io.bookingmicroservices.passenger.passengers.features.getpassengerbyid.GetPassengerByIdQueryHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PassengerReadTests extends UnitTestBase {
  @Mock
  private PassengerReadRepository passengerReadRepository;

  @InjectMocks
  private GetPassengerByIdQueryHandler getByIdHandler;

  @InjectMocks
  private GetMyPassengersQueryHandler getMineHandler;

  private final UUID passengerId = UUID.randomUUID();

  private PassengerDocument document(UUID id, String userId, String name) {
    return new PassengerDocument(id, userId, name, "P-" + name, PassengerType.Male, 30, false);
  }

  @Test
  public void should_return_my_passenger_by_id() {
    when(passengerReadRepository.findPassengerByPassengerIdAndIsDeletedFalse(passengerId)).thenReturn(document(passengerId, "user-1", "Ann"));

    PassengerDto result = getByIdHandler.handle(new GetPassengerByIdQuery(passengerId, "user-1", false));

    assertEquals("Ann", result.name());
  }

  @Test
  public void should_not_show_someone_elses_passenger() {
    when(passengerReadRepository.findPassengerByPassengerIdAndIsDeletedFalse(passengerId)).thenReturn(document(passengerId, "user-1", "Ann"));

    assertThrows(PassengerNotFoundException.class, () -> getByIdHandler.handle(new GetPassengerByIdQuery(passengerId, "user-2", false)));
  }

  @Test
  public void should_show_any_passenger_to_an_admin_including_ones_without_an_owner() {
    when(passengerReadRepository.findPassengerByPassengerIdAndIsDeletedFalse(passengerId)).thenReturn(document(passengerId, null, "Old"));

    assertEquals("Old", getByIdHandler.handle(new GetPassengerByIdQuery(passengerId, "admin-1", true)).name());
  }

  @Test
  public void should_hide_owner_less_passengers_from_travellers() {
    when(passengerReadRepository.findPassengerByPassengerIdAndIsDeletedFalse(passengerId)).thenReturn(document(passengerId, null, "Old"));

    assertThrows(PassengerNotFoundException.class, () -> getByIdHandler.handle(new GetPassengerByIdQuery(passengerId, "user-1", false)));
  }

  @Test
  public void should_return_not_found_instead_of_failing_for_an_unknown_passenger() {
    when(passengerReadRepository.findPassengerByPassengerIdAndIsDeletedFalse(passengerId)).thenReturn(null);

    assertThrows(PassengerNotFoundException.class, () -> getByIdHandler.handle(new GetPassengerByIdQuery(passengerId, "user-1", false)));
    assertThrows(PassengerNotFoundException.class, () -> getByIdHandler.handle(new GetPassengerByIdQuery(passengerId, null, true)));
  }

  @Test
  public void should_list_only_my_passengers_sorted_by_name() {
    when(passengerReadRepository.findAllByUserIdAndIsDeletedFalse("user-1"))
      .thenReturn(List.of(document(UUID.randomUUID(), "user-1", "zoe"), document(UUID.randomUUID(), "user-1", "Ann")));

    List<PassengerDto> result = getMineHandler.handle(new GetMyPassengersQuery("user-1"));

    assertEquals(List.of("Ann", "zoe"), result.stream().map(PassengerDto::name).toList());
  }
}
