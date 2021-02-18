package org.diploma.app.security;

import org.springframework.security.core.AuthenticationException;

/*
    Thrown if user not found in {@link SecurityContextHolder}
 */
public class UserNotAuthenticatedException extends AuthenticationException {

    public UserNotAuthenticatedException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserNotAuthenticatedException(String msg) {
        super(msg);
    }
}
