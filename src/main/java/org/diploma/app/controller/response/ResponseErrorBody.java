package org.diploma.app.controller.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ResponseErrorBody extends ResponseDefaultBody {

    @Getter
    private Map<String, String> errors;

    public ResponseErrorBody(Map<String, String> errors) {
        this.errors = errors;
    }

    public ResponseErrorBody(String key, String value) {
        this.errors = new HashMap<>();
        errors.put(key, value);
    }
}
