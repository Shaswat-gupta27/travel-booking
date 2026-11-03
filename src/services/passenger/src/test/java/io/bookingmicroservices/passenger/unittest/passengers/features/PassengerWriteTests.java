package io.bookingmicroservices.passenger.unittest.passengers.features;

import buildingblocks.testbase.UnitTestBase;
import com.github.f4b6a3.uuid.UuidCreator;
import io.bookingmicroservices.passenger.data.jpa.entities.PassengerEntity;
import io.bookingmicroservices.passenger.data.jpa.repositories.PassengerRepository;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;
import io.bookingmicroservices.passenger.passengers.exceptions.PassengerAlreadyExistException;
import io.bookingmicroservices.passenger.passengers.exceptions.PassengerNotFoundException;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.CreatePassengerCommand;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.CreatePassengerCommandHandler;
import io.bookingmicroservices.passenger.passengers.features.updatepassenger.UpdatePassengerCommand;
import io.bookingmicroservices.passenger.passengers.features.updatepassenger.UpdatePassengerCommandHandler;
import io.bookingmicroservices.passenger.passengers.valueobjects.Age;
import io.bookingmicroservices.passenger.passengers.valueobjects.Name;
import io.bookingmicroservices.passenger.passengers.valueobjects.PassportNumber;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PassengerWriteTests extends UnitTestBase {
  @Mock
  private PassengerRepository passengerRepository;

  @InjectMocks
  private CreatePassengerCommandHandler createHandler;

  @InjectMocks
  private UpdatePassengerCommandHandler updateHandler;

  private final UUID passengerId = UUID.randomUUID();

  private PassengerEntity stored(String userId, String passport) {
    return new PassengerEntity(passengerId, userId, new Name("Jane Doe"), new PassportNumber(passport), PassengerType.Female, new Age(28));
  }

  private CreatePassengerCommand create(String userId, String passport) {
    return new CreatePassengerCommand(UuidCreator.getTimeOrderedEpoch(), "Jane Doe", passport, PassengerType.Female, 28, userId);
  }

  private UpdatePassengerCommand update(String userId, boolean isAdmin, String passport) {
    return new UpdatePassengerCommand(passengerId, "Jane D.", passport, PassengerType.Female, 29, userId, isAdmin);
  }

  @Test
  public void should_create_a_passenger_owned_by_the_creator() {
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P1"))).thenReturn(null);
    when(passengerRepository.save(any(PassengerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PassengerDto result = createHandler.handle(create("user-1", "P1"));

    ArgumentCaptor<PassengerEntity> saved = ArgumentCaptor.forClass(PassengerEntity.class);
    verify(passengerRepository).save(saved.capture());
    assertEquals("user-1", saved.getValue().getUserId());
    assertEquals("user-1", result.userId());
    assertEquals("P1", result.passportNumber());
  }

  @Test
  public void should_refuse_the_same_passport_twice_for_one_owner() {
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P1")))
      .thenReturn(stored("user-1", "P1"));

    assertThrows(PassengerAlreadyExistException.class, () -> createHandler.handle(create("user-1", "P1")));
    verify(passengerRepository, never()).save(any(PassengerEntity.class));
  }

  @Test
  public void should_allow_the_same_passport_for_another_owner() {
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P1")))
      .thenReturn(stored("user-1", "P1"));
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-2", new PassportNumber("P1")))
      .thenReturn(null);
    when(passengerRepository.save(any(PassengerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PassengerDto result = createHandler.handle(create("user-2", "P1"));

    assertEquals("user-2", result.userId());
  }

  @Test
  public void should_update_my_passenger() {
    when(passengerRepository.findPassengerByIdAndIsDeletedFalse(passengerId)).thenReturn(stored("user-1", "P1"));
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P2"))).thenReturn(null);
    when(passengerRepository.save(any(PassengerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PassengerDto result = updateHandler.handle(update("user-1", false, "P2"));

    assertEquals("Jane D.", result.name());
    assertEquals("P2", result.passportNumber());
    assertEquals(29, result.age());
    assertEquals("user-1", result.userId());
  }

  @Test
  public void should_keep_the_same_passport_when_updating_other_fields() {
    PassengerEntity mine = stored("user-1", "P1");
    when(passengerRepository.findPassengerByIdAndIsDeletedFalse(passengerId)).thenReturn(mine);
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P1"))).thenReturn(mine);
    when(passengerRepository.save(any(PassengerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assertEquals("Jane D.", updateHandler.handle(update("user-1", false, "P1")).name());
  }

  @Test
  public void should_refuse_a_passport_already_used_by_another_of_my_passengers() {
    when(passengerRepository.findPassengerByIdAndIsDeletedFalse(passengerId)).thenReturn(stored("user-1", "P1"));
    PassengerEntity other = new PassengerEntity(UUID.randomUUID(), "user-1", new Name("Other"), new PassportNumber("P2"), PassengerType.Male, new Age(40));
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P2"))).thenReturn(other);

    assertThrows(PassengerAlreadyExistException.class, () -> updateHandler.handle(update("user-1", false, "P2")));
    verify(passengerRepository, never()).save(any(PassengerEntity.class));
  }

  @Test
  public void should_not_update_someone_elses_or_a_missing_passenger() {
    when(passengerRepository.findPassengerByIdAndIsDeletedFalse(passengerId)).thenReturn(stored("user-1", "P1"));

    assertThrows(PassengerNotFoundException.class, () -> updateHandler.handle(update("user-2", false, "P2")));

    when(passengerRepository.findPassengerByIdAndIsDeletedFalse(passengerId)).thenReturn(null);
    assertThrows(PassengerNotFoundException.class, () -> updateHandler.handle(update("user-1", false, "P2")));
    verify(passengerRepository, never()).save(any(PassengerEntity.class));
  }

  @Test
  public void should_let_an_admin_update_any_passenger() {
    when(passengerRepository.findPassengerByIdAndIsDeletedFalse(passengerId)).thenReturn(stored("user-1", "P1"));
    when(passengerRepository.findPassengerByUserIdAndPassportNumberAndIsDeletedFalse("user-1", new PassportNumber("P2"))).thenReturn(null);
    when(passengerRepository.save(any(PassengerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PassengerDto result = updateHandler.handle(update("admin-1", true, "P2"));

    assertEquals("user-1", result.userId()); // the owner does not change
  }
}
