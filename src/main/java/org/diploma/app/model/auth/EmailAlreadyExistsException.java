package org.diploma.app.model.auth;

public final class EmailAlreadyExistsException extends RegistrationException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
