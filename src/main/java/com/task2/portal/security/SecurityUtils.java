package com.task2.portal.security;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.task2.portal.enums.Role;
import com.task2.portal.exception.ForbiddenException;
public final class SecurityUtils {
    private SecurityUtils() {}

    public static AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ForbiddenException("Authenticated user context is unavailable");
        }
        return user;
    }

    public static boolean isAdmin() {
        return currentUser().role() == Role.ADMIN;
    }

    public static void requireCustomerAccess(Long customerId) {
        AuthenticatedUser user = currentUser();
        if (user.role() == Role.ADMIN) {
            return;
        }
        if (user.customerId() == null || !user.customerId().equals(customerId)) {
            throw new ForbiddenException("CUSTOMER users may only access their own orders and tickets");
        }
    }
}
