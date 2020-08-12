package org.diploma.app.model.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostStatus {

    INACTIVE("inactive"),
    PENDING("pending"),
    DECLINED("declined"),
    PUBLISHED("published");

    private String value;

    @JsonCreator
    public static PostStatus fromText(String text) {
        for(PostStatus ps : PostStatus.values()) {
            if (ps.getValue().equals(text))
                return ps;
        }
        throw new IllegalArgumentException("Illegal argument: " + text);
    }
}
