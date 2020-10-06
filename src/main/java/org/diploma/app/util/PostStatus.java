package org.diploma.app.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum PostStatus {

    INACTIVE("inactive"),
    PENDING("pending"),
    DECLINED("declined"),
    PUBLISHED("published");

    String value;

    @JsonCreator
    public static PostStatus fromText(String text) {
        for(PostStatus ps : PostStatus.values()) {
            if (ps.getValue().equals(text)) {
                return ps;
            }
        }

        throw new IllegalArgumentException("Illegal argument: " + text);
    }
}
