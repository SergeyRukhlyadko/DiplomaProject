package org.diploma.app.service;

public class RegistrationIsClosedException extends Exception {

    public RegistrationIsClosedException() {
        super("Registration is closed");
    }
}
