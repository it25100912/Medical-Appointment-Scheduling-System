package com.medicalapp.common.util;

import com.medicalapp.auth.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static User.Role getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    public static boolean isPatient() {
        return User.Role.PATIENT == getCurrentUserRole();
    }

    public static boolean isDoctor() {
        return User.Role.DOCTOR == getCurrentUserRole();
    }

    public static boolean isAdmin() {
        return User.Role.ADMIN == getCurrentUserRole();
    }
}
