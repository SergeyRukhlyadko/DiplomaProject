package org.diploma.app.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum Decision {

    ACCEPT("accept"),
    DECLINE("decline");

    String value;

    @JsonCreator
    public static Decision fromText(String text) {
        for(Decision d : Decision.values()) {
            if (d.getValue().equals(text)) {
                return d;
            }
        }

        throw new IllegalArgumentException("Illegal argument: " + text);
    }
}
