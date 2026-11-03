package io.bookingmicroservices.passenger.unittest.passengers.features;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bookingmicroservices.passenger.passengers.enums.PassengerType;
import io.bookingmicroservices.passenger.passengers.features.createpassenger.CreatePassengerRequestDto;
import io.bookingmicroservices.passenger.passengers.features.updatepassenger.UpdatePassengerRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreatePassengerRequestTests {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void should_read_passportNumber_in_camel_case_from_the_create_body() throws Exception {
    CreatePassengerRequestDto request = mapper.readValue(
      "{\"name\":\"Jane Doe\",\"passportNumber\":\"P987654321\",\"passengerType\":\"Female\",\"age\":28}", CreatePassengerRequestDto.class);

    assertEquals("P987654321", request.passportNumber());
    assertEquals(PassengerType.Female, request.passengerType());
  }

  @Test
  void should_read_the_update_body() throws Exception {
    UpdatePassengerRequestDto request = mapper.readValue(
      "{\"name\":\"Jane D.\",\"passportNumber\":\"P1\",\"passengerType\":\"Female\",\"age\":29}", UpdatePassengerRequestDto.class);

    assertEquals("P1", request.passportNumber());
    assertEquals(29, request.age());
  }
}
