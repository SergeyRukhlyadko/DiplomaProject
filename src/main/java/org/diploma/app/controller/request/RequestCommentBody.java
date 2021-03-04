package org.diploma.app.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestCommentBody {

    @JsonProperty("parent_id")
    int parentId;

    @JsonProperty("post_id")
    int postId;

    @NotBlank
    String text;
}
