package org.diploma.app.model.auth;

public class RegistrationException extends RuntimeException {

    public RegistrationException() {}

    public RegistrationException(String message) {
        super(message);
    }
}
