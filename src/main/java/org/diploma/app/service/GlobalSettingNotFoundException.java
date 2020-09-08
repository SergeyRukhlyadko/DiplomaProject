package org.diploma.app.service;

public class GlobalSettingNotFoundException extends RuntimeException {

    public GlobalSettingNotFoundException(String message) {
        super(message);
    }
}
