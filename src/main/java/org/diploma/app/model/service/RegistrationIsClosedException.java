package org.diploma.app.model.service;

public class RegistrationIsClosedException extends Exception {

    public RegistrationIsClosedException() {
        super("Registration is closed");
    }
}
