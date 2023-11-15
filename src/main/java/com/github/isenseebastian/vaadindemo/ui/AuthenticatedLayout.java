package com.github.isenseebastian.vaadindemo.ui;

import com.github.isenseebastian.vaadindemo.auth.AuthenticationService;
import com.github.isenseebastian.vaadindemo.session.TimeoutCounter;
import com.github.isenseebastian.vaadindemo.session.TimeoutScheduler;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.concurrent.Future;

/**
 * AuthenticatedLayout MUST be used for every view that requires an authenticated user.
 * It uses a header bar to show basic information, like the authenticated user, the inactivity session timeout counter
 * and a reset plus logout button. Moreover, it will reset the session timeout counter when a click event occurs.
 */
public class AuthenticatedLayout extends AppLayout {

    private final TimeoutScheduler timeoutScheduler;
    private final TimeoutCounter timeoutCounter;
    private final H3 timeoutText;

    private Runnable logout;
    private Future<?> timeoutScheduledTask;

    public AuthenticatedLayout(AuthenticationService authenticationService,
                               TimeoutScheduler timeoutScheduler,
                               @Value("${vaadindemo.session.timeout:20s}") Duration sessionTimeout) {
        this.timeoutScheduler = timeoutScheduler;

        timeoutCounter = new TimeoutCounter(sessionTimeout.toSecondsPart());
        timeoutText = new H3("");
        updateTimeoutText(sessionTimeout.toSecondsPart());

        var authUser = authenticationService.getAuthenticatedUser();

        if (authUser == null) {
            // Normally, views using AuthenticatedLayout are not accessible without authentication. However, just
            // in case it happens due to some bug, we exit here and navigate to the login page.
            UI.getCurrent().navigate("/login");
            return;
        }

        initLayoutForUser(authenticationService, authUser);
    }

    private void initLayoutForUser(AuthenticationService authenticationService, String authUser) {
        var sessionID = VaadinSession.getCurrent().getSession().getId();
        logout = () -> {
            authenticationService.expireSession(sessionID);
            UI.getCurrent().navigate("/login");
        };

        var userText = new H1(authUser);
        var resetButton = new Button("Reset", event -> resetTimeout());
        var logoutButton = new Button("Logout", event -> logout.run());

        this.addListener(ClickEvent.class, new ComponentEventListener<>() {
            @Override
            public void onComponentEvent(ClickEvent event) {
                resetTimeout();
            }
        });

        var header = new HorizontalLayout(userText, logoutButton, resetButton, timeoutText);
        header.setMargin(true);
        header.setAlignItems(FlexComponent.Alignment.BASELINE);

        addToNavbar(header);
    }

    public void resetTimeout() {
        var remaining = timeoutCounter.reset();
        updateTimeoutText(remaining);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Register our timeout counter callback to run every second until we stop it.
        timeoutScheduledTask = timeoutScheduler.scheduleCallbackEachSecond(() -> update(attachEvent.getUI()));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Stop our timeout counter callback before when we leave this layout.
        stopUpdates();
        super.onDetach(detachEvent);
    }

    private void updateTimeoutText(int timeoutRemaining) {
        timeoutText.setText(String.format("Inactivity logout after: %d s", timeoutRemaining));
    }

    private void update(UI ui) {
        if (timeoutScheduledTask.isDone()) {
            // Shield against additional calls when scheduling has already been aborted (just in case).
            return;
        }

        var remaining = timeoutCounter.decrement();
        ui.access(() -> updateTimeoutText(remaining));

        if (remaining <= 0) {
            // Initiate logout.
            ui.access(() -> logout.run());
            stopUpdates();
        }
    }

    private void stopUpdates() {
        if (timeoutScheduledTask != null) {
            timeoutScheduledTask.cancel(true);
        }
    }
}
