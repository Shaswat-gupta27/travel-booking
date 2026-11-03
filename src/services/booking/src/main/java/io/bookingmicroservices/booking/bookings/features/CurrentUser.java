package io.bookingmicroservices.booking.bookings.features;

import org.springframework.security.core.Authentication;

public final class CurrentUser {
    private CurrentUser() {
    }

    public static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }
}
