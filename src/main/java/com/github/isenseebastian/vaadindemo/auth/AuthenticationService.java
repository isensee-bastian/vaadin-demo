package com.github.isenseebastian.vaadindemo.auth;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * AuthenticationService allows simple access to the currently authenticated username (if present, else null). Moreover,
 * a logout can be triggered by expiring a given session. This is useful for triggering logouts from the server side,
 * e.g. due to an activity timeout.
 */
@Service
public class AuthenticationService {

    private final SessionRegistry sessionRegistry;

    public AuthenticationService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public String getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) context.getAuthentication().getPrincipal()).getUsername();
        }

        // Anonymous or no authentication.
        return null;
    }

    public void expireSession(String sessionID) {
        var session = sessionRegistry.getSessionInformation(sessionID);

        if (session != null) {
            session.expireNow();
        }
    }
}
