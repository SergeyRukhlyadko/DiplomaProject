package org.diploma.app.model.auth;

public final class IncorrectCaptchaException extends RuntimeException {

    public IncorrectCaptchaException(String message) {
        super(message);
    }
}
