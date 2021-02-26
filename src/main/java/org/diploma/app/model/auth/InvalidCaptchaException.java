package org.diploma.app.model.auth;

public final class InvalidCaptchaException extends RuntimeException {

    public InvalidCaptchaException(String message) {
        super(message);
    }
}
