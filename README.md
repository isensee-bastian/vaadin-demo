# Vaadin Demo Project

This is a demo project using Vaadin and Spring Boot. It provides following features:

* Simple login form with dummy users (user / user and admin / admin).
* A demo view containing a table with dummy data.
* A header bar that shows the currently authenticated user, a logout button, an inactivity timeout counter and a reset button for the timeout counter.
* The inactivity timeout counter runs down when no activity happens. In case of a click by the user, it is reset to its original value.
* Once the timer reaches zero, the user is logged out automatically.


## Running the Application

There are two ways to run the application :  using `mvn spring-boot:run` or by running the `Application` class directly from your IDE.
Once startup finished, open your webbrowser at `http://localhost:8080` to see the login window. If port 8080 is already used on your machine,
see the configuration section below for updating your port.

You can use following dummy users for login:
* user / user
* admin / admin


## Configuration

See `src/resources/application.properties for configuration settings.` Following settings might be particulary relevant:

* `server.port`: Adapt this port if port 8080 is already used or shall not be used on your machine.
* `vaadindemo.session.timeout`: Adapt this for your testing purposes as needed. Note that a valid Java duration string needs to be specified. The default is 20s.


## Session Timeout Concept

In order to logout users automatically after the configured inactivity timeout, following approach was chosen:

* `AuthenticatedLayout` serves as a base layout for every view that requires an authenticated user.
* `AuthenticatedLayout` cretes a `TimeoutCounter` that starts at the configured timeout value and is decremented every second by a callback from `TimeoutScheduler`.
* `TimeoutScheduler` is a simple background thread, that runs all registered timeout callbacks each second.
* `TimeoutScheduler` is a singleton that can be shared by views for performaing callbacks. This is more lightweight than spawning an own thread for each view or layout instance.
* If the timeout counter reaches zero, a logout is initiated by invalidating the user session and redirecting to the login page.
* If the user clicks on any UI area `MainLayout` resets the timeout counter.


## Known Issues

Subsequent issues are known but were not investigated further yet:

#### Error message on automatic logout

Subsequent error message appears in the browser window after automatic logout:
> Invalid JSON response from server: This session has been expired (possibly due to multiple concurrent logins being attempted as the same user).

The message is automatically shown by Vaadin. A short investigation has shown that this might be a bug by Vaadin that needs a workaround to be more
user-friendly: [Vaadin Flow Issue 14753](https://github.com/vaadin/flow/issues/14753)

#### Exception in server log on automatic logout

Subsequent exception appears in the server log after automatic logout:
> java.io.IOException: Connection remotely closed for 34ec51ed-4e43-4dfb-a568-9deaf3f4741a
>   at org.atmosphere.websocket.WebSocket.write(WebSocket.java:237) ~[atmosphere-runtime-3.0.3.slf4jvaadin1.jar:3.0.3.slf4jvaadin1]

However, this only happens when using Firefox. Chrome does not show this behavior. Again, this might be Vaadin bug that needs further investigation
to handle it properly (e.g. by using a workaround): [Vaadin Flow Issue 11026](https://github.com/vaadin/flow/issues/11026)
