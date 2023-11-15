package com.github.isenseebastian.vaadindemo.ui;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        var loginForm = new LoginForm();
        loginForm.setAction("login");
        add(loginForm);
    }
}
