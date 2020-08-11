package org.diploma.app.model.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Decision {

    ACCEPT("accept"),
    DECLINE("decline");

    private String value;

    @JsonCreator
    public static Decision fromText(String text) {
        for(Decision d : Decision.values()) {
            if (d.getValue().equals(text))
                return d;
        }
        throw new IllegalArgumentException("Illegal argument: " + text);
    }
}
