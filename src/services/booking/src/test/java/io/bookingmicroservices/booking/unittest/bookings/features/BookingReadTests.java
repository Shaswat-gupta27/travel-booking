package io.bookingmicroservices.booking.unittest.bookings.features;

import buildingblocks.testbase.UnitTestBase;
import io.bookingmicroservices.booking.bookings.dtos.BookingDto;
import io.bookingmicroservices.booking.bookings.enums.BookingStatus;
import io.bookingmicroservices.booking.bookings.exceptions.BookingNotFoundException;
import io.bookingmicroservices.booking.bookings.features.getbookingbyid.GetBookingByIdQuery;
import io.bookingmicroservices.booking.bookings.features.getbookingbyid.GetBookingByIdQueryHandler;
import io.bookingmicroservices.booking.bookings.features.getmybookings.GetMyBookingsQuery;
import io.bookingmicroservices.booking.bookings.features.getmybookings.GetMyBookingsQueryHandler;
import io.bookingmicroservices.booking.data.mongo.documents.BookingDocument;
import io.bookingmicroservices.booking.data.mongo.repositories.BookingReadRepository;
import io.bookingmicroservices.booking.unittest.fakes.BookingFakes;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BookingReadTests extends UnitTestBase {
  @Mock
  private BookingReadRepository bookingReadRepository;

  @InjectMocks
  private GetMyBookingsQueryHandler getMyBookingsQueryHandler;

  @InjectMocks
  private GetBookingByIdQueryHandler getBookingByIdQueryHandler;

  private final UUID bookingId = UUID.randomUUID();

  @Test
  public void should_list_my_bookings_newest_trip_first_including_cancelled() {
    BookingDocument older = BookingFakes.document(UUID.randomUUID(), "user-1", LocalDateTime.of(2026, 11, 1, 10, 0), BookingStatus.Confirmed);
    BookingDocument newer = BookingFakes.document(UUID.randomUUID(), "user-1", LocalDateTime.of(2026, 12, 5, 9, 0), BookingStatus.Cancelled);
    when(bookingReadRepository.findAllByUserIdAndIsDeletedFalse("user-1")).thenReturn(List.of(older, newer));

    List<BookingDto> result = getMyBookingsQueryHandler.handle(new GetMyBookingsQuery("user-1"));

    assertEquals(List.of(newer.getBookingId(), older.getBookingId()), result.stream().map(BookingDto::id).toList());
    assertEquals(BookingStatus.Cancelled, result.getFirst().status());
  }

  @Test
  public void should_return_nothing_for_a_user_without_bookings() {
    when(bookingReadRepository.findAllByUserIdAndIsDeletedFalse("user-2")).thenReturn(List.of());

    assertTrue(getMyBookingsQueryHandler.handle(new GetMyBookingsQuery("user-2")).isEmpty());
  }

  @Test
  public void should_return_my_booking_by_id() {
    BookingDocument booking = BookingFakes.document(bookingId, "user-1", LocalDateTime.of(2026, 11, 1, 10, 0), BookingStatus.Confirmed);
    when(bookingReadRepository.findBookingByBookingIdAndIsDeletedFalse(bookingId)).thenReturn(booking);

    BookingDto result = getBookingByIdQueryHandler.handle(new GetBookingByIdQuery(bookingId, "user-1", false));

    assertEquals(bookingId, result.id());
    assertEquals("12A", result.seatNumber());
    assertNotNull(result.flightId());
  }

  @Test
  public void should_not_show_another_users_booking() {
    BookingDocument booking = BookingFakes.document(bookingId, "user-1", LocalDateTime.of(2026, 11, 1, 10, 0), BookingStatus.Confirmed);
    when(bookingReadRepository.findBookingByBookingIdAndIsDeletedFalse(bookingId)).thenReturn(booking);

    assertThrows(BookingNotFoundException.class,
      () -> getBookingByIdQueryHandler.handle(new GetBookingByIdQuery(bookingId, "user-2", false)));
  }

  @Test
  public void should_show_any_booking_to_an_admin() {
    BookingDocument booking = BookingFakes.document(bookingId, "user-1", LocalDateTime.of(2026, 11, 1, 10, 0), BookingStatus.Confirmed);
    when(bookingReadRepository.findBookingByBookingIdAndIsDeletedFalse(bookingId)).thenReturn(booking);

    assertEquals(bookingId, getBookingByIdQueryHandler.handle(new GetBookingByIdQuery(bookingId, "admin-1", true)).id());
  }

  @Test
  public void should_not_find_a_missing_booking() {
    when(bookingReadRepository.findBookingByBookingIdAndIsDeletedFalse(bookingId)).thenReturn(null);

    assertThrows(BookingNotFoundException.class,
      () -> getBookingByIdQueryHandler.handle(new GetBookingByIdQuery(bookingId, "user-1", false)));
  }
}
